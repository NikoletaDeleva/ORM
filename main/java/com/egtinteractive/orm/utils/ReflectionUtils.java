package com.egtinteractive.orm.utils;

import java.lang.reflect.Field;
import java.sql.ResultSet;

import static java.lang.reflect.Modifier.*;

import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.egtinteractive.orm.annotations.Column;
import com.egtinteractive.orm.annotations.Entity;
import com.egtinteractive.orm.annotations.Id;
import com.egtinteractive.orm.annotations.Table;
import com.egtinteractive.orm.annotations.Transient;
import com.egtinteractive.orm.exceptions.ORMManagerException;

public final class ReflectionUtils {
    private static final List<Class<?>> ALLOWED_CLASSES = Arrays.asList(byte.class, short.class, int.class, long.class,
	    float.class, double.class, char.class, boolean.class, Byte.class, Short.class, Integer.class, Long.class,
	    Float.class, Double.class, Character.class, Boolean.class, String.class, java.util.Date.class,
	    java.sql.Date.class, java.math.BigDecimal.class, java.math.BigInteger.class, java.sql.Timestamp.class);

    public static <E> void validate(final Class<?> clazz) {
	if (clazz == null) {
	    throw new IllegalArgumentException("Null class");
	} else if (!clazz.isAnnotationPresent(Entity.class)) {
	    throw new IllegalArgumentException("@Entity not found");
	}
    }

    public static <E> Map<String, Field> getColumnToFieldMap(final Class<?> clazz) {

	final List<Field> fields = new ArrayList<Field>();

	Class<?> tempClass = clazz;

	while (tempClass != Object.class) {
	    fields.addAll(Arrays.asList(tempClass.getDeclaredFields()));
	    tempClass = tempClass.getSuperclass();
	}

	final Map<String, Field> mapColumnToField = new HashMap<>();

	for (final Field f : fields) {
	    final int modifiers = f.getModifiers();
	    if (!f.isAnnotationPresent(Transient.class) && !isStatic(modifiers) && !isTransient(modifiers)) {
		mapColumnToField.put(getColumnName(f), f);
	    }
	}

	return mapColumnToField;
    }

    private static String getColumnName(final Field f) {
	isAllowed(f);
	if (f.isAnnotationPresent(Column.class)) {
	    final Column column = f.getAnnotation(Column.class);

	    return (column.name().trim().equals("")) ? f.getName() : column.name();
	}
	return f.getName();
    }

    private static void isAllowed(final AccessibleObject f) {
	final Class<?> foundClass = ((Field) f).getType();

	if (!ALLOWED_CLASSES.contains(foundClass)) {
	    throw new IllegalArgumentException("The type of field is not allowed!");
	}
    }

    public static String getColumnNamesFromFields(final Map<String, Field> mapColumnField) {
	final StringBuilder names = new StringBuilder();
	final Set<String> keys = mapColumnField.keySet();
	for (String string : keys) {
	    names.append(string).append(",");
	}
	names.delete(names.length() - 1, names.length());
	return names.toString();
    }

    public static String getTableName(final Class<?> classGen, final String schema) {
	if (!classGen.isAnnotationPresent(Table.class)) {
	    return classGen.getSimpleName();
	}
	final Table table = classGen.getAnnotation(Table.class);

	if (!table.schema().equals("") && !table.schema().equals(schema)) {
	    throw new IllegalArgumentException(String.format("table: %s != %s", schema, table.schema()));
	}
	if(!table.name().equals("") && !table.schema().equals("")) {
	    return (table.schema() + "." + table.name());
	}
	return (table.name().equals("")) ? classGen.getSimpleName() : table.name();
    }

    public static <E> E getEntity(final Class<?> clazz, final ResultSet resultSet,
	    final Map<String, Field> mapColumnField) {
	try {
	    @SuppressWarnings("unchecked")
	    final E instance = (E) clazz.newInstance();

	    for (final String key : mapColumnField.keySet()) {
		final Field f = mapColumnField.get(key);
		f.setAccessible(true);
		f.set(instance, resultSet.getObject(key));
	    }
	    return instance;
	} catch (Exception e) {
	    throw new ORMManagerException(e);
	}

    }

    public static <E> String findPrimaryKeyField(final Class<?> clazz) {

	final List<Field> fields = Arrays.asList(clazz.getDeclaredFields());

	for (final Field f : fields) {
	    if (f.isAnnotationPresent(Id.class)) {
		if (f.isAnnotationPresent(Column.class) && !f.getAnnotation(Column.class).name().equals("")) {
		    return f.getAnnotation(Column.class).name();
		} else {
		    return f.getName();
		}
	    }
	}
	throw new IllegalArgumentException("No primary key");
    }
}

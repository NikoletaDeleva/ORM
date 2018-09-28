package com.egtinteractive.orm.utils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.lang.reflect.Modifier.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.egtinteractive.orm.annotations.AllowedClasses;
import com.egtinteractive.orm.annotations.Column;
import com.egtinteractive.orm.annotations.Entity;
import com.egtinteractive.orm.annotations.Id;
import com.egtinteractive.orm.annotations.Table;
import com.egtinteractive.orm.annotations.Transient;
import com.egtinteractive.orm.exceptions.ColumnNameNotFoundException;

public final class ReflectionUtils {

    public static <E> void validate(Class<?> classGen) {
	if (classGen == null) {
	    throw new IllegalArgumentException();
	} else if (classGen == Class.class) {
	    throw new IllegalArgumentException();
	} else if (!classGen.isAnnotationPresent(Entity.class)) {
	    throw new IllegalArgumentException("@Entity not found");
	}
    }

    public static <E> Map<String, Field> getColumnToFieldMap(final Class<?> classGen) {

	final List<Field> fields = Arrays.asList(classGen.getDeclaredFields());

	final Map<String, Field> mapColumnToField = new HashMap<>();

	for (final Field f : fields) {
	    try {
		mapColumnToField.put(getColumnName(f), f);
	    } catch (ColumnNameNotFoundException e) {
		System.out.println(f.getName() + ": " + e.getMessage());
	    }
	}

	return mapColumnToField;
    }

    private static String getColumnName(final Field f) throws ColumnNameNotFoundException {
	final int modifiers = f.getModifiers();
	if (f.isAnnotationPresent(Transient.class) || isStatic(modifiers) || isTransient(modifiers)) {
	    throw new ColumnNameNotFoundException("Field: @Transient");
	}

	if (f.isAnnotationPresent(Column.class)) {
	    if (f.isAnnotationPresent(Id.class)) {
		final Id id = f.getAnnotation(Id.class);
		isAllowed(f, id);
	    }

	    final Column column = f.getAnnotation(Column.class);
	    isAllowed(f, column);
	    return (column.name().trim().equals("")) ? f.getName() : column.name();

	}
	return f.getName();
    }

    private static void isAllowed(final AccessibleObject f, final Annotation f2) {
	if (f2.annotationType().isAnnotationPresent(AllowedClasses.class) && f.isAnnotationPresent(Id.class)) {
	    final AllowedClasses allowed = f2.annotationType().getAnnotation(AllowedClasses.class);
	    final List<Class<?>> allowedClasses = Arrays.asList(allowed.allowedClasses());
	    final Class<?> foundClass = ((Field) f).getType();

	    if (!allowedClasses.contains(foundClass)) {
		throw new IllegalArgumentException(String.format("ID type: %s not in: %s", foundClass, allowedClasses));
	    }
	}
    }

    public static String getColumnNamesFromFields(final Map<String, Field> map) {
	final StringBuilder names = new StringBuilder(map.keySet().toString());
	names.delete(0, 1).delete(names.length() - 1, names.length());
	return names.toString();
    }

    public static String getTableName(final Class<?> classGen) {
	if (!classGen.isAnnotationPresent(Table.class)) {
	    return classGen.getSimpleName();
	}
	final Table table = classGen.getAnnotation(Table.class);

	return (table.name().equals("")) ? classGen.getSimpleName() : table.name();

    }

    public static <E> E getEntity(Class<?> classGen, ResultSet resultSet, Map<String, Field> map) {
	E instance;
	try {
	    instance = (E) classGen.newInstance();
	    for (final String s : map.keySet()) {
		final Field f = map.get(s);
		f.setAccessible(true);
		f.set(instance, resultSet.getObject(s));
	    }
	    return instance;
	} catch (InstantiationException | IllegalAccessException e) {
	    throw new IllegalArgumentException();
	} catch (IllegalArgumentException | SQLException e) {
	    throw new IllegalArgumentException();
	}

    }
}

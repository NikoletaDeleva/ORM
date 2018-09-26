package utils;

import static java.lang.reflect.Modifier.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import exceptions.ColumnNameNotFoundException;

public class ReflectiveUtils {

    public ReflectiveUtils() {

    }

    public static Map<String, Field> getMapColumnField(final Class<?> classGen) {
	validateClass(classGen);
	final List<Field> fields = Arrays.asList(classGen.getDeclaredFields());
	final List<Method> methods = Arrays.asList(classGen.getDeclaredMethods());
	final Map<String, Field> map = new HashMap<>();
	for (final Field f : fields) {
	    try {
		map.put(getColumnName(f, methods), f);
	    } catch (ColumnNameNotFoundException e) {
		System.out.println(f.getName() + ": " + e.getMessage());
	    }
	}
	return map;
    }

    private static String getColumnName(Field f, List<Method> methods) throws ColumnNameNotFoundException {
	final int modifier = f.getModifiers();
	if (f.isAnnotationPresent(Transient.class) || isStatic(modifier) || isTransient(modifier)) {
	    throw new ColumnNameNotFoundException("@Transient");
	}
	if (f.isAnnotationPresent(Id.class)) {
	    final Id id = f.getAnnotation(Id.class);
	    isAllowed(f, id);
	    if (f.isAnnotationPresent(Column.class)) {
		return isColumn(f, f);
	    }

	} else if (f.isAnnotationPresent(Column.class)) {
	    return isColumn(f, f);
	}

	final StringBuilder methodName = new StringBuilder(
		(f.getType() == Boolean.class || f.getType() == boolean.class) ? "is" : "get")
			.append(f.getName().substring(0, 1).toUpperCase()).append(f.getName().substring(1));

	for (final Method m : methods) {
	    if (m.getName().equals(methodName.toString())) {
		if (m.isAnnotationPresent(Transient.class)) {
		    throw new ColumnNameNotFoundException("@Transient");
		} else if (m.isAnnotationPresent(Column.class)) {
		    return isColumn(f, f);
		} else {
		    return f.getName();
		}
	    }
	}
	throw new ColumnNameNotFoundException("@Id or @Column not found");
    }

    private static String isColumn(final AccessibleObject f, Field f2) {
	final Column column = f.getAnnotation(Column.class);
	isAllowed(f, column);
	return (column.name().equals("")) ? f2.getName() : column.name();
    }

    private static void isAllowed(final AccessibleObject accObj, final Annotation ann) {
	if (ann.annotationType().isAnnotationPresent(AllowedClasses.class) && accObj.isAnnotationPresent(Id.class)) {
	    final AllowedClasses allowed = ann.annotationType().getAnnotation(AllowedClasses.class);
	    final List<Class<?>> allowedClass = Arrays.asList(allowed.allowedClasses());
	    final Class<?> found;

	    if (accObj instanceof Field) {
		found = ((Field) accObj).getType();
	    } else if (accObj instanceof Member) {
		found = ((Method) accObj).getReturnType();
	    } else {
		throw new IllegalArgumentException("null");
	    }
	    if (!allowedClass.contains(found)) {
		throw new IllegalArgumentException();
	    }
	}

    }

    private static void validateClass(Class<?> classGen) {
	if (classGen == null) {
	    throw new IllegalArgumentException("cls: null");
	} else if (classGen == Class.class) {
	    throw new IllegalArgumentException("cls: Class.class");
	} else if (!classGen.isAnnotationPresent(Entity.class)) {
	    throw new IllegalArgumentException("@Entity not found");
	}
    }

    public static <E> String getTableName(final Class<E> classGen, final String schema) {
	validateClass(classGen);
	if (!classGen.isAnnotationPresent(Table.class)) {
	    return classGen.getSimpleName();
	}
	final Table table = classGen.getAnnotation(Table.class);
	if (!table.schema().equals("") && !table.schema().equals(schema)) {
	    throw new IllegalArgumentException(schema);
	}
	return (table.name().equals("")) ? classGen.getSimpleName() : table.name();
    }

    public static <E> E getEntity(Class<E> classGen, Map<String, Field> mapColumnField, ResultSet result) {
	try {
	    final E instance = classGen.newInstance();
	    for (final String s : mapColumnField.keySet()) {
		final Field f = mapColumnField.get(s);
		f.setAccessible(true);
		f.set(instance, result.getObject(s));
	    }
	    return instance;
	} catch (final InstantiationException e) {
	    throw new IllegalArgumentException();
	} catch (IllegalAccessException | IllegalArgumentException | SQLException ie) {
	    throw new IllegalArgumentException(ie);
	}
    }

}

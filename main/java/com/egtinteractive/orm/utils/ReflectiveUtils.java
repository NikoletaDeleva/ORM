package com.egtinteractive.orm.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.egtinteractive.orm.annotations.Id;
import com.egtinteractive.orm.annotations.Table;
import com.egtinteractive.orm.exceptions.ORMmanegerException;

public final class ReflectiveUtils {

    public ReflectiveUtils() {

    }

    public static void validateAnnotations(Class<?> classGen, List<Class<? extends Annotation>> requiredAnnotations) {
	if (!hasAnnotations(classGen, requiredAnnotations)) {
	    throw new IllegalArgumentException();
	}
	if (!hasOnlyOneFieldAnnotatedWith(classGen, Id.class)) {
	    throw new IllegalArgumentException();
	}
    }

    public static boolean hasAnnotations(final Class<?> classGen,
	    final List<Class<? extends Annotation>> annotationTypes) {
	for (final Class<? extends Annotation> annotationType : annotationTypes) {
	    if (!classGen.isAnnotationPresent(annotationType)) {
		return false;
	    }
	}
	return true;
    }

    public static boolean hasOnlyOneFieldAnnotatedWith(final Class<?> entityType,
	    final Class<? extends Annotation> annotationType) {
	final Field[] fields = entityType.getDeclaredFields();
	int count = 0;
	for (final Field field : fields) {
	    if (field.isAnnotationPresent(annotationType)) {
		count++;
	    }
	}
	return count == 1;
    }

    public static <E> Map<String, String> getColumnToFieldMap(final Class<?> entityType,
	    final Class<? extends Annotation> annotationType, final Class<? extends Annotation> withoutAnnotationType,
	    final String propertyName) {
	final Field[] fields = entityType.getDeclaredFields();
	final int fieldCount = fields.length;
	final Map<String, String> columnToFieldMap = new HashMap<>();

	for (int index = 0; index < fieldCount; index++) {
	    final Field field = fields[index];
	    if (!isFieldAssociatedWithColumn(field, annotationType, withoutAnnotationType)) {
		continue;
	    }
	    final String columnName = getAnnotationProperty(field, annotationType, propertyName, field.getName());
	    columnToFieldMap.put(columnName, field.getName());
	}
	return columnToFieldMap;
    }

    static boolean isFieldAssociatedWithColumn(final Field field, final Class<? extends Annotation> withAnnotationType,
	    final Class<? extends Annotation> withoutAnnotationType) {
	return field.isAnnotationPresent(withAnnotationType) && !field.isAnnotationPresent(withoutAnnotationType);
    }

    public static String getAnnotationProperty(final AnnotatedElement annotatedElement,
	    final Class<? extends Annotation> annotationType, final String propertyName, final String defaultValue) {

	if (!annotatedElement.isAnnotationPresent(annotationType)) {
	    return defaultValue;
	}
	try {
	    final Method propertyGetMethod = annotationType.getDeclaredMethod(propertyName, new Class[] {});
	    setAccessible(propertyGetMethod, propertyGetMethod.getModifiers());
	    final Annotation annotation = annotatedElement.getAnnotation(annotationType);
	    final String value = (String) propertyGetMethod.invoke(annotation, new Object[] {});
	    if (value.equals("")) {
		return defaultValue;
	    }
	    return value;
	} catch (final Exception e) {
	    throw new ORMmanegerException();
	}
    }

    public static <E> String getColumnNamesFromFields(final Class<?> entityType,
	    final Class<? extends Annotation> withAnnotationType,
	    final Class<? extends Annotation> withoutAnnotationType, final String propertyName) {
	final Field[] fields = entityType.getDeclaredFields();
	final int fieldCount = fields.length;
	final StringBuilder columnNames = new StringBuilder();

	for (int i = 0; i < fieldCount; i++) {
	    final Field field = fields[i];
	    if (!isFieldAssociatedWithColumn(field, withAnnotationType, withoutAnnotationType)) {
		continue;
	    }
	    final String columnName = getAnnotationProperty(field, withAnnotationType, propertyName, field.getName());
	    columnNames.append(columnName);
	    if (i < (fieldCount - 1)) {
		columnNames.append(", ");
	    }
	}
	return columnNames.toString();
    }

    public static String getTableName(Class<?> classGen) {
	final String defaultTable = classGen.getSimpleName();
	return getAnnotationProperty(classGen, Table.class, "name", defaultTable);
    }

    public static <E> E getEntityFromRecord(final Class<?> classGen, final ResultSet resultSet,
	    final Map<String, String> columnToFieldMap) throws SQLException {
	E newEntity = (E) getEntity(classGen);
	final ResultSetMetaData metaData = resultSet.getMetaData();
	for (int i = 1; i <= metaData.getColumnCount(); i++) {
	    final String columnName = metaData.getColumnName(i);
	    final String fieldName = columnToFieldMap.get(columnName);

	    final Object fieldValue = resultSet.getObject(i);
	    setField(newEntity, fieldName, fieldValue);
	}
	return newEntity;
    }

    public static <E> E getEntity(final Class<E> entityType) {
	try {
	    return entityType.newInstance();
	} catch (final Exception e) {
	    throw new ORMmanegerException();
	}
    }

    public static void setField(final Object entity, final String fieldName, final Object fieldValue) {
	try {
	    final Field field = entity.getClass().getDeclaredField(fieldName);
	    setAccessible(field, field.getModifiers());
	    field.set(entity, fieldValue);
	} catch (final Exception e) {
	    throw new ORMmanegerException();
	}
    }

    public static void setAccessible(final AccessibleObject accessibleObject, final int modifiers) {
	if (!accessibleObject.isAccessible() && !Modifier.isPublic(modifiers)) {
	    accessibleObject.setAccessible(true);
	}
    }

}

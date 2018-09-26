package utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.ORMmanegerException;

public final class ReflectiveUtils {

    public ReflectiveUtils() {

    }

    public static <E> E getEntity(final Class<E> entityType) {
	try {
	    return entityType.newInstance();
	} catch (final Exception e) {
	    throw new ORMmanegerException();
	}
    }

    public static String getAnnotationProperty(final AnnotatedElement annotatedElement, final Class<? extends Annotation> annotationType,
	    final String propertyName, final String defaultValue) {

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

    public static <E> String getColumnNamesFromFields(final Class<?> entityType, final Class<? extends Annotation> withAnnotationType,
	    final Class<? extends Annotation> withoutAnnotationType, final String propertyName) {
	final Field[] fields = entityType.getDeclaredFields();
	final int fieldCount = fields.length;
	final StringBuilder columnNames = new StringBuilder();

	for (int i = 0; i < fieldCount; i++) {
	    final Field field = fields[i];
	    if (!isFieldAssociatedWithColumn(field, withAnnotationType, withoutAnnotationType)) {
		continue;
	    }
	    final String columnName = getColumnName(field, withAnnotationType, propertyName);
	    columnNames.append(columnName);
	    if (i < (fieldCount - 1)) {
		columnNames.append(", ");
	    }
	}
	return columnNames.toString();
    }

    public static <E> Map<String, String> getColumnToFieldMap(final Class<?> entityType, final Class<? extends Annotation> withAnnotationType,
	    final Class<? extends Annotation> withoutAnnotationType, final String propertyName) {
	final Field[] fields = entityType.getDeclaredFields();
	final int fieldCount = fields.length;
	final Map<String, String> columnToFieldMap = new HashMap<>();

	for (int i = 0; i < fieldCount; i++) {
	    final Field field = fields[i];
	    if (!isFieldAssociatedWithColumn(field, withAnnotationType, withoutAnnotationType)) {
		continue;
	    }
	    final String columnName = getColumnName(field, withAnnotationType, propertyName);
	    columnToFieldMap.put(columnName, field.getName());
	}
	return columnToFieldMap;
    }

    private static String getColumnName(final Field field, final Class<? extends Annotation> annotationType, final String propertyName) {
	return getAnnotationProperty(field, annotationType, propertyName, field.getName());
    }

    public static boolean hasAnnotations(final Class<?> entityType, final List<Class<? extends Annotation>> requiredAnnotationTypes) {
	for (final Class<? extends Annotation> annotationType : requiredAnnotationTypes) {
	    if (!entityType.isAnnotationPresent(annotationType)) {
		return false;
	    }
	}
	return true;
    }

    public static boolean hasOnlyOneFieldAnnotatedWith(final Class<?> entityType, final Class<? extends Annotation> annotationType) {
	final Field[] fields = entityType.getDeclaredFields();
	int count = 0;
	for (final Field field : fields) {
	    if (field.isAnnotationPresent(annotationType)) {
		count++;
	    }
	}
	return count == 1;
    }

    static boolean isFieldAssociatedWithColumn(final Field field, final Class<? extends Annotation> withAnnotationType,
	    final Class<? extends Annotation> withoutAnnotationType) {
	return field.isAnnotationPresent(withAnnotationType) && !field.isAnnotationPresent(withoutAnnotationType);
    }

    public static void setAccessible(final AccessibleObject accessibleObject, final int modifiers) {
	if (!accessibleObject.isAccessible() && !Modifier.isPublic(modifiers)) {
	    accessibleObject.setAccessible(true);
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

}

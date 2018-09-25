package com.egtinteractive.orm.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
@Inherited
@Documented
public @interface AllowedClasses {
    Class<?>[] allowedClasses() default { byte.class, short.class, int.class, long.class, float.class, double.class,
	    char.class, boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
	    Character.class, Boolean.class, String.class, java.util.Date.class, java.sql.Date.class,
	    java.math.BigDecimal.class, java.math.BigInteger.class, java.sql.Timestamp.class };
}

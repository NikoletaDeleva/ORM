package com.egtinteractive.orm.exceptions;

/*
 * TODO not used anywhere
 * 
 */
public class ColumnNameNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public ColumnNameNotFoundException() {
    }

    public ColumnNameNotFoundException(String message) {
	super(message);
    }

    public ColumnNameNotFoundException(String message, Throwable throwable) {
	super(message, throwable);
    }

    public ColumnNameNotFoundException(Throwable throwable) {
	super(throwable);
    }

}

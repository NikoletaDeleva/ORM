package com.egtinteractive.orm.exceptions;


public class ORMmanegerException extends RuntimeException {

    private static final long serialVersionUID = 3466291134086693361L;

    public ORMmanegerException(Exception e) {
	super(e);
    }

}

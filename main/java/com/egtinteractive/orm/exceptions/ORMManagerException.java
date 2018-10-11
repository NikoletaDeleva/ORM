package com.egtinteractive.orm.exceptions;

/*
 * TODO wrong spelling 
 */
public class ORMManagerException extends RuntimeException {

    private static final long serialVersionUID = 3466291134086693361L;

    public ORMManagerException(Exception e) {
	super(e);
    }

}

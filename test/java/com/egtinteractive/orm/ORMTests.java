package com.egtinteractive.orm;

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.egtinteractive.orm.classes.*;
import com.egtinteractive.orm.utils.DBCredentials;
import com.egtinteractive.orm.utils.ORM;

public class ORMTests {
    @Test(dataProvider = "getTestData")
    public void testFindAll(final Class<Employee> empl) {

	DBCredentials credentials = new DBCredentials();

	final ORM orm = ORM.getConnectionAndCraete(credentials);

	List<?> entityList = orm.findAll(empl);

	boolean classMatch = true;
	for (final Object entity : entityList) {
	    System.out.println(entity);
	    if (!empl.isInstance(entity)) {
		classMatch = false;
		break;
	    }
	}
	assertTrue(classMatch);
    }

    @DataProvider
    public Object[][] getTestData() {
	return new Object[][] {
	    { Employee.class }, 
	    { EmployeeWithManyId.class }, 
	    { EmployeeWithoutEntity.class },
	    { EmployeeWithoutEntityTable.class }, 
	    { EmployeeWithoutEntityTableId.class },
	    { EmployeeWithoutId.class }, 
	    { EmployeeWithoutTable.class }
	};
    }
}
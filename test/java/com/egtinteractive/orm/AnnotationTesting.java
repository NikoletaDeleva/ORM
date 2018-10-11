package com.egtinteractive.orm;

import static com.egtinteractive.orm.utils.ReflectionUtils.getTableName;
import static org.junit.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;

import java.io.File;

import org.testng.annotations.Test;

import com.egtinteractive.orm.annotations.Table;
import com.egtinteractive.orm.classes.Employee;
import com.egtinteractive.orm.classes.EmployeeWithoutEntity;
import com.egtinteractive.orm.classes.EmployeeWithoutTable;
import com.egtinteractive.orm.utils.DBCredentials;
import com.egtinteractive.orm.utils.MyORM;

public class AnnotationTesting {
    @Test
    public void testNoEntity() {
	final String CONFIG_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator
		+ "configuration.properties";
	final DBCredentials credentials = new DBCredentials(CONFIG_PATH);
	Throwable actualExceptionFirst = null;

	try (MyORM orm = MyORM.getConnectionAndCreate(credentials)) {
	    orm.find(EmployeeWithoutEntity.class, "5");
	} catch (Exception e) {
	    actualExceptionFirst = e;
	}

	/*
	 * TODO you should also test the messages of the exceptions
	 */
	assertNotNull(actualExceptionFirst);
	assertEquals(actualExceptionFirst.getClass(), IllegalArgumentException.class);
    }

    @Test
    public void testNoEntityTwo() {
	final String CONFIG_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator
		+ "configuration.properties";
	final DBCredentials credentials = new DBCredentials(CONFIG_PATH);
	Throwable actualExceptionSecond = null;

	try (MyORM orm = MyORM.getConnectionAndCreate(credentials)) {
	    orm.findAll(EmployeeWithoutEntity.class);
	} catch (Exception e) {
	    actualExceptionSecond = e;
	}

	assertNotNull(actualExceptionSecond);
	assertEquals(actualExceptionSecond.getClass(), IllegalArgumentException.class);

    }

    @Test
    public void testTableName() {
	assertEquals(getTableName(EmployeeWithoutTable.class,""), EmployeeWithoutTable.class.getSimpleName());
	assertEquals(getTableName(Employee.class, ""), Employee.class.getAnnotation(Table.class).name());
    }

}

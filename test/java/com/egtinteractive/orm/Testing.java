package com.egtinteractive.orm;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.egtinteractive.orm.classes.Employee;
import com.egtinteractive.orm.utils.ORM;

public class Testing {
    @DataProvider(name = "empl")
    public Object[][] getData() {
	return MyDataProvider.insertEmployees();
    }

    @Test(dataProvider = "empl")
    public void findAllTest(final ORM orm, final List<Employee> empl) {
	final List<Employee> list = orm.findAll(Employee.class);
	for (final Employee e : list) {
	    assertTrue(empl.contains(e));
	}
	assertEquals(list.size(), empl.size());
    }

    @Test(dataProvider = "empl")
    public void findTest(final ORM orm, final List<Employee> empl) {
	final Employee e = orm.find(Employee.class, "5");
	System.out.println(e);
	assertTrue(empl.contains(e));

    }
}

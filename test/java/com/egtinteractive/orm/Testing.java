package com.egtinteractive.orm;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.egtinteractive.orm.utils.DBCredentials;
import com.egtinteractive.orm.utils.ORM;

public class Testing {

    @DataProvider(name = "employees")
    public Object[][] insertCars() {
	DBCredentials credentials = new DBCredentials();
	final ORM orm = ORM.getConnectionAndCraete(credentials);
	final int count = ThreadLocalRandom.current().nextInt(10, 30);
	Connection con = orm.getConnection();
	try (Statement st = orm.getConnection().createStatement()) {
	    st.execute("DROP TABLE EMPLOYEE;");
	    st.execute(
		    "CREATE TABLE IF NOT EXISTS EMPLOYEE(id INT PRIMARY KEY AUTO_INCREMENT, first_name varchar(10), last_name varchar(10), salary int);");
	} catch (SQLException e1) {
	    e1.printStackTrace();
	}

	final List<Employee> insertedEmployees = new ArrayList<>();
	final String sqlInserteEmployee = "INSERT INTO EMPLOYEE(first_name, last_name, salary) VALUES(?,?,?)";
	try (PreparedStatement ps = con.prepareStatement(sqlInserteEmployee,PreparedStatement.RETURN_GENERATED_KEYS);) {
	    for (int i = 0; i < count; i++) {

		final Employee employee = new Employee();
		
		employee.setFirstName("niki" + i);
		employee.setLastName("deleva" + i);
		employee.setSalary(i * 1000);

		ps.setString(1, employee.getFirstName());
		ps.setString(2, employee.getLastName());
		ps.setInt(3, employee.getSalary());

		ps.executeUpdate();
		final ResultSet rs = ps.getGeneratedKeys();
		rs.next();
		employee.setId(rs.getInt(1));
		insertedEmployees.add(employee);
	    }
	} catch (SQLException e) {
	    throw new IllegalArgumentException(e);
	}
	return new Object[][] { { orm, insertedEmployees } };
    }

    @Test(dataProvider = "employees")
    public void findAllTest(final ORM orm, final List<Employee> insertedList) {
	final List<Employee> list = orm.findAll(Employee.class);
	for (final Employee c : list) {
	    assertTrue(insertedList.contains(c));
	}
	assertEquals(list.size(), insertedList.size());
    }
}

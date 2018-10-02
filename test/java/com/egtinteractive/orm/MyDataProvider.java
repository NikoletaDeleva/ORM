package com.egtinteractive.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.egtinteractive.orm.classes.Employee;
import com.egtinteractive.orm.utils.DBCredentials;
import com.egtinteractive.orm.utils.ORM;

public class MyDataProvider {

    public static Object[][] insertEmployees() {
	DBCredentials credentials = new DBCredentials();
	final ORM orm = ORM.getConnectionAndCraete(credentials);
	final int count = ThreadLocalRandom.current().nextInt(10, 30);
	Connection con = orm.getConnection();
	
	dropAndCreateTable(con);

	final List<Employee> insertedEmployees = new ArrayList<>();
	
	final String sqlInserteEmployee = "INSERT INTO EMPLOYEE(first_name, last_name, salary) VALUES(?,?,?)";
	
	try (PreparedStatement ps = con.prepareStatement(sqlInserteEmployee,
		PreparedStatement.RETURN_GENERATED_KEYS);) {
	    for (int i = 1; i < count; i++) {

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

    private static void dropAndCreateTable(Connection con) {
	try (Statement st = con.createStatement()) {
	    st.execute("DROP TABLE EMPLOYEE;");
	    st.execute(
		    "CREATE TABLE IF NOT EXISTS EMPLOYEE(id INT PRIMARY KEY AUTO_INCREMENT, first_name varchar(10), last_name varchar(10), salary int);");
	} catch (SQLException e1) {
	    e1.printStackTrace();
	}
    }
}

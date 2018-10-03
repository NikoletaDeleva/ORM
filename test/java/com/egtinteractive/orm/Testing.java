package com.egtinteractive.orm;

import static org.junit.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static com.egtinteractive.orm.utils.ReflectionUtils.getTableName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.egtinteractive.orm.annotations.Table;
import com.egtinteractive.orm.classes.EmplInterface;
import com.egtinteractive.orm.classes.Employee;
import com.egtinteractive.orm.classes.EmployeeWithoutEntity;
import com.egtinteractive.orm.classes.EmployeeWithoutTable;
import com.egtinteractive.orm.utils.DBCredentials;
import com.egtinteractive.orm.utils.ORM;

public class Testing {
    final DBCredentials credentials = new DBCredentials();
    final ORM orm = ORM.getConnectionAndCraete(credentials);
    List<EmplInterface> empl;

    @BeforeTest
    public void insertEmployees() {
	final int count = ThreadLocalRandom.current().nextInt(10, 30);
	Connection con = orm.getConnection();

	dropAndCreateTable(con);

	final List<EmplInterface> insertedEmployees = new ArrayList<>();

	final String sqlInserteEmployee = "INSERT INTO EMPLOYEE(first_name, last_name, salary) VALUES(?,?,?)";

	try (PreparedStatement ps = con.prepareStatement(sqlInserteEmployee,
		PreparedStatement.RETURN_GENERATED_KEYS);) {
	    for (int i = 1; i < count; i++) {

		final EmplInterface employee = new Employee();

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
	empl = insertedEmployees;
    }

    @Test
    public void findAllTest() {
	final List<Employee> list = orm.findAll(Employee.class);
	for (final EmplInterface e : list) {
	    assertTrue(empl.contains(e));
	}
	assertEquals(list.size(), empl.size());
    }

    @Test
    public void findTest() {
	final EmplInterface e = orm.find(Employee.class, "5");
	assertTrue(empl.contains(e));
    }

    @Test
    public void testNoEntity() {
	DBCredentials credentials = new DBCredentials();
	final ORM orm = ORM.getConnectionAndCraete(credentials);
	Throwable actualExceptionFirst = null;

	try {
	    orm.find(EmployeeWithoutEntity.class, "5");
	} catch (Exception e) {
	    actualExceptionFirst = e;
	}

	assertNotNull(actualExceptionFirst);
	assertEquals(actualExceptionFirst.getClass(), IllegalArgumentException.class);

	Throwable actualExceptionSecond = null;

	try {
	    orm.findAll(EmployeeWithoutEntity.class);
	} catch (Exception e) {
	    actualExceptionSecond = e;
	}

	assertNotNull(actualExceptionSecond);
	assertEquals(actualExceptionSecond.getClass(), IllegalArgumentException.class);

    }

    @Test
    public void testTableName() {
	assertEquals(getTableName(EmployeeWithoutTable.class), EmployeeWithoutTable.class.getSimpleName());
	assertEquals(getTableName(Employee.class), Employee.class.getAnnotation(Table.class).name());
    }

    private void dropAndCreateTable(Connection con) {
	try (Statement st = con.createStatement()) {
	    st.execute("DROP TABLE EMPLOYEE;");
	    st.execute(
		    "CREATE TABLE IF NOT EXISTS EMPLOYEE(id INT PRIMARY KEY AUTO_INCREMENT, first_name varchar(10), last_name varchar(10), salary int);");
	} catch (SQLException e1) {
	    e1.printStackTrace();
	}
    }
}

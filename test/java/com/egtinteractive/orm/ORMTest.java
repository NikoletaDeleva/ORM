package com.egtinteractive.orm;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.egtinteractive.orm.classes.Employee;
import com.egtinteractive.orm.exceptions.ORMManagerException;
import com.egtinteractive.orm.utils.DBCredentials;
import com.egtinteractive.orm.utils.MyORM;
import com.mysql.cj.jdbc.MysqlDataSource;

public class ORMTest {
    private final String CONFIG_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator
	    + "configuration.properties";
    private final DBCredentials credentials = new DBCredentials(CONFIG_PATH);
    private Map<Integer,Employee> empl;

    private Connection getConnection(final DBCredentials credentials) {
	try {
	    final MysqlDataSource dataSource = new MysqlDataSource();

	    dataSource.setUser(credentials.getUser());
	    dataSource.setPassword(credentials.getPassword());
	    dataSource.setDatabaseName(credentials.getDBName());
	    dataSource.setPort(credentials.getPort());

	    final Connection connection = dataSource.getConnection();
	    return connection;
	} catch (Exception e) {
	    throw new ORMManagerException(e);
	}

    }

    @BeforeTest(alwaysRun = true)
    public void insertEmployees() {
	final int count = ThreadLocalRandom.current().nextInt(10, 30);

	final Map<Integer,Employee> insertedEmployees = new HashMap<>();

	final String sqlInserteEmployee = "INSERT INTO EMPLOYEE(first_name, last_name, salary, egn, height, weight) VALUES(?,?,?,?,?,?)";

	try (Connection connection = getConnection(credentials);
		PreparedStatement ps = connection.prepareStatement(sqlInserteEmployee,
			PreparedStatement.RETURN_GENERATED_KEYS);) {
	    for (int i = 1; i < count; i++) {

		final Employee employee = new Employee();

		employee.setFirstName("niki" + i);
		employee.setLastName("deleva" + i);
		employee.setSalary(i * 1000);

		ps.setString(1, employee.getFirstName());
		ps.setString(2, employee.getLastName());
		ps.setInt(3, employee.getSalary());
		ps.setLong(4, 0L);
		ps.setInt(5, 0);
		ps.setInt(6, 0);

		ps.executeUpdate();

		final ResultSet rs = ps.getGeneratedKeys();
		rs.next();

		employee.setId(rs.getInt(1));
		insertedEmployees.put(employee.getId(),employee);
	    }
	} catch (SQLException e) {
	    throw new ORMManagerException(e);
	}
	empl = insertedEmployees;
    }

    @Test
    public void findAllTest() throws Exception {
	try (final MyORM orm = MyORM.getConnectionAndCreate(credentials)) {
	    final List<Employee> list = orm.findAll(Employee.class);
	    for (final Employee e : list) {
		assertTrue(empl.containsValue(e));
	    }
	    assertEquals(list.size(), empl.size());
	}
    }

    @Test
    public void findTest() throws Exception {
	try (final MyORM orm = MyORM.getConnectionAndCreate(credentials)) {
	    final int rand = ThreadLocalRandom.current().nextInt(1, 10);
	    final Employee e = orm.find(Employee.class, "" +rand );
	    Employee employee = empl.get(rand);
	    assertEquals(e, employee);
	    assertTrue(empl.containsValue(e));
	}

    }

    @AfterTest(alwaysRun = true)
    public void cleanTable() {
	try (Connection connection = getConnection(credentials); Statement st = connection.createStatement()) {
	    st.execute("TRUNCATE TABLE EMPLOYEE;");
	} catch (SQLException e) {
	  throw new ORMManagerException(e);
	}
    }
}

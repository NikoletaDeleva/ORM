package utils;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.mysql.cj.jdbc.MysqlDataSource;
import static utils.ReflectiveUtils.*;

public class ORM implements Functionality {
    private static ORM instance;
    private final List<String> properties;

    private MysqlDataSource dataSource;

    private ORM() {
	properties = Arrays.asList("driver.class", "server.name", "server.port", "database.name", "user", "password");
    }

    public static ORM getInstance() {
	return getInstance(null);
    }

    public static ORM getInstance(final String dbProperties) {
	if (instance == null) {
	    instance = new ORM();
	}
	if (dbProperties != null) {
	    instance.setProperties(dbProperties);
	}
	return instance;
    }

    public Connection getConnection() {
	try {
	    return dataSource.getConnection();
	} catch (SQLException e) {
	    throw new IllegalArgumentException(e);
	}
    }

    private void setProperties(final String dbProperties) {
	try (FileReader inStream = new FileReader(dbProperties)) {
	    final Properties prop = new Properties();
	    prop.load(inStream);
	    dataSource = new MysqlDataSource();

	    for (String s : properties) {
		if (!prop.keySet().contains(s)) {
		    throw new IllegalArgumentException("Missing!");
		}
		if (prop.getProperty(s).equals("")) {
		    throw new IllegalArgumentException("Value missing!");
		}
	    }

	    Class.forName(prop.getProperty("driver.class"));
	    dataSource.setUser(prop.getProperty("user"));
	    dataSource.setPassword(prop.getProperty("password"));
	    dataSource.setDatabaseName(prop.getProperty("database.name"));
	    dataSource.setPort(Integer.parseInt(prop.getProperty("server.port")));

	} catch (ClassNotFoundException | IOException e) {
	    throw new IllegalStateException(e);
	}

    }

    public <E> List<E> findAll(final Class<E> classGen) {
	final Map<String, Field> mapColumnField = getMapColumnField(classGen);
	final StringBuilder columns = new StringBuilder(mapColumnField.keySet().toString());

	columns.delete(0, 1).delete(columns.length() - 1, columns.length());
	final StringBuilder sql = new StringBuilder();
	sql.append("SELECT ").append(columns.toString()).append(" FROM ");

	try (Connection connection = dataSource.getConnection();
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(
			sql.append(getTableName(classGen, dataSource.getDatabaseName())).append(";").toString())) {

	    return ReflectiveUtils.getList(classGen, result, mapColumnField);

	} catch (SQLException e) {
	    throw new IllegalStateException(e);
	}
    }
}

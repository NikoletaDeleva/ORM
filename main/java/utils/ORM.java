package utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.egtinteractive.orm.annotations.Entity;

import exceptions.ORMmanegerException;

import static utils.ReflectiveUtils.*;

public class ORM implements Functionality {
    private final Connection connection;
    private final Properties properties;
    private final List<Class<? extends Annotation>> requiredAnnotations;

    private ORM(final Connection connection, final Properties properties) {
	this.connection = connection;
	this.properties = properties;
	requiredAnnotations = new ArrayList<>();
	requiredAnnotations.add(Entity.class);
    }

    public static ORM getConnection() {
	final String path = "src" + File.separator + "main" + File.separator + "resources" + File.separator
		+ "configuration.properties";

	try {
	    final Properties properties = createProperties(path);
	    Connection connection = DriverManager.getConnection(properties.getProperty("URL"), properties);
	    return new ORM(connection, properties);
	} catch (Exception e) {
	    throw new ORMmanegerException();
	}

    }

    private static Properties createProperties(final String path) {
	try {
	    final Reader reader = new FileReader(path);
	    final Properties properties = new Properties();
	    properties.load(reader);
	    return properties;
	} catch (final IOException e) {
	    throw new RuntimeException();
	}
    }

    public void closeConnection() {
	try {
	    connection.close();
	} catch (SQLException e) {
	    throw new ORMmanegerException();
	}
    }

    @Override
    public <E> List<E> findAll(final Class<E> classGen) {

	final Map<String, Field> mapColumnField = getMapColumnField(classGen);
	final List<E> entityList = new ArrayList<>();

	final StringBuilder columnNames = new StringBuilder(mapColumnField.keySet().toString());

	columnNames.delete(0, 1).delete(columnNames.length() - 1, columnNames.length());

	final StringBuilder selectStatement = new StringBuilder();
	selectStatement.append("SELECT ").append(columnNames.toString()).append(" FROM ");

	try (final Statement statement = connection.createStatement();
		final ResultSet resultSet = statement.executeQuery(selectStatement
			.append(getTableName(classGen, connection.getSchema())).append(";").toString())) {

	    while (resultSet.next()) {
		E newEntity = getEntity(classGen, mapColumnField, resultSet);
		entityList.add(newEntity);
	    }
	    return entityList;
	} catch (SQLException e) {
	    throw new ORMmanegerException();
	}

    }

}

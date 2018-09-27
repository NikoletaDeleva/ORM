package com.egtinteractive.orm.utils;

import static com.egtinteractive.orm.utils.ReflectionUtils.*;

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

import com.egtinteractive.orm.annotations.*;
import com.egtinteractive.orm.exceptions.ORMmanegerException;

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

	final Map<String, Field> mapColumnField = getColumnToFieldMap(classGen);
	final List<E> entityList = new ArrayList<>();

	final String columnNames = getColumnNamesFromFields(mapColumnField);
	final String tableName = getTableName(classGen);

	final StringBuilder selectStatement = new StringBuilder();
	selectStatement.append("SELECT ").append(columnNames).append(" FROM ").append(tableName).append(";");

	try (final Statement statement = connection.createStatement();
		final ResultSet resultSet = statement.executeQuery(selectStatement.toString())) {

	    while (resultSet.next()) {
		E newEntity = getEntityFromRecord(classGen, resultSet, mapColumnField);
		entityList.add(newEntity);
	    }
	    return entityList;
	} catch (SQLException e) {
	    throw new ORMmanegerException();
	}

    }

    @Override
    public <E> E find(Class<E> classGen, String primaryKey) {
	// TODO Auto-generated method stub
	return null;
    }

    

}

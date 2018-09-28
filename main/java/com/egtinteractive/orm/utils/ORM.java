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

import javax.sql.DataSource;

import com.egtinteractive.orm.annotations.*;
import com.egtinteractive.orm.exceptions.ORMmanegerException;
import com.mysql.cj.jdbc.MysqlDataSource;

public class ORM implements Functionality {
    private final Connection connection;

    private ORM(final Connection connection) {
	this.connection = connection;
	;
    }

    public static ORM getConnection(DBCredentials credentials) {
	try {
	    MysqlDataSource dataSource = new MysqlDataSource();

	    dataSource.setUser(credentials.getUSER());
	    dataSource.setPassword(credentials.getPASSWORD());
	    dataSource.setDatabaseName(credentials.getDBNane());
	    dataSource.setPort(Integer.parseInt(credentials.getPort()));

	    Connection connection = dataSource.getConnection();
	    return new ORM(connection);
	} catch (Exception e) {
	    throw new ORMmanegerException();
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
	validate(classGen);

	final Map<String, Field> mapColumnField = getColumnToFieldMap(classGen);
	final List<E> entityList = new ArrayList<>();

	final String columnNames = getColumnNamesFromFields(mapColumnField);
	final String tableName = getTableName(classGen);

	final StringBuilder selectStatement = new StringBuilder();
	selectStatement.append("SELECT ").append(columnNames).append(" FROM ").append(tableName).append(";");

	try (final Statement statement = connection.createStatement();
		final ResultSet resultSet = statement.executeQuery(selectStatement.toString())) {

	    while (resultSet.next()) {
		E newEntity = getEntity(classGen, resultSet, mapColumnField);
		entityList.add(newEntity);
	    }
	    return entityList;
	} catch (SQLException e) {
	    throw new IllegalArgumentException();
	}

    }

    @Override
    public <E> E find(Class<E> classGen, String primaryKey) {

	return null;
    }

}

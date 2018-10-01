package com.egtinteractive.orm.utils;

import static com.egtinteractive.orm.utils.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.egtinteractive.orm.exceptions.ORMmanegerException;
import com.mysql.cj.jdbc.MysqlDataSource;

public class ORM implements Functionality, AutoCloseable {
    private final Connection connection;

    private ORM(final Connection connection) {
	this.connection = connection;
    }

    public static ORM getConnectionAndCraete(DBCredentials credentials) {
	try {
	    MysqlDataSource dataSource = new MysqlDataSource();

	    dataSource.setUser(credentials.getUSER());
	    dataSource.setPassword(credentials.getPASSWORD());
	    dataSource.setDatabaseName(credentials.getDBNane());
	    dataSource.setPort(Integer.parseInt(credentials.getPort()));

	    Connection connection = dataSource.getConnection();
	    return new ORM(connection);
	} catch (Exception e) {
	    throw new ORMmanegerException(e);
	}

    }

    public Connection getConnection() {
	return connection;
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
	    throw new IllegalArgumentException(e);
	}

    }

    @Override
    public <E> E find(Class<E> classGen, String primaryKey) {
	validate(classGen);

	final Map<String, Field> mapColumnField = getColumnToFieldMap(classGen);

	final String primaryColumn = findPrimaryField(classGen);

	final String columnNames = getColumnNamesFromFields(mapColumnField);
	final String table = getTableName(classGen);

	final StringBuilder selectStatement = new StringBuilder();
	selectStatement.append("SELECT ").append(columnNames).append(" FROM ").append(table).append(" WHERE ")
		.append(primaryColumn).append("=").append(primaryKey).append(";");

	try (final Statement statement = connection.createStatement();
		final ResultSet resultSet = statement.executeQuery(selectStatement.toString())) {

	    resultSet.next();
	    return getEntity(classGen, resultSet, mapColumnField);

	} catch (SQLException e) {
	    throw new IllegalArgumentException(e);
	}

    }

    @Override
    public void close() throws Exception {
	try {
	    connection.close();
	} catch (SQLException e) {
	    throw new ORMmanegerException(e);
	}
    }

}

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

import com.egtinteractive.orm.exceptions.ORMManagerException;
import com.mysql.cj.jdbc.MysqlDataSource;

public class MyORM implements ORM{
    private final Connection connection;
    private static MysqlDataSource dataSource;
    
    private MyORM(final Connection connection) {
	this.connection = connection;
    }

    public static MyORM getConnectionAndCreate(final DBCredentials credentials) {
	try {
	    dataSource = new MysqlDataSource();
	    
	    dataSource.setUser(credentials.getUser());
	    dataSource.setPassword(credentials.getPassword());
	    dataSource.setDatabaseName(credentials.getDBName());
	    dataSource.setPort(credentials.getPort());

	    final Connection connection = dataSource.getConnection();
	    return new MyORM(connection);
	} catch (Exception e) {
	    throw new ORMManagerException(e);
	}
    }

    @Override
    public <E> List<E> findAll(final Class<E> clazz) {
	validate(clazz);

	final Map<String, Field> mapColumnField = getColumnToFieldMap(clazz);
	final List<E> entityList = new ArrayList<>();

	final String columnNames = getColumnNamesFromFields(mapColumnField);
	final String tableName = getTableName(clazz, dataSource.getDatabaseName());

	final StringBuilder selectStatement = new StringBuilder();
	selectStatement.append("SELECT ").append(columnNames).append(" FROM ").append(tableName).append(";");

	try (final Statement statement = connection.createStatement();
		final ResultSet resultSet = statement.executeQuery(selectStatement.toString())) {

	    while (resultSet.next()) {
		final E newEntity = getEntity(clazz, resultSet, mapColumnField);
		entityList.add(newEntity);
	    }
	    return entityList;
	} catch (SQLException e) {
	    throw new ORMManagerException(e);
	}

    }

    @Override
    public <E> E find(final Class<E> clazz, final String primaryKey) {
	validate(clazz);

	final Map<String, Field> mapColumnField = getColumnToFieldMap(clazz);

	final String primaryColumn = findPrimaryKeyField(clazz);

	final String columnNames = getColumnNamesFromFields(mapColumnField);
	final String table = getTableName(clazz, dataSource.getDatabaseName());

	final StringBuilder selectStatement = new StringBuilder();
	selectStatement.append("SELECT ").append(columnNames).append(" FROM ").append(table).append(" WHERE ")
		.append(primaryColumn).append("=").append(primaryKey).append(";");

	try (final Statement statement = connection.createStatement();
		final ResultSet resultSet = statement.executeQuery(selectStatement.toString())) {

	    resultSet.next();
	    return getEntity(clazz, resultSet, mapColumnField);

	} catch (SQLException e) {
	    throw new ORMManagerException(e);
	}

    }

    @Override
    public void close() throws Exception {
	try {
	    connection.close();
	} catch (SQLException e) {
	    throw new ORMManagerException(e);
	}
    }

}

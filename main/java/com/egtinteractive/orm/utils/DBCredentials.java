package com.egtinteractive.orm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBCredentials {
    private final String url;
    private final String user;
    private final String password;
    private final String database;
    private final int port;

    public DBCredentials(String path) {
	final Properties properties = getCredentials(path);

	validate(properties);

	this.user = properties.getProperty("dbuser");
	this.password = properties.getProperty("dbpassword");
	final String host = properties.getProperty("host");
	this.port = Integer.parseInt(properties.getProperty("port"));
	this.database = properties.getProperty("database");

	StringBuilder urlBuild = new StringBuilder();
	urlBuild.append("jdbc:mysql:").append(File.separator).append(File.separator).append(host).append(":")
		.append(port).append(File.separator).append(database);

	this.url = urlBuild.toString();
    }

    private Properties getCredentials(String path) {
	final Properties properties = new Properties();

	try (InputStream input = new FileInputStream(path);) {
	    properties.load(input);
	    return properties;
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    public String getUrl() {
	return url;
    }

    public String getUser() {
	return user;
    }

    public int getPort() {
	return port;
    }

    public String getPassword() {
	return password;
    }

    public String getDBName() {
	return database;
    }

    private void validate(Properties properties) {
	if (!properties.containsKey("dbpassword")) {
	    throw new IllegalArgumentException("Password property is missing!");
	} else if (!properties.containsKey("host")) {
	    throw new IllegalArgumentException("Host property is missing!");
	} else if (!properties.containsKey("port")) {
	    throw new IllegalArgumentException("Port property is missing!");
	} else if (!properties.containsKey("database")) {
	    throw new IllegalArgumentException("DataBase property is missing!");
	} else if (!properties.containsKey("dbuser")) {
	    throw new IllegalArgumentException("User property is missing!");
	}

    }

}

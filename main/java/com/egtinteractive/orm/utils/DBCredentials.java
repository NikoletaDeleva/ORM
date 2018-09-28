package com.egtinteractive.orm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBCredentials {
    private final static String CONFIG_PATH = "src" + File.separator + "main" + File.separator + "resources"
	    + File.separator + "configuration.properties";
    private final String URL;
    private final String USER;
    private final String PASSWORD;
    final String database;
    final String port;
    
    public DBCredentials() {
	final Properties properties = getCredentials();
	this.USER = properties.getProperty("dbuser");
	this.PASSWORD = properties.getProperty("dbpassword");
	final String host = properties.getProperty("host");
	this.port = properties.getProperty("port");
	this.database = properties.getProperty("database");

	this.URL = "jdbc:mysql:" + File.separator + File.separator + host + ":" + port + File.separator + database;
    }

    private Properties getCredentials() {
	final Properties properties = new Properties();

	try (InputStream input = new FileInputStream(CONFIG_PATH);) {
	    properties.load(input);
	    return properties;
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    public String getURL() {
	return URL;
    }

    public String getUSER() {
	return USER;
    }

    public String getPort() {
        return port;
    }

    public String getPASSWORD() {
	return PASSWORD;
    }

    public String getDBNane() {
	return database;
    }

}

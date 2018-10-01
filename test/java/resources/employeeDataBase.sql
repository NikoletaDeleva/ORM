DROP database if EXISTS pojo;

CREATE database pojo;
USE pojo;

CREATE TABLE IF NOT EXISTS EMPLOYEE(id INT PRIMARY KEY AUTO_INCREMENT, first_name varchar(10), last_name varchar(10), salary int);
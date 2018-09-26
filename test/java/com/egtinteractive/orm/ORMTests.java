package com.egtinteractive.orm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mysql.cj.jdbc.PreparedStatement;

import utils.ORM;

public class ORMTests {
    @DataProvider
    public Object[][] insertCars() {
	final Random rn = new Random();
	final String dbProperties = "src/com/reflection/resources/db.properties";
	final int minCarPower = 100;
	final int maxCarPower = 300;
	final int minCarCount = 10;
	final int maxCarCount = 20;
	final int carsCount = rn.nextInt(maxCarCount - minCarCount) + minCarCount;

	try (ORM.getConnection(); Statement st = con.createStatement()) {
	    st.execute("DROP TABLE reflection.cars");
	    st.execute(
		    "CREATE TABLE IF NOT EXISTS reflection.cars(id INT PRIMARY KEY AUTO_INCREMENT,brand VARCHAR(45),abs BIT(1),power INT);");
	} catch (SQLException e1) {
	    e1.printStackTrace();
	}

	final List<Car> insertedCars = new ArrayList<>();
	final String sqlInsertIntoCars = "INSERT INTO reflection.cars(brand, abs, power) VALUES(?,?,?)";
	try (Connection con = orm.getConnection();
		PreparedStatement ps = con.prepareStatement(sqlInsertIntoCars,
			PreparedStatement.RETURN_GENERATED_KEYS)) {
	    for (int i = 0; i < carsCount; i++) {
		final Car car = new Car(i, Brand.values()[rn.nextInt(Brand.values().length)].toString(),
			rn.nextBoolean(), rn.nextInt(maxCarPower - minCarPower) + minCarPower, null);
		ps.setString(1, car.getBrand());
		ps.setBoolean(2, car.isAbs());
		ps.setInt(3, car.getPower());
		ps.executeUpdate();
		final ResultSet rs = ps.getGeneratedKeys();
		rs.next();
		car.setId(rs.getInt(1));
		insertedCars.add(car);
	    }
	} catch (SQLException e) {
	    throw new IllegalArgumentException(e);
	}
	return new Object[][] { { dbProperties, orm, insertedCars } };
    }
    
    @Test(dataProvider = "insertCars")
    public void findAllCarViews(final String dbProperties, final ORM dbManager, final List<Car> insertedList){
	final List<CarView> carViewList = new ArrayList<>(); 
	for (final Car c : insertedList){
	    final CarView cv = new CarView();
	    cv.setCarId(c.getId());
	    cv.setBrand(c.getBrand());
	    carViewList.add(cv);
	}
	final List<CarView> list = dbManager.findAll(CarView.class);
	for (final CarView c : list) {
	    assertTrue(carViewList.contains(c));
	}
	assertEquals(list.size(), carViewList.size());
    }

    @Test(dataProvider = "insertCars")
    public void findAllCars(final String dbProperties, final ORM dbManager, final List<Car> insertedList) {
	final List<Car> list = dbManager.findAll(Car.class);
	for (final Car c : list) {
	    assertTrue(insertedList.contains(c));
	}
	assertEquals(list.size(), insertedList.size());
    }
}

package com.egtinteractive.orm.classes;

import com.egtinteractive.orm.annotations.*;

@Entity
@Table(name = "EMPLOYEE")
public class Employee implements EmplInterface {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "salary")
    private int salary;

    public Employee() {
    }

    public Employee(String fname, String lname, int salary) {
	this.firstName = fname;
	this.lastName = lname;
	this.salary = salary;
    }

    @Override
    public int getId() {
	return id;
    }

    @Override
    public void setId(int id) {
	this.id = id;
    }

    @Override
    public String getFirstName() {
	return firstName;
    }

    @Override
    public void setFirstName(String first_name) {
	this.firstName = first_name;
    }

    @Override
    public String getLastName() {
	return lastName;
    }

    @Override
    public void setLastName(String last_name) {
	this.lastName = last_name;
    }

    @Override
    public int getSalary() {
	return salary;
    }

    @Override
    public void setSalary(int salary) {
	this.salary = salary;
    }

    @Override
    public String toString() {
	return this.getId() + " " + this.getFirstName() + " " + this.getLastName() + " " + this.getSalary();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
	result = prime * result + id;
	result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
	result = prime * result + salary;
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Employee other = (Employee) obj;
	if (firstName == null) {
	    if (other.firstName != null)
		return false;
	} else if (!firstName.equals(other.firstName))
	    return false;
	if (id != other.id)
	    return false;
	if (lastName == null) {
	    if (other.lastName != null)
		return false;
	} else if (!lastName.equals(other.lastName))
	    return false;
	if (salary != other.salary)
	    return false;
	return true;
    }

}
package com.egtinteractive.orm.classes;

import com.egtinteractive.orm.annotations.Column;
import com.egtinteractive.orm.annotations.Entity;
import com.egtinteractive.orm.annotations.Id;
import com.egtinteractive.orm.annotations.Table;
import com.egtinteractive.orm.annotations.Transient;

@Entity
@Table(name = "EMPLOYEE")
public class EmployeeWithTransient extends Person {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Transient
    private String lastName;

    @Transient
    private int salary;

    public EmployeeWithTransient() {
    }

    public EmployeeWithTransient(String fname, String lname, int salary) {
	this.firstName = fname;
	this.lastName = lname;
	this.salary = salary;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String first_name) {
	this.firstName = first_name;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String last_name) {
	this.lastName = last_name;
    }

    public int getSalary() {
	return salary;
    }

    public void setSalary(int salary) {
	this.salary = salary;
    }

    public String toString() {
	return this.getId() + " " + this.getFirstName() + " " + this.getLastName() + " " + this.getSalary();
    }

    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
	result = prime * result + id;
	result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
	result = prime * result + salary;
	return result;
    }

    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	EmployeeWithTransient other = (EmployeeWithTransient) obj;
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

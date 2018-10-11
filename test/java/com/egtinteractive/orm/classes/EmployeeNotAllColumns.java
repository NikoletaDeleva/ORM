package com.egtinteractive.orm.classes;

import com.egtinteractive.orm.annotations.Column;
import com.egtinteractive.orm.annotations.Entity;
import com.egtinteractive.orm.annotations.Id;
import com.egtinteractive.orm.annotations.Table;

@Entity
@Table(name = "EMPLOYEE")
public class EmployeeNotAllColumns extends Person {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    public EmployeeNotAllColumns() {
    }

    public EmployeeNotAllColumns(String fname) {
	this.firstName = fname;
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

    public String toString() {
	return this.getId() + " " + this.getFirstName();
    }

    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
	result = prime * result + id;
	return result;
    }

    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	EmployeeNotAllColumns other = (EmployeeNotAllColumns) obj;
	if (firstName == null) {
	    if (other.firstName != null)
		return false;
	} else if (!firstName.equals(other.firstName))
	    return false;
	if (id != other.id)
	    return false;
	return true;
    }

}

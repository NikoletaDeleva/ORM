package com.egtinteractive.orm.classes;

public interface EmplInterface {

    int getId();

    void setId(int id);

    String getFirstName();

    void setFirstName(String first_name);

    String getLastName();

    void setLastName(String last_name);

    int getSalary();

    void setSalary(int salary);

    String toString();

    int hashCode();

    boolean equals(Object obj);

}
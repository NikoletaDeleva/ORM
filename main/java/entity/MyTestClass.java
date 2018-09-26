package entity;

import java.util.List;

import com.egtinteractive.orm.annotations.Column;
import com.egtinteractive.orm.annotations.Entity;
import com.egtinteractive.orm.annotations.Id;
import com.egtinteractive.orm.annotations.Table;
import com.egtinteractive.orm.annotations.Transient;

@Entity
@Table(name = "cars")
public class MyTestClass {

    @Id
    private int id;

    @Column
    private String name;

    @Transient
    private List<String> names;

    public MyTestClass() {

    }

    public MyTestClass(int id, String name, List<String> commonBrands) {
	this.id = id;
	this.name = name;
	this.names = commonBrands;
    }

    @Column(name = "id")
    public int geId() {
	return id;
    }

    public void seId(int id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<String> getNames() {
	return names;
    }

    public void setNames(List<String> names) {
	this.names = names;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + ((names == null) ? 0 : names.hashCode());
	result = prime * result + id;
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
	MyTestClass other = (MyTestClass) obj;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (names == null) {
	    if (other.names != null)
		return false;
	} else if (!names.equals(other.names))
	    return false;
	if (id != other.id)
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "MyTestClass [id=" + id + ", name=" + name + ", Names=" + names + "]";
    }

}

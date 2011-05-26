package test;

import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Domain {
	
	private Long id;
	private String name;
	private Map<String, String> names;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	@Basic
	public String getName() {
		return name;
	}
	
	@Transient
	public Map<String, String> getNames() {
		return names;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	} 
	
	public void setNames(Map<String, String> names) {
		this.names = names;
	}
	
}
package org.chai.kevin;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.chai.kevin.dashboard.DashboardCalculation;
import org.hibernate.annotations.Cascade;

@MappedSuperclass
public abstract class Translatable implements Serializable {

	private static final long serialVersionUID = 5282731214725130450L;
	
	private String name;
	private String description;
	
	private Map<String, Translation> descriptions;
	private Map<String, Translation> names;
	
	@Basic
	public String getName() {
		return name;
	}
	
	@Basic
	public String getDescription() {
		return description;
	}
	
	@OneToMany(targetEntity=Translation.class)
	@Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	@MapKey(name="locale")
	@JoinColumn
	public Map<String, Translation> getDescriptions() {
		return descriptions;
	}
	
	@OneToMany(targetEntity=Translation.class)
	@Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	@MapKey(name="locale")
	@JoinColumn
	public Map<String, Translation> getNames() {
		return names;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setDescriptions(Map<String, Translation> descriptions) {
		this.descriptions = descriptions;
	}
	
	public void setNames(Map<String, Translation> names) {
		this.names = names;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Translatable other = (Translatable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Objective [name=" + name + "]";
	}
	
}

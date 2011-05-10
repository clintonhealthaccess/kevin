package org.chai.kevin;

import java.io.Serializable;
import java.util.NoSuchElementException;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Organisation;
import org.chai.kevin.dashboard.Explanation;
import org.chai.kevin.dashboard.ExplanationCalculator;
import org.chai.kevin.dashboard.DashboardPercentage;
import org.chai.kevin.dashboard.PercentageCalculator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

@Entity(name="Objective")
@Table(name="dhsst_objective")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Objective implements Serializable {

	private Integer id;
	private String name;
	private String description;
	
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Basic
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Basic
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
		Objective other = (Objective) obj;
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

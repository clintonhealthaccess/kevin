package org.chai.kevin;

import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;

public class Organisation {

	private List<Organisation> children;
	private OrganisationUnit organisationUnit;
	private Organisation parent;
	private int level;
	
	public Organisation(OrganisationUnit organisationUnit) {
		this.organisationUnit = organisationUnit;
	}

	public String getName() {
		return organisationUnit.getName();
	}
	
	public List<Organisation> getChildren() {
		return children;
	}
	
	public void setChildren(List<Organisation> children) {
		this.children = children;
	}

	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
	}
	
	public void setOrganisationUnit(OrganisationUnit organisationUnit) {
		this.organisationUnit = organisationUnit;
	}
	
	public Organisation getParent() {
		return parent;
	}
	
	public void setParent(Organisation parent) {
		this.parent = parent;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getId() {
		return organisationUnit.getId();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((organisationUnit == null) ? 0 : organisationUnit.hashCode());
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
		Organisation other = (Organisation) obj;
		if (organisationUnit == null) {
			if (other.organisationUnit != null)
				return false;
		} else if (!organisationUnit.equals(other.organisationUnit))
			return false;
		return true;
	}
	
}


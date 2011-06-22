package org.chai.kevin;

/* 
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

public class Organisation {

	private List<Organisation> children;
	private OrganisationUnit organisationUnit;
	private Organisation parent;
	private OrganisationUnitGroup organisationUnitGroup;
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
	
	public OrganisationUnitGroup getOrganisationUnitGroup() {
		return organisationUnitGroup;
	}
	
	public void setOrganisationUnitGroup(OrganisationUnitGroup organisationUnitGroup) {
		this.organisationUnitGroup = organisationUnitGroup;
	}
	
	public void setParent(Organisation parent) {
		this.parent = parent;
	}
	
	public Integer getLevel() {
		return level;
	}
	
	public void setLevel(Integer level) {
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

	
	public String toJson() {
		return 
		"{" +
			"\"name\":\""+getOrganisationUnit().getName()+"\","+
			(getOrganisationUnit().getCoordinates()!=null?"\"coordinates\":"+getOrganisationUnit().getCoordinates()+",":"")+
			"\"id\":"+getOrganisationUnit().getId()+","+
			(getParent()!=null?"\"parent\":"+getParent().getId()+",":"")+
			"\"level\":"+getLevel()+
		"}";
	}
	
	@Override
	public String toString() {
		return "Organisation [organisationUnit=" + organisationUnit + ", level=" + level + "]";
	}
	
}


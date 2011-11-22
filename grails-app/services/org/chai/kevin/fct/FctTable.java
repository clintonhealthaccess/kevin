package org.chai.kevin.fct;

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

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.Organisation;
import org.chai.kevin.dsr.DsrObjective;
import org.chai.kevin.dsr.DsrTarget;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;

@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class FctTable implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<FctTarget, Fct> totalMap;
	private List<Organisation> organisations;
	private List<FctTarget> targets;
	private Set<OrganisationUnitGroup> facilityTypes;
	private Map<FctTarget, Map<Organisation, Fct>> values;
	private Map<Organisation, List<Organisation>> organisationMap;


	public FctTable(List<Organisation> organisations, List<FctTarget> targets, Set<OrganisationUnitGroup> facilityTypes,
			Map<FctTarget, Map<Organisation, Fct>> values, Map<Organisation,List<Organisation>> organisationMap, Map<FctTarget, Fct> totalMap) {
		super();
		this.organisations = organisations;
		this.facilityTypes = facilityTypes;
		this.targets = targets;
		this.values = values;
		this.organisationMap = organisationMap;
		this.totalMap = totalMap;
	}

	public List<Organisation> getOrganisations() {
		return organisations;
	}

	public List<FctTarget> getTargets() {
		return targets;
	}
	
	public Fct getTotal(FctTarget target) {
		return totalMap.get(target);
	}
	
	public Fct getFct(Organisation organisation, FctTarget target) {
		return values.get(target).get(organisation);
	}

	public Set<OrganisationUnitGroup> getFacilityTypes() {
		return facilityTypes;
	}

	public Map<Organisation, List<Organisation>> getOrganisationMap() {
		return organisationMap;
	}

}

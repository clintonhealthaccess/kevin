package org.chai.kevin.dashboard;

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
import java.util.Map;
import java.util.Map.Entry;

import org.chai.kevin.Organisation;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;

public class Dashboard {
	
	private List<Organisation> organisations;
	private List<DashboardObjectiveEntry> objectiveEntries;
	
	private List<Organisation> organisationPath;
	private List<DashboardObjective> objectivePath;
	
	private List<OrganisationUnitGroup> facilityTypes;
	
	private Map<Organisation, Map<DashboardEntry, DashboardPercentage>> values;
	
	public Dashboard(List<Organisation> organisations, List<DashboardObjectiveEntry> objectiveEntries,
			List<Organisation> organisationPath, List<DashboardObjective> objectivePath,
			List<OrganisationUnitGroup> facilityTypes,
			Map<Organisation, Map<DashboardEntry, DashboardPercentage>> values
	) {
		this.organisations = organisations;
		this.objectiveEntries = objectiveEntries;
		this.organisationPath = organisationPath;
		this.objectivePath = objectivePath;
		this.values = values;
		this.facilityTypes = facilityTypes;
	}
	

	public List<Organisation> getOrganisations() {
		return organisations;
	}
	
	public List<DashboardObjectiveEntry> getObjectiveEntries() {
		return objectiveEntries;
	}
	
	public List<DashboardObjective> getObjectivePath() {
		return objectivePath;
	}
	
	public List<Organisation> getOrganisationPath() {
		return organisationPath;
	}
	
	public List<OrganisationUnitGroup> getFacilityTypes() {
		return facilityTypes;
	}
	
	public DashboardPercentage getPercentage(Organisation organisation, DashboardEntry objective) {
		return values.get(organisation).get(objective);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Entry<Organisation, Map<DashboardEntry, DashboardPercentage>> organisationEntry : this.values.entrySet()) {
			buffer.append(organisationEntry.getKey());
			for (Entry<DashboardEntry, DashboardPercentage> objectiveEntry : organisationEntry.getValue().entrySet()) {
				buffer.append(objectiveEntry.getKey());
				buffer.append(":");
				buffer.append(objectiveEntry.getValue());
				buffer.append(",");
			}
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
}
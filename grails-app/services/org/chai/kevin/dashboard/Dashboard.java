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
import org.chai.kevin.reports.ReportObjective;

public class Dashboard {
	
	private List<Organisation> organisations;
	private List<DashboardEntity> dashboardEntities;
	
	private List<Organisation> organisationPath;
	private List<DashboardObjective> objectivePath;
	
	private Map<Organisation, Map<DashboardEntity, DashboardPercentage>> values;
	
	public Dashboard(List<Organisation> organisations, List<DashboardEntity> dashboardEntities,
			List<Organisation> organisationPath, List<DashboardObjective> objectivePath,
			Map<Organisation, Map<DashboardEntity, DashboardPercentage>> map
	) {
		this.organisations = organisations;
		this.dashboardEntities = dashboardEntities;
		this.organisationPath = organisationPath;
		this.objectivePath = objectivePath;
		this.values = map;
	}
	

	public List<Organisation> getOrganisations() {
		return organisations;
	}
	
	public List<DashboardEntity> getObjectiveEntities() {
		return dashboardEntities;
	}
	
	public List<DashboardObjective> getObjectivePath() {
		return objectivePath;
	}
	
	public List<Organisation> getOrganisationPath() {
		return organisationPath;
	}
	
	public DashboardPercentage getPercentage(Organisation organisation, DashboardEntity entity) {
		return values.get(organisation).get(entity);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Entry<Organisation, Map<DashboardEntity, DashboardPercentage>> organisationDashboardEntities : this.values.entrySet()) {
			buffer.append(organisationDashboardEntities.getKey());
			for (Entry<DashboardEntity, DashboardPercentage> organisationDashboardEntity : organisationDashboardEntities.getValue().entrySet()) {
				buffer.append(organisationDashboardEntity.getKey());
				buffer.append(":");
				buffer.append(organisationDashboardEntity.getValue());
				buffer.append(",");
			}
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
}

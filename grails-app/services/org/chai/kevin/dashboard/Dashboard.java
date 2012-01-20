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

import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.LocationEntity;

public class Dashboard {
	
	private List<CalculationEntity> organisations;
	private List<DashboardEntity> dashboardEntities;
	
	private List<LocationEntity> organisationPath;
	private List<DashboardObjective> objectivePath;
	
	private Map<CalculationEntity, Map<DashboardEntity, DashboardPercentage>> values;
	
	public Dashboard(List<CalculationEntity> organisations, List<DashboardEntity> dashboardEntities,
			List<LocationEntity> organisationPath, List<DashboardObjective> objectivePath,
			Map<CalculationEntity, Map<DashboardEntity, DashboardPercentage>> values
	) {
		this.organisations = organisations;
		this.dashboardEntities = dashboardEntities;
		this.organisationPath = organisationPath;
		this.objectivePath = objectivePath;
		this.values = values;
	}
	
	public List<CalculationEntity> getOrganisations() {
		return organisations;
	}
	
	public List<DashboardEntity> getObjectiveEntities() {
		return dashboardEntities;
	}
	
	public List<DashboardObjective> getObjectivePath() {
		return objectivePath;
	}
	
	public List<LocationEntity> getOrganisationPath() {
		return organisationPath;
	}
	
	public Integer getPercentage(CalculationEntity organisation, DashboardEntity dashboardEntity) {		
		DashboardPercentage percentage = null;
		percentage = values.get(organisation).get(dashboardEntity);
		if(percentage != null && percentage.isValid())
			return percentage.getRoundedValue();
		else
			return null;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Entry<CalculationEntity, Map<DashboardEntity, DashboardPercentage>> organisationEntry : this.values.entrySet()) {
			buffer.append(organisationEntry.getKey());
			for (Entry<DashboardEntity, DashboardPercentage> objectiveEntry : organisationEntry.getValue().entrySet()) {
				buffer.append(objectiveEntry.getKey());
				buffer.append(":");
				buffer.append(organisationEntry.getValue());
				buffer.append(",");
			}
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
}

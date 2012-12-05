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
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.Transient;

import org.chai.kevin.data.Type;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.kevin.value.Value;

public class Dashboard {
	
	DashboardService dashboardService
	
	private static Type DASHBOARD_TYPE = Type.TYPE_NUMBER();
	private static String format = "#%";
	
	// all the data
	private Map<CalculationLocation, Map<DashboardEntity, Value>> valueMap;
	
	List<Location> locationPath;
	
	public Dashboard(Map<CalculationLocation, Map<DashboardEntity, Value>> valueMap, List<Location> locationPath, DashboardService dashboardService) {
		this.locationPath = locationPath;
		this.valueMap = valueMap;
		this.dashboardService = dashboardService
	}
	
	public List<CalculationLocation> getLocations(Location parent, def skipLevels, def types) {
		//gets all location children and data locations
		def result = new ArrayList<CalculationLocation>();
		result.addAll(parent.getChildren(skipLevels));
		result.addAll(parent.getDataLocations(skipLevels, types));
		return result.sort({it.names});
	}
	
	public List<DashboardEntity> getIndicators(DashboardEntity entity) {
		// TODO sorting
		return dashboardService.collectDashboardEntitiesWithTargets(entity.getReportProgram()).sort({it.order})
	}
	
	public Value getPercentage(CalculationLocation location, DashboardEntity dashboardEntity) {		
		return valueMap.get(location).get(dashboardEntity);
	}
	
	public Type getType() {
		return DASHBOARD_TYPE;
	}
	
	public String getFormat(){
		return format;
	}
	
	public boolean hasData(){
		def result = false
		valueMap.each { location, map -> 
			map.each { entity, value ->
				if (value != null) result = true
			}
		}
		return result
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Entry<CalculationLocation, Map<DashboardEntity, Value>> locationEntry : this.valueMap.entrySet()) {
			buffer.append(locationEntry.getKey());
			for (Entry<DashboardEntity, Value> programEntry : locationEntry.getValue().entrySet()) {
				buffer.append(programEntry.getKey());
				buffer.append(":");
				buffer.append(locationEntry.getValue());
				buffer.append(",");
			}
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
}

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

import grails.plugin.springcache.annotations.Cacheable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LocationService;
import org.chai.kevin.data.Info;
import org.chai.kevin.data.InfoService;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportObjective;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class DashboardService {

//	private Log log = LogFactory.getLog(DashboardService.class);
	
	private ReportService reportService;
	private LocationService locationService;
	private SessionFactory sessionFactory;
	private DashboardPercentageService dashboardPercentageService;
	private Set<String> skipLevels;
	
	@Transactional(readOnly = true)
	public Dashboard getProgramDashboard(LocationEntity location, ReportObjective objective, Period period, Set<DataEntityType> types){

		List<CalculationEntity> locationEntities = new ArrayList<CalculationEntity>();		
		locationEntities.add(location);

		List<DashboardEntity> dashboardEntities = new ArrayList<DashboardEntity>();		
		dashboardEntities.addAll(getDashboardEntities(objective));
		
		List<LocationEntity> locationPath = new ArrayList<LocationEntity>();
		Map<CalculationEntity, Map<DashboardEntity, DashboardPercentage>> valueMap = 
				new HashMap<CalculationEntity, Map<DashboardEntity, DashboardPercentage>>();
		
		if(dashboardEntities.isEmpty())
			return new Dashboard(locationEntities, dashboardEntities, locationPath, valueMap);
		
		locationPath = calculateLocationPath(location);
		valueMap = getValues(locationEntities, dashboardEntities, period, types);
		
		return new Dashboard(locationEntities, dashboardEntities, locationPath, valueMap);
	}
			
	@Transactional(readOnly = true)
	public Dashboard getLocationDashboard(LocationEntity location, ReportObjective objective, Period period, Set<DataEntityType> types, boolean compare) {
		
		List<CalculationEntity> locationEntities = new ArrayList<CalculationEntity>();
		if(compare) locationEntities.add(location);
		else locationEntities.addAll(locationService.getLocationEntities(location, skipLevels, types));	
		
		List<DashboardEntity> dashboardEntities = new ArrayList<DashboardEntity>();		
		dashboardEntities.add(getDashboardObjective(objective));		
		
		List<LocationEntity> locationPath = new ArrayList<LocationEntity>();
		Map<CalculationEntity, Map<DashboardEntity, DashboardPercentage>> valueMap = 
				new HashMap<CalculationEntity, Map<DashboardEntity, DashboardPercentage>>();
		
		if(locationEntities.isEmpty())
			return new Dashboard(locationEntities, dashboardEntities, locationPath, valueMap);
		
		locationPath = calculateLocationPath(location);
		valueMap = getValues(locationEntities, dashboardEntities, period, types);
		
		return new Dashboard(locationEntities, dashboardEntities, locationPath, valueMap);
	}
	
	private Map<CalculationEntity, Map<DashboardEntity, DashboardPercentage>> getValues(List<CalculationEntity> locations, List<DashboardEntity> dashboardEntities, Period period, Set<DataEntityType> types) {
		Map<CalculationEntity, Map<DashboardEntity, DashboardPercentage>> valueMap = new HashMap<CalculationEntity, Map<DashboardEntity, DashboardPercentage>>();

		for (CalculationEntity location : locations) {
			Map<DashboardEntity, DashboardPercentage> locationMap = getValues(dashboardEntities, period, location, types);
			valueMap.put(location, locationMap);
		}
		return valueMap;
	}

	private Map<DashboardEntity, DashboardPercentage> getValues(List<DashboardEntity> dashboardEntities, Period period, CalculationEntity location, Set<DataEntityType> types) {
		Map<DashboardEntity, DashboardPercentage> entityMap = new HashMap<DashboardEntity, DashboardPercentage>();
		for (DashboardEntity dashboardEntity : dashboardEntities) {
			DashboardPercentage percentage = dashboardPercentageService.getDashboardValue(period, location, types, dashboardEntity);
			entityMap.put(dashboardEntity, percentage);
		}
		return entityMap;
	}

	private List<LocationEntity> calculateLocationPath(LocationEntity entity) {
		List<LocationEntity> locationPath = new ArrayList<LocationEntity>();
		LocationEntity parent = entity;
		while ((parent = parent.getParent()) != null) {
			locationPath.add(parent);
		}
		Collections.reverse(locationPath);
		return locationPath;
	}
	
//	private List<DashboardObjective> getBreadcrumb(ReportObjective objective) {
//		List<DashboardObjective> objectivePath = new ArrayList<DashboardObjective>();
//		while (objective != null) {
//			ReportObjective parent = objective.getParent();
//			if(parent != null) {
//				DashboardObjective dashboardParent = (DashboardObjective) getDashboardObjective(parent);
//				if(dashboardParent != null)	objectivePath.add(dashboardParent);
//			}
//			objective = parent;
//		}
//		Collections.reverse(objectivePath);
//		return objectivePath;
//	}
	
	public List<DashboardEntity> getDashboardEntities(ReportObjective objective) {		
		List<DashboardEntity> entities = new ArrayList<DashboardEntity>();				
		
		List<DashboardEntity> dashboardObjectives = getDashboardObjectives(objective);
		entities.addAll(dashboardObjectives);
		
		List<DashboardTarget> dashboardTargets = reportService.getReportTargets(DashboardTarget.class, objective);		
		entities.addAll(dashboardTargets);
		
		return entities;
	}
	
	public List<DashboardEntity> getDashboardObjectives(ReportObjective objective){
		List<DashboardEntity> result = new ArrayList<DashboardEntity>();
		List<ReportObjective> children = objective.getChildren();
		if(children != null) {
			for (ReportObjective child : children) {
				DashboardEntity dashboardObjective = getDashboardObjective(child);
				if(dashboardObjective != null)	result.add(dashboardObjective);
			}
		}
		return result;
	}
	
	public DashboardEntity getDashboardObjective(ReportObjective objective) {
		DashboardObjective dashboardObjective = (DashboardObjective) sessionFactory.getCurrentSession()
				.createCriteria(DashboardObjective.class)
				.add(Restrictions.eq("objective", objective))
				.uniqueResult();
		
		return dashboardObjective;
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setSkipLevels(Set<String> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public void setDashboardPercentageService(DashboardPercentageService dashboardPercentageService) {
		this.dashboardPercentageService = dashboardPercentageService;
	}
	
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
}

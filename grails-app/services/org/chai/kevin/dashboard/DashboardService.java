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
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class DashboardService {

//	private Log log = LogFactory.getLog(DashboardService.class);
	
	private InfoService infoService;
	private ValueService valueService;
	private LocationService locationService;
	
	private Set<String> skipLevels;
	
	private Set<LocationLevel> getSkipLocationLevels() {
		Set<LocationLevel> levels = new HashSet<LocationLevel>();
		for (String skipLevel : skipLevels) {
			levels.add(locationService.findLocationLevelByCode(skipLevel));
		}
		return levels;
	}
	
	@Transactional(readOnly = true)
	@Cacheable("dashboardCache")
	public Dashboard getDashboard(LocationEntity entity, DashboardObjective objective, Period period, Set<DataEntityType> groups) {
		List<CalculationEntity> organisations = new ArrayList<CalculationEntity>();
		organisations.addAll(entity.getChildren(getSkipLocationLevels()));
		for (DataEntity dataEntity : entity.getDataEntities(getSkipLocationLevels())) {
			if (groups.contains(dataEntity.getType())) organisations.add(dataEntity);
		}
		
		List<DashboardObjectiveEntry> weightedObjectives = objective.getObjectiveEntries();
		List<LocationEntity> organisationPath = calculateOrganisationPath(entity);
		List<DashboardObjective> objectivePath = calculateObjectivePath(objective);
		return new Dashboard(organisations, weightedObjectives, 
				organisationPath, objectivePath,
				getValues(organisations, weightedObjectives, period, groups));
	}

	@Transactional(readOnly = true)
	public Info<?> getExplanation(CalculationEntity entity, DashboardEntry entry, Period period, Set<DataEntityType> groups) {
		return entry.visit(new ExplanationVisitor(groups), entity, period);
	}

	private class ExplanationVisitor implements DashboardVisitor<Info> {

		private Set<DataEntityType> groups;
		
		public ExplanationVisitor(Set<DataEntityType> groups) {
			this.groups = groups;
		}
		
		@Override
		public Info visitObjective(DashboardObjective objective, CalculationEntity entity, Period period) {
			DashboardPercentage percentage = objective.visit(new PercentageVisitor(groups), entity, period);
			if (percentage == null) return null;
			Map<DashboardObjectiveEntry, DashboardPercentage> values = getValues(objective.getObjectiveEntries(), period, entity, groups);
			return new DashboardObjectiveInfo(percentage, values);
		}

		@Override
		public Info visitTarget(DashboardTarget target, CalculationEntity entity, Period period) {
			return infoService.getCalculationInfo(target.getCalculation(), entity, period, groups);
		}
		
	}
	
	private static Type type = Type.TYPE_NUMBER();
	
	private class PercentageVisitor implements DashboardVisitor<DashboardPercentage> {
		
		private Set<DataEntityType> groups;
		
		public PercentageVisitor(Set<DataEntityType> groups) {
			this.groups = groups;
		}

		private final Log log = LogFactory.getLog(PercentageVisitor.class);
		
		@Override
		public DashboardPercentage visitObjective(DashboardObjective objective, CalculationEntity entity, Period period) {
			if (log.isDebugEnabled()) log.debug("visitObjective(objective="+objective+",entity="+entity+",period="+period+")");
			
			Integer totalWeight = 0;
			Double sum = 0.0d;

			for (DashboardObjectiveEntry child : objective.getObjectiveEntries()) {
				DashboardPercentage childPercentage = child.getEntry().visit(this, entity, period);
				if (childPercentage == null) {
					if (log.isErrorEnabled()) log.error("found null percentage, objective: "+child.getEntry()+", entity: "+entity+", period: "+period);
					return null;
				}
				Integer weight = child.getWeight();
				if (childPercentage.isValid()) {
					sum += childPercentage.getGradientValue() * weight;
					totalWeight += weight;
				}
				else {
					// MISSING_EXPRESSION - we skip it
					// MISSING_NUMBER - should we count it in as zero ?
				}

			}
			// TODO what if sum = 0 and totalWeight = 0 ?
			Double average = sum/totalWeight;
			Value value = null;
			if (average.isNaN() || average.isInfinite()) value = Value.NULL;
			else value = type.getValue(average);
			DashboardPercentage percentage = new DashboardPercentage(value, entity, period);
			
			if (log.isDebugEnabled()) log.debug("visitObjective()="+percentage);
			return percentage;
		}

		@Override
		public DashboardPercentage visitTarget(DashboardTarget target, CalculationEntity entity, Period period) {
			if (log.isDebugEnabled()) log.debug("visitTarget(target="+target+",entity="+entity+",period="+period+")");
			
			CalculationValue<?> calculationValue = valueService.getCalculationValue(target.getCalculation(), entity, period, groups);
			if (calculationValue == null) return null;
			DashboardPercentage percentage = new DashboardPercentage(calculationValue.getValue(), entity, period);

			if (log.isDebugEnabled()) log.debug("visitTarget(...)="+percentage);
			return percentage;
		}
	}
	
	private Map<CalculationEntity, Map<DashboardObjectiveEntry, DashboardPercentage>> getValues(List<CalculationEntity> organisations, List<DashboardObjectiveEntry> objectiveEntries, Period period, Set<DataEntityType> groups) {
		Map<CalculationEntity, Map<DashboardObjectiveEntry, DashboardPercentage>> values = new HashMap<CalculationEntity, Map<DashboardObjectiveEntry, DashboardPercentage>>();

		for (CalculationEntity entity : organisations) {
			Map<DashboardObjectiveEntry, DashboardPercentage> organisationMap = getValues(objectiveEntries, period, entity, groups);
			values.put(entity, organisationMap);
		}
		return values;
	}

	private Map<DashboardObjectiveEntry, DashboardPercentage> getValues(List<DashboardObjectiveEntry> objectiveEntries, Period period, CalculationEntity entity, Set<DataEntityType> groups) {
		Map<DashboardObjectiveEntry, DashboardPercentage> organisationMap = new HashMap<DashboardObjectiveEntry, DashboardPercentage>();
		for (DashboardObjectiveEntry objectiveEntry : objectiveEntries) {
			DashboardPercentage percentage = objectiveEntry.getEntry().visit(new PercentageVisitor(groups), entity, period);
			organisationMap.put(objectiveEntry, percentage);
		}
		return organisationMap;
	}
	
	private List<LocationEntity> calculateOrganisationPath(LocationEntity entity) {
		List<LocationEntity> organisationPath = new ArrayList<LocationEntity>();
		LocationEntity parent = entity;
		while ((parent = parent.getParent()) != null) {
			organisationPath.add(parent);
		}
		Collections.reverse(organisationPath);
		return organisationPath;
	}
	
	private List<DashboardObjective> calculateObjectivePath(DashboardObjective objective) {
		List<DashboardObjective> objectivePath = new ArrayList<DashboardObjective>();
		DashboardObjectiveEntry parent = objective.getParent();
		while (parent != null) {
			objective = parent.getParent();
			objectivePath.add(objective);
			parent = objective.getParent();
		}
		Collections.reverse(objectivePath);
		return objectivePath;
	}
	
	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setSkipLevels(Set<String> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public Integer[] getSkipLevelArray() {
		return skipLevels.toArray(new Integer[skipLevels.size()]);
	}
}

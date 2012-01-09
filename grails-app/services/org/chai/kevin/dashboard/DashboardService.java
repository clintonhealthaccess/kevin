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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.data.Info;
import org.chai.kevin.data.InfoService;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportObjective;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class DashboardService {

//	private Log log = LogFactory.getLog(DashboardService.class);
	
	private ReportService reportService;
	private OrganisationService organisationService;
	private InfoService infoService;
	private ValueService valueService;	
	private Set<Integer> skipLevels;
	
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setSkipLevels(Set<Integer> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public Integer[] getSkipLevelArray() {
		return skipLevels.toArray(new Integer[skipLevels.size()]);
	}
	
	@Transactional(readOnly = true)
	@Cacheable("dashboardCache")
	public Dashboard getDashboard(Organisation organisation, ReportObjective objective, Period period, Set<String> groupUuids) {
		organisationService.loadChildren(organisation, getSkipLevelArray());
		
		List<Organisation> organisations = new ArrayList<Organisation>();
		for (Organisation child : organisation.getChildren()) {
			organisationService.loadGroup(child);
						
			if (organisationService.loadLevel(child) != organisationService.getFacilityLevel()
				||  groupUuids.contains(child.getOrganisationUnitGroup().getUuid())) {
				organisations.add(child);
				organisationService.loadChildren(child, getSkipLevelArray());
			}
		}
		Organisation parent = organisation;
		while (organisationService.loadParent(parent, getSkipLevelArray())) {
			parent = parent.getParent();
		}
		
		List<DashboardEntity> dashboardEntities = reportService.getDashboardEntities(objective);
		
		List<Organisation> organisationPath = calculateOrganisationPath(organisation);
		List<DashboardObjective> objectivePath = getBreadcrumb(objective);
		return new Dashboard(organisations, dashboardEntities, 
				organisationPath, objectivePath,
				getValues(organisations, dashboardEntities, period, groupUuids));
	}

	@Transactional(readOnly = true)
	public Info<?> getExplanation(Organisation organisation, DashboardObjective objective, Period period, Set<String> groupUuids) {
		organisationService.loadChildren(organisation, getSkipLevelArray());
		organisationService.loadParent(organisation, getSkipLevelArray());
		organisationService.loadGroup(organisation);
		organisationService.loadLevel(organisation);
				
		return objective.visit(new ExplanationVisitor(groupUuids), organisation, period);
	}

	private class ExplanationVisitor implements DashboardVisitor<Info> {

		private Set<String> groupUuids;
		
		public ExplanationVisitor(Set<String> groupUuids) {
			this.groupUuids = groupUuids;
		}
		
		@Override
		public Info visitObjective(DashboardObjective objective, Organisation organisation, Period period) {
			DashboardPercentage percentage = objective.visit(new PercentageVisitor(groupUuids), organisation, period);
			if (percentage == null) return null;
			List<DashboardEntity> dashboardEntities = reportService.getDashboardEntities(objective.getObjective());
			Map<DashboardEntity, DashboardPercentage> values = getValues(dashboardEntities, period, organisation, groupUuids);
			return new DashboardObjectiveInfo(percentage, values);
		}

		@Override
		public Info visitTarget(DashboardTarget target, Organisation organisation, Period period) {
			return infoService.getCalculationInfo(target.getCalculation(), organisation, period, groupUuids);
		}
		
	}
	
	private static Type type = Type.TYPE_NUMBER();
	
	private class PercentageVisitor implements DashboardVisitor<DashboardPercentage> {
		
		private Set<String> groupUuids;
		
		public PercentageVisitor(Set<String> groupUuids) {
			this.groupUuids = groupUuids;
		}

		private final Log log = LogFactory.getLog(PercentageVisitor.class);
		
		@Override
		public DashboardPercentage visitObjective(DashboardObjective objective, Organisation organisation, Period period) {
			if (log.isDebugEnabled()) log.debug("visitObjective(objective="+objective+",organisation="+organisation+",period="+period+")");
			
			Integer totalWeight = 0;
			Double sum = 0.0d;

			List<DashboardEntity> dashboardEntities = reportService.getDashboardEntities(objective.getObjective());
			
			for (DashboardEntity child : dashboardEntities) {
				DashboardPercentage childPercentage = child.visit(this, organisation, period);
				if (childPercentage == null) {
					if (log.isErrorEnabled()) log.error("found null percentage, objective: "+child+", organisation: "+organisation.getOrganisationUnit()+", period: "+period);
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
			DashboardPercentage percentage = new DashboardPercentage(value, organisation.getOrganisationUnit(), period);
			
			if (log.isDebugEnabled()) log.debug("visitObjective()="+percentage);
			return percentage;
		}

		@Override
		public DashboardPercentage visitTarget(DashboardTarget target, Organisation organisation, Period period) {
			if (log.isDebugEnabled()) log.debug("visitTarget(target="+target+",organisation="+organisation+",period="+period+")");
			
			CalculationValue<?> calculationValue = valueService.getCalculationValue(target.getCalculation(), organisation.getOrganisationUnit(), period, groupUuids);
			if (calculationValue == null) return null;
			DashboardPercentage percentage = new DashboardPercentage(calculationValue.getValue(), organisation.getOrganisationUnit(), period);

			if (log.isDebugEnabled()) log.debug("visitTarget(...)="+percentage);
			return percentage;
		}
	}
	
	private Map<Organisation, Map<DashboardEntity, DashboardPercentage>> getValues(List<Organisation> organisations, List<DashboardEntity> dashboardEntities, Period period, Set<String> groupUuids) {
		Map<Organisation, Map<DashboardEntity, DashboardPercentage>> values = new HashMap<Organisation, Map<DashboardEntity, DashboardPercentage>>();

		for (Organisation organisation : organisations) {
			Map<DashboardEntity, DashboardPercentage> organisationMap = getValues(dashboardEntities, period, organisation, groupUuids);
			values.put(organisation, organisationMap);
		}
		return values;
	}

	private Map<DashboardEntity, DashboardPercentage> getValues(List<DashboardEntity> entities, Period period, Organisation organisation, Set<String> groupUuids) {
		Map<DashboardEntity, DashboardPercentage> organisationMap = new HashMap<DashboardEntity, DashboardPercentage>();
		for (DashboardEntity entity : entities) {
			DashboardPercentage percentage = entity.visit(new PercentageVisitor(groupUuids), organisation, period);
			organisationMap.put(entity, percentage);
		}
		return organisationMap;
	}
	
	private List<Organisation> calculateOrganisationPath(Organisation organisation) {
		List<Organisation> organisationPath = new ArrayList<Organisation>();
		Organisation parent = organisation;
		while ((parent = parent.getParent()) != null) {
			organisationPath.add(parent);
		}
		Collections.reverse(organisationPath);
		return organisationPath;
	}
	
	private List<DashboardObjective> getBreadcrumb(ReportObjective objective) {
		List<DashboardObjective> objectivePath = new ArrayList<DashboardObjective>();
		while (objective != null) {
			ReportObjective parent = objective.getParent();
			if(parent != null) {
				DashboardObjective dashboardParent = (DashboardObjective) reportService.getDashboardEntity(parent);
				if(dashboardParent != null)	objectivePath.add(dashboardParent);
			}
			objective = parent;
		}
		Collections.reverse(objectivePath);
		return objectivePath;
	}

}

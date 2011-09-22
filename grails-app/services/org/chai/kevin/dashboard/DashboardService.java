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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.ExpressionService;
import org.chai.kevin.InfoService;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.ValueService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class DashboardService {

//	private Log log = LogFactory.getLog(DashboardService.class);
	
	private OrganisationService organisationService;
	private InfoService infoService;
	private ValueService valueService;
	private ExpressionService expressionService;
	
	private Set<Integer> skipLevels;
	
	@Transactional(readOnly = false)
	public Dashboard getDashboard(Organisation organisation, DashboardObjective objective, Period period) {
		organisationService.loadChildren(organisation, getSkipLevelArray());
		for (Organisation child : organisation.getChildren()) {
			organisationService.loadChildren(child, getSkipLevelArray());
			organisationService.loadGroup(child);
		}
		Organisation parent = organisation;
		while (organisationService.loadParent(parent, getSkipLevelArray())) {
			parent = parent.getParent();
		}
		
		Set<OrganisationUnitGroup> facilityTypeSet = new LinkedHashSet<OrganisationUnitGroup>();
		for (Organisation child : organisation.getChildren()) {
			if (child.getOrganisationUnitGroup() != null) facilityTypeSet.add(child.getOrganisationUnitGroup());
		}
		List<OrganisationUnitGroup> facilityTypes = new ArrayList<OrganisationUnitGroup>(facilityTypeSet);

		List<Organisation> organisations = organisation.getChildren();
		List<DashboardObjectiveEntry> weightedObjectives = objective.getObjectiveEntries();
		List<Organisation> organisationPath = calculateOrganisationPath(organisation);
		List<DashboardObjective> objectivePath = calculateObjectivePath(objective);
		return new Dashboard(organisation, objective, period, 
				organisations, weightedObjectives, 
				organisationPath, objectivePath, facilityTypes,
				getValues(organisations, weightedObjectives, period));
	}

	@Transactional(readOnly = false)
	public DashboardExplanation getExplanation(Organisation organisation, DashboardEntry entry, Period period) {
		organisationService.loadChildren(organisation, getSkipLevelArray());
		organisationService.loadParent(organisation, getSkipLevelArray());
		organisationService.loadGroup(organisation);
		organisationService.getLevel(organisation);
		
		ExplanationCalculator calculator = createExplanationCalculator();
		return entry.getExplanation(calculator, organisation, period, organisationService.getFacilityLevel() == organisationService.getLevel(organisation));
	}
	
	private ExplanationCalculator createExplanationCalculator() {
		ExplanationCalculator calculator = new ExplanationCalculator();
		calculator.setOrganisationService(organisationService);
		calculator.setInfoService(infoService);
		calculator.setExpressionService(expressionService);
		calculator.setValueService(valueService);
		return calculator;
	}

	
	private PercentageCalculator createCalculator() {
		PercentageCalculator calculator = new PercentageCalculator();
		calculator.setOrganisationService(organisationService);
		calculator.setExpressionService(expressionService);
		calculator.setValueService(valueService);
		return calculator;
	}
	
	private Map<Organisation, Map<DashboardEntry, DashboardPercentage>> getValues(List<Organisation> organisations, List<DashboardObjectiveEntry> objectiveEntries, Period period) {
		Map<Organisation, Map<DashboardEntry, DashboardPercentage>> values = new HashMap<Organisation, Map<DashboardEntry, DashboardPercentage>>();
		PercentageCalculator calculator = createCalculator();

		for (Organisation organisation : organisations) {
			Map<DashboardEntry, DashboardPercentage> organisationMap = new HashMap<DashboardEntry, DashboardPercentage>();
			for (DashboardObjectiveEntry objectiveEntry : objectiveEntries) {
				DashboardPercentage percentage = objectiveEntry.getEntry().getValue(calculator, organisation, period, organisationService.getFacilityLevel() == organisationService.getLevel(organisation));
				organisationMap.put(objectiveEntry.getEntry(), percentage);
			}
			values.put(organisation, organisationMap);
		}
		return values;
	}
	
	public List<Organisation> calculateOrganisationPath(Organisation organisation) {
		List<Organisation> organisationPath = new ArrayList<Organisation>();
		Organisation parent = organisation;
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
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
	
	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}
	
	public void setSkipLevels(Set<Integer> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public Integer[] getSkipLevelArray() {
		return skipLevels.toArray(new Integer[skipLevels.size()]);
	}
}

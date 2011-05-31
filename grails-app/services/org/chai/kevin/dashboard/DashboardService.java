package org.chai.kevin.dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.ProgressListener;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class DashboardService {

	private Log log = LogFactory.getLog(DashboardService.class);
	
	private DashboardObjectiveService dashboardObjectiveService;
	private OrganisationService organisationService;
	private PercentageService percentageService;
	private OrganisationUnitService organisationUnitService;
	private ExpressionService expressionService;
	private PeriodService periodService;
	
	private Set<String> skipLevels;
	
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

	public Explanation getExplanation(Organisation organisation, DashboardEntry entry, Period period) {
		organisationService.loadChildren(organisation, getSkipLevelArray());
		organisationService.loadParent(organisation, getSkipLevelArray());
		organisationService.loadGroup(organisation);
		organisationService.getLevel(organisation);
		
		ExplanationCalculator calculator = createExplanationCalculator();
		return entry.getExplanation(calculator, organisation, period);
	}
	
	private ExplanationCalculator createExplanationCalculator() {
		ExplanationCalculator calculator = new ExplanationCalculator();
		calculator.setPercentageService(percentageService);
		calculator.setExpressionService(expressionService);
		calculator.setOrganisationUnitService(organisationUnitService);
		calculator.setGroupCollection(new GroupCollection(organisationService.getGroupsForExpression()));
		return calculator;
	}

	@Transactional
	public void refreshDashboard(Organisation organisation, DashboardObjective objective, Period period, ProgressListener listener) {
		if (log.isInfoEnabled()) log.info("refreshDashboard(organisation="+organisation+", objective="+objective+", period="+period+")");
		
		Set<DashboardEntry> targets = new HashSet<DashboardEntry>(dashboardObjectiveService.getTargets());
		List<Organisation> organisations = new ArrayList<Organisation>();
		getOrganisations(organisation, organisations);
		
		PercentageCalculator calculator = createCalculator();

		int updates = 0;
		while (!targets.isEmpty() && !targets.contains(objective)) {
			updates += organisations.size() * targets.size();
			targets = getParents(targets);
		}
		if (log.isInfoEnabled()) log.info("setting total to: "+updates);
		listener.setTotal(updates);
		
		targets = new HashSet<DashboardEntry>(dashboardObjectiveService.getTargets());
		
		while (!targets.isEmpty() && !targets.contains(objective)) {
			deepRefreshDashboard(organisations, targets, period, calculator, listener);
			if (listener.isInterrupted()) return;
			targets = getParents(targets);
		}
	}
	
	@Transactional
	public void refreshEntireDashboard(ProgressListener listener) {
		Organisation rootOrganisation = organisationService.getRootOrganisation();
		Set<DashboardEntry> targets = new HashSet<DashboardEntry>(dashboardObjectiveService.getTargets());
		List<Organisation> organisations = new ArrayList<Organisation>();
		getOrganisations(rootOrganisation, organisations);
		
		PercentageCalculator calculator = createCalculator();
		
		for (Period period : periodService.getAllPeriods()) {
			while (targets.size() > 0) {
				deepRefreshDashboard(organisations, targets, period, calculator, listener);
				if (listener.isInterrupted()) return;
				targets = getParents(targets);
			}
		}
	}
	
	private void getOrganisations(Organisation organisation, List<Organisation> organisations) {
		organisationService.loadChildren(organisation, getSkipLevelArray());
		for (Organisation child : organisation.getChildren()) {
			getOrganisations(child, organisations);
		}
		if (organisationService.loadParent(organisation, getSkipLevelArray())) organisations.add(organisation);
	}
	
	private Set<DashboardEntry> getParents(Set<DashboardEntry> entries) {
		Set<DashboardEntry> parents = new HashSet<DashboardEntry>();
		for (DashboardEntry entry : entries) {
			if (entry.getParent() != null) { 
				DashboardObjective parent = entry.getParent().getParent();
				if (parent != null && parent.getParent() != null) parents.add(parent);
			}
		}
		return parents;
	}
	
	private void deepRefreshDashboard(List<Organisation> organisations, Set<DashboardEntry> entries, Period period, PercentageCalculator calculator, ProgressListener listener) {
		for (Organisation organisation : organisations) {
			for (DashboardEntry entry : entries) {
				if (listener.isInterrupted()) return;
				organisationService.loadGroup(organisation);
				DashboardPercentage percentage = entry.getValue(calculator, organisation, period);
				percentageService.updatePercentage(percentage);
				listener.increment();
			}
		}
	}
	
	private PercentageCalculator createCalculator() {
		PercentageCalculator calculator = new PercentageCalculator();
		calculator.setExpressionService(expressionService);
		calculator.setPercentageService(percentageService);
		calculator.setGroupCollection(new GroupCollection(organisationService.getGroupsForExpression()));
		return calculator;
	}
	
	private Map<Organisation, Map<DashboardEntry, DashboardPercentage>> getValues(List<Organisation> organisations, List<DashboardObjectiveEntry> objectiveEntries, Period period) {
		Map<Organisation, Map<DashboardEntry, DashboardPercentage>> values = new HashMap<Organisation, Map<DashboardEntry, DashboardPercentage>>();
		for (Organisation organisation : organisations) {
			Map<DashboardEntry, DashboardPercentage> organisationMap = new HashMap<DashboardEntry, DashboardPercentage>();
			for (DashboardObjectiveEntry objectiveEntry : objectiveEntries) {
				DashboardPercentage percentage = percentageService.getPercentage(organisation.getOrganisationUnit(), objectiveEntry.getEntry(), period);
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
	
	public void setDashboardObjectiveService(DashboardObjectiveService dashboardObjectiveService) {
		this.dashboardObjectiveService = dashboardObjectiveService;
	}	

	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
	public void setPercentageService(PercentageService percentageService) {
		this.percentageService = percentageService;
	}
	
	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}
	
	public void setOrganisationUnitService(
			OrganisationUnitService organisationUnitService) {
		this.organisationUnitService = organisationUnitService;
	}
	
	public void setPeriodService(PeriodService periodService) {
		this.periodService = periodService;
	}
	
	public void setSkipLevels(Set<String> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public String[] getSkipLevelArray() {
		return skipLevels.toArray(new String[skipLevels.size()]);
	}
}

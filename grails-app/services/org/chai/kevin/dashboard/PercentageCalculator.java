package org.chai.kevin.dashboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Expression;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.dashboard.DashboardPercentage.Status;
import org.hisp.dhis.common.AbstractNameableObject;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;

public class PercentageCalculator {

	// TODO this should be private
	protected ExpressionService expressionService;
	protected OrganisationService organisationService;
	protected PercentageService percentageService;
	private GroupCollection groupCollection;

	private static Log log = LogFactory.getLog(PercentageCalculator.class);
	
	public DashboardPercentage getPercentage(DashboardObjective objective, Organisation organisation, Period period) {
		if (log.isDebugEnabled()) log.debug("getPercentageForObjective(objective: "+objective+", organisation: "+organisation+")");
		
		return getValueForObjective(objective, organisation, period, new HashMap<DashboardObjectiveEntry, DashboardPercentage>());
	}

	public DashboardPercentage getPercentage(DashboardTarget target, Organisation organisation, Period period) {
		if (log.isDebugEnabled()) log.debug("getPercentageForTarget(target: "+target+", organisation: "+organisation+")");

		DashboardCalculation matchingCalculation = getMatchingCalculation(target, organisation);
		
		DashboardPercentage percentage;
		if (!isValid(matchingCalculation) && organisation.getChildren().size() == 0) {
			if (log.isInfoEnabled()) log.info("no matching groups and organisation has no children, organisation: "+organisation+", objective: "+target);
			percentage = new DashboardPercentage(Status.MISSING_EXPRESSION, organisation.getOrganisationUnit(), target, period);
		}
		else {
			if (isValid(matchingCalculation)) {
				percentage = getValueForLeafTarget(matchingCalculation.getExpression(), target, organisation, period, new HashMap<AbstractNameableObject, Object>());
			}
			else {
				percentage = getValueForNonLeafTarget(target, organisation, period, new HashMap<Organisation, DashboardPercentage>());
			}
		}
		return percentage;
	}
	
	protected static boolean isValid(DashboardCalculation calculation) {
		return calculation==null?false:calculation.getExpression()!=null;
	}
	
	
	protected DashboardPercentage getValueForObjective(DashboardObjective objective, Organisation organisation, Period period, Map<DashboardObjectiveEntry, DashboardPercentage> values) {
		Integer totalWeight = 0;
		Double sum = 0.0d;
		boolean hasMissingExpression = false;
		boolean hasMissingValue = false;
		for (DashboardObjectiveEntry child : objective.getObjectiveEntries()) {
			DashboardPercentage childPercentage = percentageService.getPercentage(organisation.getOrganisationUnit(), child.getEntry(), period);
			if (childPercentage == null) {
				if (log.isErrorEnabled()) log.error("found null percentage, objective: "+child.getEntry()+", organisation: "+organisation.getOrganisationUnit()+", period: "+period);
				continue;
			}
			Integer weight = child.getWeight();
			if (childPercentage.isValid()) {
				sum += childPercentage.getValue() * weight;
				totalWeight += weight;
			}
			else {
				// MISSING_EXPRESSION - we skip it
				// MISSING_VALUE - should we count it in as zero ?
			}

			if (childPercentage.isHasMissingExpression()) {
				hasMissingExpression = true;
			}
			if (childPercentage.isHasMissingValue()) {
				hasMissingValue = true;
			}
			values.put(child, childPercentage);
		}
		return new DashboardPercentage(sum / totalWeight, organisation.getOrganisationUnit(), objective, period, hasMissingValue, hasMissingExpression);
	}

	protected DashboardPercentage getValueForLeafTarget(Expression expression, DashboardTarget target, Organisation organisation, Period period, Map<AbstractNameableObject, Object> values) {
		Double value = (Double)expressionService.getValue(expression, period, organisation, values);
		
		if (ExpressionService.hasNullValues(values.values())) {
			return new DashboardPercentage(Status.MISSING_VALUE, organisation.getOrganisationUnit(), target, period);
		}
		else {
			return new DashboardPercentage(value, organisation.getOrganisationUnit(), target, period);
		}
	}
	
	protected DashboardPercentage getValueForNonLeafTarget(DashboardTarget target, Organisation organisation, Period period, Map<Organisation, DashboardPercentage> values) {
		// TODO valuation function for organisation units (based on data element value, eg population)
		Double sum = 0.0d;
		Integer total = 0;
		boolean hasMissingExpression = false;
		boolean hasMissingValue = false;
		for (Organisation child : organisation.getChildren()) {
			DashboardPercentage childPercentage = percentageService.getPercentage(child.getOrganisationUnit(), target, period);
			if (childPercentage.isValid()) {
				sum += childPercentage.getValue();
				total++;
			}
			else {
				// MISSING_EXPRESSION - we skip it
				// MISSING_VALUE - should we count it in as zero ?
			}
			
			if (childPercentage.isHasMissingExpression()) {
				hasMissingExpression = true;
			}
			if (childPercentage.isHasMissingValue()) {
				hasMissingValue = true;
			}
			values.put(child, childPercentage);
		}
		return new DashboardPercentage(sum / total, organisation.getOrganisationUnit(), target, period, hasMissingValue, hasMissingExpression);
	}
	
	public DashboardCalculation getMatchingCalculation(DashboardTarget target, Organisation organisation) {
		Set<OrganisationUnitGroup> groups = organisation.getOrganisationUnit().getGroups();
		if (log.isDebugEnabled()) log.debug("groups on organisation: "+groups);
		if (log.isDebugEnabled()) log.debug("groups on calculations: "+target.getCalculations());

		DashboardCalculation matchingCalculation = null;
		for (DashboardCalculation calculation : target.getCalculations().values()) {
			if (groups.contains(groupCollection.getGroupByUuid(calculation.getGroupUuid()))) {  
				matchingCalculation = calculation;
			}
		}
		return matchingCalculation;
	}
	
	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}

	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
	public void setPercentageService(PercentageService percentageService) {
		this.percentageService = percentageService;
	}
	
	public void setGroupCollection(GroupCollection groupCollection) {
		this.groupCollection = groupCollection;
	}
	
}
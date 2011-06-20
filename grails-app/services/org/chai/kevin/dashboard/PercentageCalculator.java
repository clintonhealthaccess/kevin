package org.chai.kevin.dashboard;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.DataElement;
import org.chai.kevin.Expression;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.ValueService;
import org.chai.kevin.ValueType;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ExpressionValue;
import org.chai.kevin.value.ExpressionValue.Status;
import org.hisp.dhis.period.Period;

public class PercentageCalculator {

	// TODO this should be private
	protected ValueService valueService;
	protected OrganisationService organisationService;
	protected ExpressionService expressionService;
	
	private static Log log = LogFactory.getLog(PercentageCalculator.class);
	
	public DashboardPercentage getPercentageForObjective(DashboardObjective objective, Organisation organisation, Period period) {
		if (log.isDebugEnabled()) log.debug("getPercentageForObjective(objective: "+objective+", organisation: "+organisation+")");
		
		DashboardPercentage percentage = getValueForObjective(objective, organisation, period);
		if (log.isDebugEnabled()) log.debug("getPercentageForObjective()="+percentage);
		return percentage;
	}

	public DashboardPercentage getPercentageForNonLeafTarget(DashboardTarget target, Organisation organisation, Period period) {
		if (log.isDebugEnabled()) log.debug("getPercentageForNonLeafTarget(target: "+target+", organisation: "+organisation+")");
		
		// calculation has to be of VALUE type
		if (target.getCalculation().getType() != ValueType.VALUE) log.error("dashboard target has calculation of invalid type: "+target.getCalculation());

		// but we try anyway, can be a user mistake
		CalculationValue calculationValue = valueService.getCalculationValue(organisation.getOrganisationUnit(), target.getCalculation(), period);
		if (calculationValue == null) return null;
		DashboardPercentage percentage = new DashboardPercentage(calculationValue.getAverage(), calculationValue.getHasMissingValues(), calculationValue.getHasMissingExpression());

		if (log.isDebugEnabled()) log.debug("getPercentageForNonLeafTarget()="+percentage);
		return percentage;
	}
	
	public DashboardPercentage getPercentageForLeafTarget(DashboardTarget target, Organisation organisation, Period period) {
		if (log.isDebugEnabled()) log.debug("getPercentageForLeafTarget(target: "+target+", organisation: "+organisation+")");
		
		// calculation has to be of VALUE type
		if (target.getCalculation().getType() != ValueType.VALUE) log.error("dashboard target has calculation of invalid type: "+target.getCalculation());

		// but we try anyway, can be a user mistake
		DashboardPercentage percentage;
		Expression expression = expressionService.getMatchingExpression(target.getCalculation(), organisation);
		if (expression != null) {
			ExpressionValue expressionValue = valueService.getExpressionValue(organisation.getOrganisationUnit(), expression, period);
			if (expressionValue == null) return null;
			percentage = new DashboardPercentage(expressionValue.getNumberValue(), expressionValue.getStatus() == Status.MISSING_VALUE, false);
		}
		else {
			percentage = new DashboardPercentage(null, false, true);
		}
		if (log.isDebugEnabled()) log.debug("getPercentageForLeafTarget()="+percentage);
		return percentage;
	}
	
	protected Map<DashboardObjectiveEntry, DashboardPercentage> getValues(DashboardObjective objective, Organisation organisation, Period period){
		Map<DashboardObjectiveEntry, DashboardPercentage> result = new HashMap<DashboardObjectiveEntry, DashboardPercentage>();
		for (DashboardObjectiveEntry child : objective.getObjectiveEntries()) {
			DashboardPercentage childPercentage = child.getEntry().getValue(this, organisation, period, organisationService.getFacilityLevel() == organisationService.getLevel(organisation));
			result.put(child, childPercentage);
		}
		return result;
	}
	
	protected DashboardPercentage getValueForObjective(DashboardObjective objective, Organisation organisation, Period period) {
		Integer totalWeight = 0;
		Double sum = 0.0d;
		boolean hasMissingExpression = false;
		boolean hasMissingValue = false;
		for (DashboardObjectiveEntry child : objective.getObjectiveEntries()) {
			DashboardPercentage childPercentage = child.getEntry().getValue(this, organisation, period, organisationService.getFacilityLevel() == organisationService.getLevel(organisation));
			if (childPercentage == null) {
				if (log.isErrorEnabled()) log.error("found null percentage, objective: "+child.getEntry()+", organisation: "+organisation.getOrganisationUnit()+", period: "+period);
				return null;
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
		}
		// TODO what if sum = 0 and totalWeight = 0 ?
		return new DashboardPercentage(sum / totalWeight, hasMissingValue, hasMissingExpression);
	}

	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}
	
}
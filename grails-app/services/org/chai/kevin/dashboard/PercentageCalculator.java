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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.ValueService;
import org.chai.kevin.data.Expression;
import org.chai.kevin.data.ValueType;
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
		CalculationValue calculationValue = valueService.getValue(target.getCalculation(), organisation.getOrganisationUnit(), period);
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
		Expression expression = expressionService.getMatchingExpression(target.getCalculation().getExpressions(), organisation);
		if (expression != null) {
			ExpressionValue expressionValue = valueService.getValue(expression, organisation.getOrganisationUnit(), period);
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
package org.chai.kevin;

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

import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Expression;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionValue;
import org.hisp.dhis.period.Period;

public class InfoService {

	private ValueService valueService;
	private ExpressionService expressionService;
	private OrganisationService organisationService;
	private int groupLevel;
	
	public ExpressionInfo getInfo(Expression expression, Organisation organisation, Period period) {
		ExpressionValue expressionValue = valueService.getValue(expression, organisation.getOrganisationUnit(), period);
		if (expressionValue == null) return null;
		Map<Organisation, Map<DataElement, DataValue>> calculateDataValues = expressionService.calculateDataValues(expression, period, organisation);
		return new ExpressionInfo(expressionValue, calculateDataValues);
	}
	
	public CalculationInfo getInfo(Calculation calculation, Organisation organisation, Period period) {
		CalculationValue calculationValue = valueService.getValue(calculation, organisation.getOrganisationUnit(), period);
		if (calculationValue == null) return null;
		
		List<Organisation> groupOrganisations = null;
		if (groupLevel != 0 && organisation.getLevel() < groupLevel) {
			groupOrganisations = organisationService.getOrganisationsOfLevel(groupLevel);
			for (Organisation groupOrganisation : groupOrganisations) {
				organisationService.loadUntilLevel(groupOrganisation, organisationService.getFacilityLevel());
			}
		}
		Map<Organisation, ExpressionValue> expressionValues = expressionService.calculateExpressionValues(calculation, period, organisation);
		CalculationInfo info = new CalculationInfo(calculationValue, groupOrganisations, expressionValues);
		return info;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
	public void setGroupLevel(int groupLevel) {
		this.groupLevel = groupLevel;
	}
}

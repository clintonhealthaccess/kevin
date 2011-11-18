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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chai.kevin.data.Calculation;
import org.chai.kevin.value.CalculationPartialValue;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.StoredValue;
import org.chai.kevin.value.Value;
import org.hisp.dhis.organisationunit.OrganisationUnit;

public class CalculationInfo extends Info<CalculationValue<?>> {

	private CalculationPartialValue calculationValue;
	private List<Organisation> groups;
	private Map<Organisation, NormalizedDataElementValue> expressionValues;
	
	public CalculationInfo(CalculationValue<?> calculationValue, Map<Organisation, NormalizedDataElementValue> expressionValues) {
		this.calculationValue = calculationValue;
		this.expressionValues = expressionValues;
	}

	public CalculationInfo(CalculationPartialValue calculationValue, List<Organisation> groups,
			Map<Organisation, NormalizedDataElementValue> expressionValues) {
		this.calculationValue = calculationValue;
		this.groups = groups;
		this.expressionValues = expressionValues;
	}
	
	public List<Organisation> getGroups() {
		return groups;
	}
	
	public Map<Organisation, NormalizedDataElementValue> getExpressionValuesForGroup(Organisation organisation) {
		Map<Organisation, NormalizedDataElementValue> result = new HashMap<Organisation, NormalizedDataElementValue>();
		for (Organisation child : expressionValues.keySet()) {
			if (organisation.hasChild(child)) result.put(child, expressionValues.get(child));
		}
		return result;
	}
	
	public Calculation getCalculation() {
		return calculationValue.getCalculation();
	}
	
	public OrganisationUnit getOrganisation() {
		return calculationValue.getOrganisationUnit();
	}
	
	public StoredValue getCalculationValue() {
		return calculationValue;
	}
	

	public Map<Organisation, NormalizedDataElementValue> getExpressionValues() {
		return expressionValues;
	}
	
	public Value getValue() {
		return calculationValue.getValue();
	}

	public String getTemplate() {
		return "/info/calculationInfo";
	}
	
}

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

import java.util.Map;
import java.util.Map.Entry;

import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Expression;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;

public class ExpressionInfo extends Info {

	private Double maxValue;
	private ExpressionValue expressionValue;
	private Map<Organisation, Map<DataElement, DataValue>> values;
	
	public ExpressionInfo(ExpressionValue expressionValue, Map<Organisation, Map<DataElement, DataValue>> values, Double maxValue) {
		this.maxValue = maxValue;
		this.expressionValue = expressionValue;
		this.values = values;
	}

	public ExpressionValue getExpressionValue() {
		return expressionValue;
	}
	
	public Expression getExpression() {
		return expressionValue.getExpression();
	}
	
	public OrganisationUnit getOrganisation() {
		return expressionValue.getOrganisationUnit();
	}
	
	public Map<Organisation, Map<DataElement, DataValue>> getValues() {
		return values;
	}
	
	public Map<DataElement, DataValue> getValuesForOrganisation() {
		for (Entry<Organisation, Map<DataElement, DataValue>> entry : values.entrySet()) {
			if (entry.getKey().getOrganisationUnit().equals(expressionValue.getOrganisationUnit())) return entry.getValue();
		}
		return null;
	}
	
	public Double getNumberValue() {
		Double value = null;
		if (getValue() != null) {
			value = Double.parseDouble(getValue());
			if (getMaxValue() != null) {
				value = value / getMaxValue();
			}
		}
		return value;
	}
	
	public Double getMaxValue() {
		return maxValue;
	}
	
	
	public String getValue() {
		if (expressionValue.getValue() == null) return null;
		return String.valueOf(expressionValue.getValue());
	}

	@Override
	public String getTemplate() {
		return "/info/expressionInfo";
	}

	
}

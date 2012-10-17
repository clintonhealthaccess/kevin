package org.chai.kevin.data;

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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.chai.kevin.Period;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.ExpressionService.StatusValuePair;
import org.chai.kevin.value.SumPartialValue;
import org.chai.kevin.value.SumValue;
import org.chai.kevin.value.Value;

//@Entity(name="Summ")
class Sum extends Calculation<SumPartialValue> {
	
	static mapping = {
		table 'dhsst_data_calculation_sum'
	}
	
	@Override
	public SumValue getCalculationValue(List<SumPartialValue> partialValues, Period period, CalculationLocation location) {
		return new SumValue(partialValues, this, period, location);
	}

	@Override
	public Class<SumPartialValue> getValueClass() {
		return SumPartialValue.class;
	}

	@Override
	public SumPartialValue getCalculationPartialValue(String expression, Map<DataLocation, StatusValuePair> values, CalculationLocation location, Period period, DataLocationType type) {
		Value value = getValue(values, location);
		Integer numberOfDataLocations = getNumberOfDataLocations(values);
		return new SumPartialValue(this, location, period, type, numberOfDataLocations, value);
	}

	private Integer getNumberOfDataLocations(Map<DataLocation, StatusValuePair> values) {
		Integer result = 0;
		for (Entry<DataLocation, StatusValuePair> entry : values.entrySet()) {
			if (!entry.getValue().value.isNull()) result++;
		}
		return result;
	}
	
	@Override
	public List<String> getPartialExpressions() {
		List<String> result = new ArrayList<String>();
		result.add(getExpression());
		return result;
	}

	@Override
	public String toString() {
		return "Sum[getId()=" + getId() + ", getCode()="
				+ getCode() + ", getExpression()='" + getExpression() + "']";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}

}

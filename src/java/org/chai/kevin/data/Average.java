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
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.value.AveragePartialValue;
import org.chai.kevin.value.AverageValue;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ExpressionService.StatusValuePair;
import org.chai.kevin.value.Value;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hisp.dhis.period.Period;

@Entity(name="Average")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="dhsst_data_calculation_average")
public class Average extends Calculation<AveragePartialValue> {

	@Override
	public AverageValue getCalculationValue(Set<AveragePartialValue> partialValues, Period period, CalculationEntity entity) {
		return new AverageValue(partialValues, this, period, entity);
	}

	@Override
	@Transient
	public Class<AveragePartialValue> getValueClass() {
		return AveragePartialValue.class;
	}

	@Override
	public AveragePartialValue getCalculationPartialValue(String expression, Map<DataLocationEntity, StatusValuePair> values, CalculationEntity entity, Period period, DataEntityType type) {
		Value value = getValue(values.values());
		Integer numberOfFacilities = getNumberOfFacilities(values);
		return new AveragePartialValue(this, entity, period, type, numberOfFacilities, value);
	}

	private Integer getNumberOfFacilities(Map<DataLocationEntity, StatusValuePair> values) {
		Integer result = 0;
		for (Entry<DataLocationEntity, StatusValuePair> entry : values.entrySet()) {
			if (!entry.getValue().value.isNull()) result++;
		}
		return result;
	}

	@Override
	@Transient
	public List<String> getPartialExpressions() {
		List<String> result = new ArrayList<String>();
		result.add(getExpression());
		return result;
	}

	@Override
	public String toString() {
		return "Average [getExpression()=" + getExpression() + ", getId()="
				+ getId() + ", getCode()=" + getCode() + "]";
	}



}

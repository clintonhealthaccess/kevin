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
  
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

import org.chai.kevin.Period;
import org.chai.kevin.entity.export.Exportable;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.CalculationPartialValue;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ExpressionService.StatusValuePair;
import org.chai.kevin.value.Value;

@Entity(name="Calculation")
@Table(name="dhsst_data_calculation")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Calculation<T extends CalculationPartialValue> extends Data<T> implements Exportable {

	public static Type TYPE = Type.TYPE_NUMBER();
	
	private String expression;
	private Date refreshed;
	
	// extract partial expressions from the calculation
	@Transient
	public abstract List<String> getPartialExpressions();
	
	public abstract T getCalculationPartialValue(String expression, Map<DataLocation, StatusValuePair> values, 
			CalculationLocation location, Period period, DataLocationType type);
	
	public abstract CalculationValue<T> getCalculationValue(List<T> partialValues, Period period, CalculationLocation location);
	
	protected Value getValue(Map<DataLocation, StatusValuePair> statusValuePairs, CalculationLocation location) {
		Value result = null;
		if (location.collectsData()) {
			StatusValuePair statusValuePair = statusValuePairs.get(location);
			result = statusValuePair.value;
		}
		else {
			Double value = 0d;
			for (StatusValuePair statusValuePair : statusValuePairs.values()) {
				if (!statusValuePair.value.isNull()) value += statusValuePair.value.getNumberValue().doubleValue();
			}
			result = getType().getValue(value);
		}
		return result;
	}
	
	@Lob
	@Column(nullable=false)
	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Transient
	@Override
	public Type getType() {
		return Calculation.TYPE;
	}
	
	@Column(nullable=true, columnDefinition="datetime")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	public Date getRefreshed() {
		return refreshed;
	}
	
	public void setRefreshed(Date refreshed) {
		this.refreshed = refreshed;
	}
	
	@Override
	public abstract String toString();
}

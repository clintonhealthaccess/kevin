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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.Exportable;
import org.chai.kevin.Period;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.kevin.security.User;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.CalculationPartialValue;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ExpressionService.StatusValuePair;
import org.chai.kevin.value.Value;

abstract class Calculation<T extends CalculationPartialValue> extends Data<T> implements Exportable {

	Date refreshed;
	String expression;
	String typeString;
	String sourceMapString;
	
	static mapping = {
		table 'dhsst_data_calculation'
		tablePerHierarchy false
//		refreshed sqlType: "datetime"
		typeString sqlType: 'text'
	}
	
	static constraints =  {
		refreshed (nullable: true)
		expression(nullable: false, expressionValid: true)
		sourceMapString (nullable: true)
	}
	
	Type cachedType
	Map cachedSourceMap
	static transients = ['cachedType', 'type', 'cachedSourceMap', 'sourceMap']
	
	public abstract Type getType(CalculationLocation location);
	
	/*
	 * Retaining backward compatibility with old getters and setters
	 */
	public abstract Type getType();
	
	void setType(Type type) {
		this.cachedType = type
		this.typeString = type.jsonValue
	}
	
	void setTypeString(String typeString) {
		this.typeString = typeString
		this.cachedType = null
	}
	
	/*
	 * Retaining backward compatibility with old getters and setters
	 */
	Map getSourceMap() {
		if (sourceMapString != null && cachedSourceMap == null) cachedSourceMap = JSONUtils.getMapFromJSON(sourceMapString)
	   return cachedSourceMap
	}
   
	void setSourceMap(Map sourceMap) {
		sourceMapString = JSONUtils.getJSONFromMap(sourceMap)
		cachedSourceMap = sourceMap
	}
   
	void setSourceMapString(String sourceMapString) {
		this.cachedSourceMap = null
		this.sourceMapString = sourceMapString
	}
	
	// extract partial expressions from the calculation
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
	
	@Override
	public Set<String> getSources(Period period, DataLocationType type) {
		Set<String> result = new HashSet<String>();
		if (sourceMap.containsKey(period.getId()+"") && sourceMap.get(period.getId()+"").containsKey(type.getCode())) {
			result.addAll(sourceMap.get(period.getId()+"").get(type.getCode()));
		}
		return result;
	}
	
	@Override
	public Set<String> getSources() {
		Set<String> result = new HashSet<String>();
		for (Map<String, List<String>> map : sourceMap.values()) {
			for (List<String> sources : map.values()) {
				result.addAll(sources);
			}
		}
		return result;
	}
	
}

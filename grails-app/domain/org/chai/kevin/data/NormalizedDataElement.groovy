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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.Period;
import org.chai.location.DataLocationType;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.NormalizedDataElementValue;

class NormalizedDataElement extends DataElement<NormalizedDataElementValue> {

	// json text example : {"1":{"DH":"$1 + $2"}, "2":{"HC":"$1 + $2 + $3"}}
	Date refreshed
	String expressionMapString
	String sourceMapString
	
	static mapping = {
		table 'dhsst_data_normalized_element'
		refreshed sqlType: "datetime"
	}
	
	static constraints =  {
		refreshed (nullable: true)
		sourceMapString (nullable: true)
		expressionMap (nullable: false, expressionMapValid: true,
			circularDependency: true
		)
	}
	
	Map cachedSourceMap
	Map cachedExpressionMap
	
	static transients = ['cachedExpressionMap', 'cachedSourceMap', 'expressionMap', 'sourceMap']
	
	/*
	 * Retaining backward compatibility with old getters and setters
	 */
	Map getExpressionMap() {
		if (expressionMapString != null && cachedExpressionMap == null) this.cachedExpressionMap = JSONUtils.getMapFromJSON(expressionMapString)
		return this.cachedExpressionMap
	}
	
	void setExpressionMap(Map expressionMap) {
		this.expressionMapString = JSONUtils.getJSONFromMap(expressionMap)
		this.cachedExpressionMap = expressionMap
	}
	
	void setExpressionMapString(String expressionMapString) {
		this.cachedExpressionMap = null
		this.expressionMapString = expressionMapString
	}
	
	/*
	 * Retaining backward compatibility with old getters and setters
	 */
	Map getSourceMap() {
		if (sourceMapString != null && cachedSourceMap == null) this.cachedSourceMap = JSONUtils.getMapFromJSON(sourceMapString)
		return this.cachedSourceMap
	}
	
	void setSourceMap(Map sourceMap) {
		this.sourceMapString = JSONUtils.getJSONFromMap(sourceMap)
		this.cachedSourceMap = sourceMap
	}
	
	void setSourceMapString(String sourceMapString) {
		this.cachedSourceMap = null
		this.sourceMapString = sourceMapString
	}
	
	public String getExpression(Period period, String typeCode) {
		if (!expressionMap.containsKey(Long.toString(period.getId()))) return null;
		return expressionMap.get(Long.toString(period.getId())).get(typeCode);
	}
	
	public Set<String> getExpressions() {
		Set<String> expressions = new HashSet<String>();
		for (Map<String, String> groupMap : expressionMap.values()) {
			for (String expression : groupMap.values()) {
				expressions.add(expression);
			}
		}
		return expressions;
	}

	@Override
	public Class<NormalizedDataElementValue> getValueClass() {
		return NormalizedDataElementValue.class;
	}
	
	@Override
	public String toString() {
		return "NormalizedDataElement[getId()=" + getId() + ", getCode()=" + getCode() + "]";
	}
	
	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
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

package org.chai.kevin.value

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

import groovy.transform.EqualsAndHashCode;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.Transient;

import org.chai.kevin.data.Type;

import org.chai.kevin.Period;
import org.chai.kevin.data.Data;
import org.chai.location.CalculationLocation;
import org.hibernate.annotations.NaturalId;

//@EqualsAndHashCode(includes=['location', 'period', 'data'])
abstract class StoredValue implements DataValue {

	Date timestamp = new Date();

	CalculationLocation location
	Period period
	String valueString
	
	static mapping = {
		table 'dhsst_value'
		
		period index: 'Value_Index'
		location index: 'Value_Index'
		
		valueString sqlType: 'text'
//		timestamp sqlType: "datetime"
	}
	
	static constraints = {
		location (nullable: false)
		period (nullable: false)
		valueString (nullable: false)
		value (nullable: false)
	}
	
	public StoredValue() {}
	
	public StoredValue(CalculationLocation location, Period period, Value value) {
		this.location = location;
		this.period = period;
		if (value != null) this.value = value;
	}

	Value cachedValue
	static transients = ['cachedValue', 'value']
	
	/*
	 * Retaining backward compatibility with old getters and setters
	 */
	Value getValue() {
		if (valueString != null && cachedValue == null) this.cachedValue = new Value(valueString)
		return cachedValue
	}
	
	void setValue(Value value) {
		this.cachedValue = value
		this.valueString = value.jsonValue
	}
	
	void setValueString(String valueString) {
		this.valueString = valueString
		this.cachedValue = null
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this.is(obj))
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof StoredValue))
			return false;
		StoredValue other = (StoredValue) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
	
	
	
}
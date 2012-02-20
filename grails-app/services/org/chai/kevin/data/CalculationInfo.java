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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;

public class CalculationInfo extends Info<CalculationValue<?>> {

	private Calculation<?> calculation;
	private List<DataLocationEntity> locations;
	private List<DataElement<?>> dataElements;
	private Map<DataLocationEntity, Map<DataElement<?>, DataValue>> values;
	private Map<CalculationEntity, CalculationValue<?>> calculationValues;
	
	public CalculationInfo(Calculation<?> calculation, CalculationValue<?> calculationValue, List<DataLocationEntity> locations, List<DataElement<?>> dataElements, 
			Map<DataLocationEntity, Map<DataElement<?>, DataValue>> values, Map<CalculationEntity, CalculationValue<?>> calculationValues) {
		super(calculationValue);

		this.calculation = calculation;
		this.locations = locations;
		this.dataElements = dataElements;
		this.values = values;
		this.calculationValues = calculationValues;
	}

	public Calculation<?> getCalculation() {
		return calculation;
	}
	
	public List<DataLocationEntity> getLocations() {
		return locations;
	}
	
	public List<DataElement<?>> getDataElements() {
		return dataElements;
	}
	
	public Set<DataLocationEntity> getLocationsOfGroup(LocationEntity location) {
		Set<DataLocationEntity> result = new HashSet<DataLocationEntity>();
		for (DataLocationEntity child : values.keySet()) {
			if (location.getDataEntities().contains(child)) result.add(child);
		}
		return result;
	}
	
	public CalculationValue<?> getValue(CalculationEntity location) {
		return calculationValues.get(location);
	}
	
	public DataValue getValue(CalculationEntity location, DataElement<?> dataElement) {
		return values.get(location).get(dataElement);
	}
	
	public String getTemplate() {
		return "/info/calculationInfo";
	}
	
}

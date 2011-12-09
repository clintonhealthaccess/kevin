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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.chai.kevin.Organisation;
import org.chai.kevin.LocationService;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionService;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.period.Period;

public class InfoService {

	private ValueService valueService;
	private ExpressionService expressionService;
	private LocationService locationService;
	
	public NormalizedDataElementInfo getNormalizedDataElementInfo(NormalizedDataElement normalizedDataElement, Organisation facility, Period period) {
		if (locationService.loadLevel(facility) != locationService.getFacilityLevel()) {
			throw new IllegalArgumentException("getting the info of a NormalizedDateElement for non-facility organisation is not possible");
		}
		NormalizedDataElementInfo info = null;
		NormalizedDataElementValue expressionValue = valueService.getDataElementValue(normalizedDataElement, facility.getOrganisationUnit(), period);
		if (expressionValue != null) {
			locationService.loadGroup(facility);
			Map<RawDataElement, RawDataElementValue> dataValues = new HashMap<RawDataElement, RawDataElementValue>();
			List<RawDataElement> dataElements = new ArrayList<RawDataElement>();
			for (Entry<String, RawDataElement> entry : expressionService.getDataInExpression(normalizedDataElement.getExpression(period, facility.getOrganisationUnitGroup().getUuid()), RawDataElement.class).entrySet()) {
				if (entry.getValue() != null) {
					dataValues.put(entry.getValue(), valueService.getDataElementValue(entry.getValue(), facility.getOrganisationUnit(), period));
					dataElements.add(entry.getValue());
				}
			}
			info = new NormalizedDataElementInfo(expressionValue, dataElements, dataValues);
		}
		return info;
	}
	
	public CalculationInfo getCalculationInfo(Calculation<?> calculation, Organisation organisation, Period period, Set<String> groupUuids) {
		CalculationInfo info = null;
		CalculationValue<?> calculationValue = valueService.getCalculationValue(calculation, organisation.getOrganisationUnit(), period, groupUuids);
		if (calculationValue != null) {
			List<Organisation> facilities = locationService.getChildrenOfLevel(organisation, locationService.getFacilityLevel());
			Map<String, DataElement> dataMap = expressionService.getDataInExpression(calculation.getExpression(), DataElement.class);
			List<DataElement<?>> dataElements = new ArrayList<DataElement<?>>();
			for (DataElement<?> value : dataMap.values()) {
				dataElements.add(value);
			}
			
			Map<Organisation, CalculationValue<?>> calculationValues = new HashMap<Organisation, CalculationValue<?>>();
			Map<Organisation, Map<DataElement<?>, DataValue>> values = new HashMap<Organisation, Map<DataElement<?>,DataValue>>();
			for (Organisation facility : facilities) {
				locationService.loadGroup(facility);
				if (groupUuids.contains(facility.getOrganisationUnitGroup().getUuid())) {
					calculationValues.put(facility, valueService.getCalculationValue(calculation, facility.getOrganisationUnit(), period, groupUuids));
					Map<DataElement<?>, DataValue> data = new HashMap<DataElement<?>, DataValue>();
					for (DataElement<?> dataElement : dataElements) {
						data.put(dataElement, valueService.getDataElementValue(dataElement, facility.getOrganisationUnit(), period));
					}
					values.put(facility, data);
				}
			}
			info = new CalculationInfo(calculation, calculationValue, facilities, dataElements, values, calculationValues);
		}
		
		return info;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}
	
	public void setOrganisationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
}

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

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.SumValue;
import org.chai.kevin.data.Average;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.AggregationValue;
import org.chai.kevin.value.AveragePartialValue;
import org.chai.kevin.value.AverageValue;
import org.chai.kevin.value.CalculationPartialValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.Status;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class ValueServiceSpec extends IntegrationTests {

	def valueService;
	
	def "test get raw data element value"() {
		setup:
		def period = newPeriod()
		def organisationUnit = newOrganisationUnit(BUTARO)
		
		when: "empty value list"
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		
		then:
		valueService.getDataElementValue(rawDataElement, organisationUnit, period) == null
		
		when:
		def dataValue = newRawDataElementValue(rawDataElement, period, organisationUnit, v("1"))
		
		then:
		valueService.getDataElementValue(rawDataElement, organisationUnit, period).equals(dataValue)
	}
	
	def "test get raw data element value at non-facility level"() {
		setup:
		def period = newPeriod()
		setupOrganisationUnitTree()
		
		when:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		
		then:
		valueService.getDataElementValue(rawDataElement, OrganisationUnit.findByName(RWANDA), period) == null
	}
	
	def "test get normalized data element value"() {
		setup:
		def period = newPeriod()
		setupOrganisationUnitTree()
		
		when:
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		
		then: "empty value list"
		valueService.getDataElementValue(normalizedDataElement, OrganisationUnit.findByName(BUTARO), period) == null
		
		when:
		def dataValue = newNormalizedDataElementValue(normalizedDataElement, OrganisationUnit.findByName(BUTARO), period, Status.VALID, v("1"))
		
		then:
		valueService.getDataElementValue(normalizedDataElement, OrganisationUnit.findByName(BUTARO), period).equals(dataValue)
	}
	
	def "test get average value"() {
		setup:
		def period = newPeriod()
		setupOrganisationUnitTree()
		
		when:
		def average = newAverage("1", CODE(1))
		def expectedValue = new AverageValue([], average, period, OrganisationUnit.findByName(BUTARO))
		def value = valueService.getCalculationValue(average, OrganisationUnit.findByName(BUTARO), period, s([DISTRICT_HOSPITAL_GROUP]))
		 
		then:
		value.equals(expectedValue)
		
		when:
		def partialValue = newAveragePartialValue(average, period, OrganisationUnit.findByName(BUTARO), DISTRICT_HOSPITAL_GROUP, 1, v("1"))
		expectedValue = new AverageValue([partialValue], average, period, OrganisationUnit.findByName(BUTARO))
		value = valueService.getCalculationValue(average, OrganisationUnit.findByName(BUTARO), period, s([DISTRICT_HOSPITAL_GROUP]))

		then:
		value.equals(expectedValue)
	}
	
	def "test get sum value"() {
		setup:
		def period = newPeriod()
		setupOrganisationUnitTree()
		
		when:
		def sum = newSum("1", CODE(1))
		def expectedValue = new SumValue([], sum, period, OrganisationUnit.findByName(BUTARO))
		def value = valueService.getCalculationValue(sum, OrganisationUnit.findByName(BUTARO), period, s([DISTRICT_HOSPITAL_GROUP]))
		 
		then:
		value.equals(expectedValue)
		
		when:
		def partialValue = newSumPartialValue(sum, period, OrganisationUnit.findByName(BUTARO), DISTRICT_HOSPITAL_GROUP, v("1"))
		expectedValue = new SumValue([partialValue], sum, period, OrganisationUnit.findByName(BUTARO))
		value = valueService.getCalculationValue(sum, OrganisationUnit.findByName(BUTARO), period, s([DISTRICT_HOSPITAL_GROUP]))

		then:
		value.equals(expectedValue)
	}
	
	def "test get aggregation value"() {
		setup:
		def period = newPeriod()
		setupOrganisationUnitTree()
		
		when:
		def aggregation = newAggregation("1", CODE(1))
		def expectedValue = new AggregationValue([], aggregation, period, OrganisationUnit.findByName(BUTARO))
		def value = valueService.getCalculationValue(aggregation, OrganisationUnit.findByName(BUTARO), period, s([DISTRICT_HOSPITAL_GROUP]))
		 
		then:
		value.equals(expectedValue)
		
		when:
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER());
		aggregation = newAggregation("\$"+dataElement.id, CODE(2))
		def partialValue = newAggregationPartialValue(aggregation, period, OrganisationUnit.findByName(BUTARO), DISTRICT_HOSPITAL_GROUP, "\$"+dataElement.id, v("1"))
		expectedValue = new AggregationValue([partialValue], aggregation, period, OrganisationUnit.findByName(BUTARO))
		value = valueService.getCalculationValue(aggregation, OrganisationUnit.findByName(BUTARO), period, s([DISTRICT_HOSPITAL_GROUP]))

		then:
		value.equals(expectedValue)
	}
	
	def "test number of values"() {
		setup:
		def period = newPeriod()
		def organisationUnit = newOrganisationUnit(BUTARO)
		
		when: 
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		newRawDataElementValue(rawDataElement, period, organisationUnit, v("40"))
		
		then:
		valueService.getNumberOfValues(rawDataElement, period) == 1
		
		when:
		def newPeriod = newPeriod()
		
		then:
		valueService.getNumberOfValues(rawDataElement, newPeriod) == 0
				
		when:
		def rawDataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		
		then:
		valueService.getNumberOfValues(rawDataElement2, period) == 0
	}
	
	def "test number of values does not count other value types"() {
		setup:
		def period = newPeriod()
		def organisationUnit = newOrganisationUnit(BUTARO)
		
		when:
		def rawDataElement1 = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def rawDataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		newRawDataElementValue(rawDataElement1, period, organisationUnit, v("40"))
		
		then:
		valueService.getNumberOfValues(rawDataElement1, period) == 1
	}
	
	def "test value list"() {
		
		setup:
		def period = newPeriod()
		def organisationUnit = newOrganisationUnit(BUTARO)
		
		when:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def rawDataElementValue = newRawDataElementValue(rawDataElement, period, organisationUnit, v("40"))
		
		then:
		valueService.getValues(rawDataElement, period).equals([rawDataElementValue])
		
	}

	def "test delete data element values"() {
		setup:
		def period = newPeriod()
		def organisation = newOrganisationUnit(BUTARO)
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		
		when:
		def rawDataElementValue = newRawDataElementValue(rawDataElement, period, OrganisationUnit.findByName(BUTARO), v("40"))
		
		then:
		RawDataElementValue.count() == 1
		
		when:
		valueService.deleteValues(rawDataElement)
		
		then:
		RawDataElementValue.count() == 0
		
		when: "only deletes right values"
		def rawDataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		newRawDataElementValue(rawDataElement, period, OrganisationUnit.findByName(BUTARO), v("40"))
		newRawDataElementValue(rawDataElement2, period, OrganisationUnit.findByName(BUTARO), v("40"))
		valueService.deleteValues(rawDataElement)
		
		then:
		RawDataElementValue.count() == 1
	}
	
	def "save raw data element value sets date"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+""):[DISTRICT_HOSPITAL_GROUP:"\$"+rawDataElement.id]]))
		
		when:
		def date = normalizedDataElement.timestamp
		def rawDataElementValue = newRawDataElementValue(rawDataElement, period, OrganisationUnit.findByName(BUTARO), v("40"))
		valueService.save(rawDataElementValue);
		
		then:
		NormalizedDataElement.list()[0].timestamp.after(date)
	}
		
	def "test delete calculation values"() {
		setup:
		def period = newPeriod()
		def organisation = newOrganisationUnit(BUTARO)
		def average = newAverage("1", CODE(1))
		
		when:
		newAveragePartialValue(average, period, OrganisationUnit.findByName(BUTARO), DISTRICT_HOSPITAL_GROUP, 1, v("1"))
		
		then:
		AveragePartialValue.count() == 1
		
		when:
		valueService.deleteValues(average)
		
		then:
		AveragePartialValue.count() == 0
		
		when:
		def average2 = newAverage("2", CODE(2))
		newAveragePartialValue(average, period, OrganisationUnit.findByName(BUTARO), DISTRICT_HOSPITAL_GROUP, 1, v("1"))
		newAveragePartialValue(average2, period, OrganisationUnit.findByName(BUTARO), DISTRICT_HOSPITAL_GROUP, 1, v("1"))
		valueService.deleteValues(average)
		
		then:
		AveragePartialValue.count() == 1
	}
	
}

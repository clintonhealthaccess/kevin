package org.chai.kevin.data

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
import org.chai.kevin.data.Average;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Sum;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.value.CalculationPartialValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.Status;
import org.chai.kevin.value.SumPartialValue;
import org.chai.kevin.value.Value;

import grails.plugin.spock.UnitSpec;

class DataServiceSpec extends IntegrationTests {

	def dataService;
	
	def "get data element"() {
		setup:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([:]))
		def average = newAverage("1", CODE(3))
		def sum = newSum("1", CODE(4))
		def aggregation = newAggregation("1", CODE(5))
		def result = null
		
		when:
		result = dataService.getData(rawDataElement.id, RawDataElement.class)
		
		then:
		result.equals(rawDataElement)
		
		when:
		result = dataService.getData(normalizedDataElement.id, NormalizedDataElement.class)
		
		then:
		result.equals(normalizedDataElement)

		when:
		result = dataService.getData(average.id, Average.class)
		
		then:
		result.equals(average)

		when:
		result = dataService.getData(sum.id, Sum.class)
		
		then:
		result.equals(sum)
	
		expect:
		dataService.getData(average.id, Sum.class) == null
		dataService.getData(average.id, NormalizedDataElement.class) == null
		dataService.getData(average.id, RawDataElement.class) == null
		dataService.getData(sum.id, Average.class) == null
		dataService.getData(sum.id, NormalizedDataElement.class) == null
		dataService.getData(sum.id, RawDataElement.class) == null
		dataService.getData(rawDataElement.id, Average.class) == null
		dataService.getData(rawDataElement.id, NormalizedDataElement.class) == null
		dataService.getData(rawDataElement.id, Sum.class) == null
		
	}
	
	def "get data element using super type"() {
		setup:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def average = newAverage("1", CODE(3)) 
		
		expect:
		dataService.getData(rawDataElement.id, DataElement.class).equals(rawDataElement)
		dataService.getData(rawDataElement.id, Data.class).equals(rawDataElement)
		dataService.getData(average.id, Calculation.class).equals(average)
		dataService.getData(average.id, Data.class).equals(average)
	}
	
	def "list data element"() {
		setup:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([:]))
		def average = newAverage("1", CODE(3))
		def sum = newSum("1", CODE(4))
		def aggregation = newAggregation("1", CODE(5))
		
		expect:
		dataService.list(Average.class, [:]).equals([average])
		dataService.count(Average.class) == 1
		dataService.list(DataElement.class, [:]).equals([rawDataElement, normalizedDataElement])
		dataService.count(DataElement.class) == 2
		dataService.list(NormalizedDataElement.class, [:]).equals([normalizedDataElement])
		dataService.count(NormalizedDataElement.class) == 1
		dataService.list(Data.class, [:]).equals([rawDataElement, normalizedDataElement, average, sum, aggregation])
		dataService.count(Data.class) == 5
	}
	
	def "list data element with params"() {
		setup:
		def rawDataElement1 = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def rawDataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def rawDataElement3 = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def rawDataElement4 = newRawDataElement(CODE(4), Type.TYPE_NUMBER())
		
		expect:
		dataService.list(DataElement.class, [max:1]).equals([rawDataElement1])
		dataService.list(DataElement.class, [max:1, offset:1]).equals([rawDataElement2])
		dataService.list(DataElement.class, [max:2]).equals([rawDataElement1, rawDataElement2])
		dataService.list(DataElement.class, [max:2, offset:2]).equals([rawDataElement3, rawDataElement4])
		
	}
	
	def "search for null"() {
		expect:
		dataService.searchData(RawDataElement.class, null, [], [:]).equals([])
	}
	
	def "search for data element works"() {
		setup:
		def dataElement1 = newRawDataElement(j(["en": "element"]), CODE(1), Type.TYPE_NUMBER())
		def dataElement2 = newRawDataElement(j(["en": "something"]), CODE(2), Type.TYPE_NUMBER())
		def dataElement3 = newRawDataElement(j(["en": ""]), CODE(3), Type.TYPE_NUMBER(), "info")
		
		expect:
		dataService.searchData(RawDataElement.class, "ele", [], [:]).equals([dataElement1])
		dataService.countData(RawDataElement.class, "ele", []) == 1
		dataService.searchData(RawDataElement.class, "some", [], [:]).equals([dataElement2])
		dataService.countData(RawDataElement.class, "some", []) == 1
		dataService.searchData(RawDataElement.class, "ele some", [], [:]).equals([])
		dataService.countData(RawDataElement.class, "ele some", []) == 0
		dataService.searchData(RawDataElement.class, "info", [], [:]).equals([dataElement3])
		dataService.countData(RawDataElement.class, "info", []) == 1
				
	}
	
	def "search for normalized data element works"() {
		setup:
		def dataElement1 = newNormalizedDataElement(j(["en": "expression"]), CODE(1), Type.TYPE_NUMBER(), e([:]))
		def dataElement2 = newNormalizedDataElement(j(["en": "something"]), CODE(2), Type.TYPE_NUMBER(), e([:]))
		
		expect:
		dataService.searchData(NormalizedDataElement.class, "expr", [], [:]).equals([dataElement1])
		dataService.countData(NormalizedDataElement.class, "expr", []) == 1
		dataService.searchData(NormalizedDataElement.class, "some", [], [:]).equals([dataElement2])
		dataService.countData(NormalizedDataElement.class, "some", []) == 1
		dataService.searchData(NormalizedDataElement.class, "expr some", [], [:]).equals([])
		dataService.countData(NormalizedDataElement.class, "expr some", []) == 0
		
	}
	
	def "search for calculations work"() {
		setup:
		def average1 = newAverage(j(["en": "average"]), "1", CODE(1));
		
		expect:
		dataService.searchData(Average.class, "aver", [], [:]).equals([average1])
		dataService.searchData(Data.class, "aver", [], [:]).equals([average1])
		dataService.searchData(Sum.class, "aver", [], [:]).isEmpty()
		
	}
	
	def "delete data element with associated values throws exception"() {
		when:
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def period = newPeriod()
		def type = newDataEntityType(HEALTH_CENTER_GROUP)
		def location = newDataLocationEntity(KIVUYE, type)
		newRawDataElementValue(dataElement, period, location, Value.NULL_INSTANCE())
		
		dataService.delete(dataElement)
		
		then:
		thrown IllegalArgumentException
		RawDataElement.count() == 1
		RawDataElementValue.count() == 1
	}
	
	def "delete normalized data elements with associated values throws exception"() {
		when:
		def dataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		def period = newPeriod()
		def type = newDataEntityType(HEALTH_CENTER_GROUP)
		def location = newDataLocationEntity(KIVUYE, type)
		newNormalizedDataElementValue(dataElement, location, period, Status.VALID, Value.NULL_INSTANCE())
		
		dataService.delete(dataElement)
		
		then:
		thrown IllegalArgumentException
		NormalizedDataElement.count() == 1
		NormalizedDataElementValue.count() == 1
	}
	
	def "delete calculation with associated values throws exception"() {
		when:
		def calculation = newSum("1", CODE(1))
		def period = newPeriod()
		def type = newDataEntityType(HEALTH_CENTER_GROUP)
		def location = newDataLocationEntity(KIVUYE, type)
		newSumPartialValue(calculation, period, location, DataEntityType.findByCode(HEALTH_CENTER_GROUP), Value.NULL_INSTANCE())

		dataService.delete(calculation)
		
		then:
		thrown IllegalArgumentException
		Sum.count() == 1
		SumPartialValue.count() == 1
		
	}
	
	def "delete normalized data element with associated expression throws exception"() {
		when:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [(1):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]])
		
		dataService.delete(rawDataElement)
		
		then:
		thrown IllegalArgumentException
		NormalizedDataElement.count() == 1
	}
	
	def "delete calculation with associated expression throws exception"() {
		when:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def sum = newSum("\$"+rawDataElement.id, CODE(2))
		
		dataService.delete(rawDataElement)
		
		then:
		thrown IllegalArgumentException
		Sum.count() == 1
	}
	
	def "get referencing normalized data element"() {
		when:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [(1):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]])
		
		then:
		dataService.getReferencingNormalizedDataElements(rawDataElement).equals([normalizedDataElement])
		
		when:
		newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), [(1):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id+"0"]], [validate: false])
		
		then:
		dataService.getReferencingNormalizedDataElements(rawDataElement).equals([normalizedDataElement])
		
		when:
		newSum("\$"+rawDataElement.id, CODE(4))
		
		then:
		dataService.getReferencingNormalizedDataElements(rawDataElement).equals([normalizedDataElement])
	}
	
	def "get referencing calculations"() {
		when:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def sum = newSum("\$"+rawDataElement.id, CODE(2))
		
		then:
		dataService.getReferencingCalculations(rawDataElement).equals([sum])
		
		when:
		newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), [(1):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]])
		
		then:
		dataService.getReferencingCalculations(rawDataElement).equals([sum])
	}

	
}

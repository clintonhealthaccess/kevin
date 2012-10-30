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
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Summ;
import org.chai.kevin.data.Type;
import org.chai.kevin.dsr.DsrIntegrationTests;
import org.chai.kevin.dsr.DsrTarget;
import org.chai.location.DataLocationType;
import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.kevin.value.CalculationPartialValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.Status;
import org.chai.kevin.value.SumPartialValue;
import org.chai.kevin.value.Value;

import grails.plugin.spock.UnitSpec;

class DataServiceSpec extends IntegrationTests {

	def dataService;
	
	def "get data returns null when id is null"() {
		expect:
		dataService.getData(null, DataElement.class) == null
	}
	
	def "get data element by id"() {
		setup:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [:])
		def ratio = newSum("1", CODE(3))
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
		result = dataService.getData(ratio.id, Summ.class)
		
		then:
		result.equals(ratio)

		when:
		result = dataService.getData(sum.id, Summ.class)
		
		then:
		result.equals(sum)
	
		expect:		
		dataService.getData(ratio.id, NormalizedDataElement.class) == null
		dataService.getData(ratio.id, RawDataElement.class) == null
		dataService.getData(sum.id, NormalizedDataElement.class) == null
		dataService.getData(sum.id, RawDataElement.class) == null
		dataService.getData(rawDataElement.id, Summ.class) == null				
		dataService.getData(normalizedDataElement.id, Summ.class) == null
		dataService.getData(normalizedDataElement.id, RawDataElement.class) == null
		
	}

	def "get data from DSR"() {
		setup:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def program = newReportProgram(CODE(1))
		def category = DsrIntegrationTests.newDsrTargetCategory(CODE(2), 1)
		def dsrTarget = DsrIntegrationTests.newDsrTarget(CODE(3), rawDataElement, program, category)
		
		when:
		dsrTarget = DsrTarget.findByCode(CODE(3))
		
		then:
		dataService.getData(dsrTarget.getData().id, Calculation.class) == null 
	}
		
	def "get data element by code"() {
		setup:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [:])
		def ratio = newSum("1", CODE(3))
		def sum = newSum("1", CODE(4))
		def aggregation = newAggregation("1", CODE(5))
		def result = null
		
		when:
		result = dataService.getDataByCode(rawDataElement.code, RawDataElement.class)
		
		then:
		result.equals(rawDataElement)
		
		when:
		result = dataService.getDataByCode(normalizedDataElement.code, NormalizedDataElement.class)
		
		then:
		result.equals(normalizedDataElement)

		when:
		result = dataService.getDataByCode(ratio.code, Summ.class)
		
		then:
		result.equals(ratio)

		when:
		result = dataService.getDataByCode(sum.code, Summ.class)
		
		then:
		result.equals(sum)
	
		expect:
		dataService.getDataByCode(ratio.code, NormalizedDataElement.class) == null
		dataService.getDataByCode(ratio.code, RawDataElement.class) == null
		dataService.getDataByCode(sum.code, NormalizedDataElement.class) == null
		dataService.getDataByCode(sum.code, RawDataElement.class) == null
		dataService.getDataByCode(rawDataElement.code, Summ.class) == null
		dataService.getDataByCode(rawDataElement.code, NormalizedDataElement.class) == null
		dataService.getDataByCode(normalizedDataElement.code, Summ.class) == null
		dataService.getDataByCode(normalizedDataElement.code, RawDataElement.class) == null
		
	}
	
	def "get data element using super type"() {
		setup:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def ratio = newSum("1", CODE(3)) 
		
		expect:
		dataService.getData(rawDataElement.id, DataElement.class).equals(rawDataElement)
		dataService.getData(rawDataElement.id, Data.class).equals(rawDataElement)
		dataService.getData(ratio.id, Calculation.class).equals(ratio)
		dataService.getData(ratio.id, Data.class).equals(ratio)
	}
	
	def "list data element"() {
		setup:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [:])
		def ratio = newSum("1", CODE(3))
		def sum = newSum("1", CODE(4))
		def aggregation = newAggregation("1", CODE(5))
		
		expect:
		dataService.list(Summ.class, [:]).equals([ratio, sum])
		dataService.count(Summ.class) == 2
		dataService.list(DataElement.class, [:]).equals([rawDataElement, normalizedDataElement])
		dataService.count(DataElement.class) == 2
		dataService.list(NormalizedDataElement.class, [:]).equals([normalizedDataElement])
		dataService.count(NormalizedDataElement.class) == 1
		dataService.list(Data.class, [:]).equals([rawDataElement, normalizedDataElement, ratio, sum, aggregation])
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
		def dataElement1 = newRawDataElement(["en": "element"], CODE(1), Type.TYPE_NUMBER())
		def dataElement2 = newRawDataElement(["en": "something"], CODE(2), Type.TYPE_NUMBER())
		def dataElement3 = newRawDataElement(["en": ""], CODE(3), Type.TYPE_NUMBER(), "info")
		
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
		def dataElement1 = newNormalizedDataElement(["en": "expression"], CODE(1), Type.TYPE_NUMBER(), [:])
		def dataElement2 = newNormalizedDataElement(["en": "something"], CODE(2), Type.TYPE_NUMBER(), [:])
		
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
		def ratio1 = newSum(["en": "sum"], "1", CODE(1));
		
		expect:
		dataService.searchData(Summ.class, "su", [], [:]).equals([ratio1])
		dataService.searchData(Data.class, "su", [], [:]).equals([ratio1])
		
	}
	
	def "delete data element with associated values throws exception"() {		
		when:
		setupLocationTree()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def period = newPeriod()
		newRawDataElementValue(dataElement, period, DataLocation.findByCode(KIVUYE), Value.NULL_INSTANCE())
		
		dataService.delete(dataElement)
		
		then:
		thrown IllegalArgumentException
		RawDataElement.count() == 1
		RawDataElementValue.count() == 1
	}
	
	def "delete normalized data elements with associated values throws exception"() {
		when:
		setupLocationTree()
		def dataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [:])
		def period = newPeriod()
		newNormalizedDataElementValue(dataElement, DataLocation.findByCode(KIVUYE), period, Status.VALID, Value.NULL_INSTANCE())
		
		dataService.delete(dataElement)
		
		then:
		thrown IllegalArgumentException
		NormalizedDataElement.count() == 1
		NormalizedDataElementValue.count() == 1
	}
	
	def "delete calculation with associated values throws exception"() {
		when:
		setupLocationTree()
		def calculation = newSum("1", CODE(1))
		def period = newPeriod()
		newSumPartialValue(calculation, period, DataLocation.findByCode(KIVUYE), DataLocationType.findByCode(HEALTH_CENTER_GROUP), Value.NULL_INSTANCE())

		dataService.delete(calculation)
		
		then:
		thrown IllegalArgumentException
		Summ.count() == 1
		SumPartialValue.count() == 1
		
	}
	
	def "delete normalized data element with associated expression throws exception"() {
		when:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [('1'):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]])
		
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
		Summ.count() == 1
	}
	
	def "get referencing normalized data element"() {
		when:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [('1'):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]])
		
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
		newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), [('1'):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]])
		
		then:
		dataService.getReferencingCalculations(rawDataElement).equals([sum])
	}
	
}

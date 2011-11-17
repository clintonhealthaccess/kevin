package org.chai.kevin

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

import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Sum;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.CalculationPartialValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.NormalizedDataElementValue;
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
		dataService.getData(element.id, clazz) == null
		
		where:
		element			| clazz
		average			| Sum.class
		average			| NormalizedDataElement.class
		average			| RawDataElement.class
		sum				| Average.class
		sum				| NormalizedDataElement.class
		sum				| RawDataElement.class
		rawDataElement	| Sum.class
		rawDataElement	| Average.class
		rawDataElement	| NormalizedDataElement.class
	}
	
	def "get data element using super type"() {
		setup:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def average = newAverage("1", CODE(3)) 
		
		expect:
		dataService.getData(rowDataElement.id, DataElement.class).equals(rawDataElement)
		dataService.getData(rowDataElement.id, Data.class).equals(rawDataElement)
		dataService.getData(average.id, Calculation.class).equals(average)
		dataService.getData(average.id, Data.class).equals(average)
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
	
	def "search for expression works"() {
		setup:
		def expression1 = newExpression(j(["en": "expression"]), CODE(1), Type.TYPE_NUMBER(), "1")
		def expression2 = newExpression(j(["en": "something"]), CODE(2), Type.TYPE_NUMBER(), "1")
		
		expect:
		dataService.searchData(Expression.class, "expr", [], [:]).equals([expression1])
		dataService.countData(Expression.class, "expr", []) == 1
		dataService.searchData(Expression.class, "some", [], [:]).equals([expression2])
		dataService.countData(Expression.class, "some", []) == 1
		dataService.searchData(Expression.class, "expr some", [], [:]).equals([])
		dataService.countData(Expression.class, "expr some", []) == 0
		
	}
	
	def "delete data element with associated values throws exception"() {
		when:
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def period = newPeriod()
		def organisation = newOrganisationUnit(KIVUYE)
		newRawDataElementValue(dataElement, period, organisation, Value.NULL)
		
		dataService.delete(dataElement)
		
		then:
		thrown IllegalArgumentException
		RawDataElement.count() == 1
		RawDataElementValue.count() == 1
	}
	
	def "delete expressions with associated values throws exception"() {
		when:
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "1")
		def period = newPeriod()
		def organisation = newOrganisationUnit(KIVUYE)
		newExpressionValue(expression, period, organisation, Status.VALID, Value.NULL)
		
		dataService.delete(expression)
		
		then:
		thrown IllegalArgumentException
		Expression.count() == 1
		NormalizedDataElementValue.count() == 1
	}
	
	def "delete calculation with associated values throws exception"() {
		when:
		def calculation = newSum([:], CODE(1), Type.TYPE_NUMBER())
		def period = newPeriod()
		def organisation = newOrganisationUnit(KIVUYE)
		newCalculationValue(calculation, period, organisation, false, false, Value.NULL)
		
		dataService.delete(calculation)
		
		then:
		thrown IllegalArgumentException
		Sum.count() == 1
		CalculationPartialValue.count() == 1
		
	}
	
	def "get calculations for expression"() {
		when:
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "1")
		def calculation = newSum([(DISTRICT_HOSPITAL_GROUP):expression], CODE(2), Type.TYPE_NUMBER())
		
		then:
		dataService.getCalculations(expression).equals([calculation])
	}
	
}

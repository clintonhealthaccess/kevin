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
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Expression;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ExpressionValue;
import org.chai.kevin.value.ExpressionValue.Status;
import org.chai.kevin.value.Value;

import grails.plugin.spock.UnitSpec;

class DataServiceSpec extends IntegrationTests {

	def dataService;
	
//	def "search for constant works"() {
//		expect:
//		def constants = dataService.searchConstants("con")
//		constants == [Constant.findByCode("CONST1")]
//	}
	
	def "search for data element works"() {
		setup:
		def dataElement = newDataElement(j(["en": "element"]), CODE(1), Type.TYPE_NUMBER)
		
		expect:
		def dataElements = dataService.searchDataElements("ele", null)
		dataElements == [dataElement]
	}
	
	def "delete data element with associated values throws exception"() {
		when:
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER)
		def period = newPeriod()
		def organisation = newOrganisationUnit(KIVUYE)
		newDataValue(dataElement, period, organisation, Value.NULL)
		
		dataService.delete(dataElement)
		
		then:
		thrown IllegalArgumentException
		DataElement.count() == 1
		
	}
	
	def "delete expressions with associated values deletes values"() {
		when:
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER, expression: "1")
		def period = newPeriod()
		def organisation = newOrganisationUnit(KIVUYE)
		newExpressionValue(expression, period, organisation, Status.VALID, Value.NULL)
		
		dataService.delete(expression)
		
		then:
		Expression.count() == 0
		ExpressionValue.count() == 0
	}
	
	def "delete calculation with associated values deletes values"() {
		when:
		def calculation = newSum([:], CODE(1), Type.TYPE_NUMBER)
		def period = newPeriod()
		def organisation = newOrganisationUnit(KIVUYE)
		newCalculationValue(calculation, period, organisation, false, false, Value.NULL)
		
		dataService.delete(calculation)
		
		then:
		Calculation.count() == 0
		CalculationValue.count() == 0
		
	}
	
}

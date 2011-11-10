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

import org.chai.kevin.data.Average;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Expression;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.ExpressionValue.Status;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class ValueServiceSpec extends IntegrationTests {

	def valueService;
	
	def "test number of values"() {
		setup:
		def period = newPeriod()
		def organisationUnit = newOrganisationUnit(BUTARO)
		
		when: 
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		newDataValue(dataElement, period, organisationUnit, v("40"))
		
		then:
		valueService.getNumberOfValues(dataElement, period) == 1
		
		when:
		def newPeriod = newPeriod()
		
		then:
		valueService.getNumberOfValues(dataElement, newPeriod) == 0
				
		when:
		def dataElement2 = newDataElement(CODE(2), Type.TYPE_NUMBER())
		
		then:
		valueService.getNumberOfValues(dataElement2, period) == 0
	}
	
	def "test value list"() {
		
		setup:
		def period = newPeriod()
		def organisationUnit = newOrganisationUnit(BUTARO)
		
		when:
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		def dataValue = newDataValue(dataElement, period, organisationUnit, v("40"))
		
		then:
		valueService.getValues(dataElement, period).equals([dataValue])
		
	}

	def "test delete expression values"() {
		setup:
		def period = newPeriod()
		def organisation = newOrganisationUnit(BUTARO)
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "1")
		
		when:
		newExpressionValue(expression, period, organisation)
		
		then:
		NormalizedDataElementValue.count() == 1
		
		when:
		valueService.deleteValues(expression)
		
		then:
		NormalizedDataElementValue.count() == 0
		
		when:
		def expression2 = newExpression(CODE(2), Type.TYPE_NUMBER(), "1")
		newExpressionValue(expression, period, organisation)
		newExpressionValue(expression2, period, organisation)
		valueService.deleteValues(expression)
		
		then:
		NormalizedDataElementValue.count() == 1
	}
	
	
	def "test delete calculation values"() {
		setup:
		def period = newPeriod()
		def organisation = newOrganisationUnit(BUTARO)
		def calculation = newAverage([:], CODE(1), Type.TYPE_NUMBER())
		
		when:
		newCalculationValue(calculation, period, organisation, false, false, v("1"))
		
		then:
		CalculationValue.count() == 1
		
		when:
		valueService.deleteValues(calculation)
		
		then:
		CalculationValue.count() == 0
		
		when:
		def calculation2 = newAverage([:], CODE(2), Type.TYPE_NUMBER())
		newCalculationValue(calculation, period, organisation, false, false, v("1"))
		newCalculationValue(calculation2, period, organisation, false, false, v("1"))
		valueService.deleteValues(calculation)
		
		then:
		CalculationValue.count() == 1
	}
	
}

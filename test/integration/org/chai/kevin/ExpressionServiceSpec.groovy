package org.chai.kevin;

import org.chai.kevin.data.Average;
import org.chai.kevin.data.Sum;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Expression;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionValue;
import org.chai.kevin.value.ExpressionValue.Status;
import org.chai.kevin.value.Value;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

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


public class ExpressionServiceSpec extends IntegrationTests {

	def expressionService;
	def valueService;
	
	def "test basic expression at facility level"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		def organisationUnit = OrganisationUnit.findByName(BUTARO)
		def expression = null
		def result = null
		
		when:
		newDataValue(dataElement, period, organisationUnit, v("40"))
		expression = newExpression(CODE(2), Type.TYPE_NUMBER(), "\$"+dataElement.id+" * 2")
		result = expressionService.calculate(expression, organisationUnit, period)
		
		then:
		result.value.numberValue == 80d
		result.status == Status.VALID
		
		when:
		expression = newExpression(CODE(3), Type.TYPE_NUMBER(), "\$0", [validate: false])
		result = expressionService.calculate(expression, organisationUnit, period)
		
		then:
		result.value == Value.NULL
		result.status == Status.MISSING_DATA_ELEMENT
		
		when:
		expression = newExpression(CODE(4), Type.TYPE_NUMBER(), "\$"+dataElement.id)
		result = expressionService.calculate(expression, OrganisationUnit.findByName(KIVUYE), period)
		
		then:
		result.value == Value.NULL
		result.status == Status.MISSING_NUMBER
				
		when:
		newDataValue(dataElement, period, OrganisationUnit.findByName(KIVUYE), Value.NULL)
		expression = newExpression(CODE(5), Type.TYPE_NUMBER(), "\$"+dataElement.id)
		result = expressionService.calculate(expression, OrganisationUnit.findByName(KIVUYE), period)
		
		then:
		result.value == Value.NULL
		result.status == Status.ERROR
		
	}
	
	def "test errors of typing"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def organisationUnit = OrganisationUnit.findByName(BUTARO)
		def expression = null
		def result = null
		
		when:
		expression = newExpression(CODE(2), type, formula)
		result = expressionService.calculate(expression, organisationUnit, period)
		
		then:
		result.status == Status.ERROR
		result.value == Value.NULL
		
		where:
		type			| formula
		Type.TYPE_BOOL()	| "1"
		Type.TYPE_NUMBER()| "true"
		
	}
	
	def "test expressions at different levels"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def kivuye = OrganisationUnit.findByName(KIVUYE)
		def butaro = OrganisationUnit.findByName(BUTARO)
		def expression = null
		def result = null
		
		when:
		def enume = newEnume(CODE(1))
		newEnumOption(enume, CODE(2), v("\"test\""))
		newEnumOption(enume, CODE(3), v("\"absent\""))
		
		def dataElement = newDataElement(CODE(4), Type.TYPE_ENUM (enume.code))
		newDataValue(dataElement, period, kivuye, v("\"absent\""))
		newDataValue(dataElement, period, butaro, v("\"test\""))
		
		expression = newExpression(CODE(5), Type.TYPE_NUMBER(), "if(\$"+dataElement.id+"==\"test\") 20 else 10")
		result = expressionService.calculate(expression, OrganisationUnit.findByName(organisationName), period)
		
		then:
		result.value == value
		result.status == status
		
		where:
		organisationName	| value		| status
		BUTARO				| v("20")	| Status.VALID
		KIVUYE				| v("10")	| Status.VALID
		BURERA				| Value.NULL| Status.NOT_AGGREGATABLE
		NORTH				| Value.NULL| Status.NOT_AGGREGATABLE
		RWANDA				| Value.NULL| Status.NOT_AGGREGATABLE
	}
		
	def "test valid expression at different levels"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def kivuye = OrganisationUnit.findByName(KIVUYE)
		def butaro = OrganisationUnit.findByName(BUTARO)
	
		when:
		def dataElement = newDataElement(CODE(6), Type.TYPE_NUMBER())
		newDataValue(dataElement, period, kivuye, v("10"))
		newDataValue(dataElement, period, butaro, v("20"))
		def expression = newExpression(CODE(7), Type.TYPE_NUMBER(), "\$"+dataElement.id)
		def result = expressionService.calculate(expression, OrganisationUnit.findByName(organisationName), period)
		
		then:
		result.value == value
		result.status == status
		
		where:
		organisationName	| value		| status
		BUTARO				| v("20")	| Status.VALID
		KIVUYE				| v("10")	| Status.VALID
		BURERA				| v("30")	| Status.VALID
		NORTH				| v("30")	| Status.VALID
		RWANDA				| v("30")	| Status.VALID
	}
	
	def "test sum with valid calculation"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "1")
		refreshExpression()
		
		when:
		def sum = newSum([(DISTRICT_HOSPITAL_GROUP): expression, (HEALTH_CENTER_GROUP): expression], CODE(2), Type.TYPE_NUMBER())
		def result = expressionService.calculate(sum, OrganisationUnit.findByName(organisationName), period)
		
		then:
		result.hasMissingValues == false
		result.hasMissingExpression == false  
		result.value == value
		
		where:
		organisationName	| value
		BUTARO				| v("1")
		KIVUYE				| v("1")
		BURERA				| v("2")
		NORTH				| v("2")
		RWANDA				| v("2")
	}
	
	def "test sum with missing expression"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		
		when:
		def sum = newSum([:], CODE(1), Type.TYPE_NUMBER())
		def result = expressionService.calculate(sum, OrganisationUnit.findByName(organisationName), period)
		
		then:
		result.hasMissingValues == false
		result.hasMissingExpression == true
		result.value == v("0")
	
		where:
		organisationName << [BUTARO, KIVUYE, BURERA, NORTH, RWANDA]
	}
		
	def "test sum with data element"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def dataElement = newDataElement(CODE(2), Type.TYPE_NUMBER())
		newDataValue(dataElement, period, OrganisationUnit.findByName(KIVUYE), v("1"))
		newDataValue(dataElement, period, OrganisationUnit.findByName(BUTARO), v("2"))
		def expression = newExpression(CODE(3), Type.TYPE_NUMBER(), "\$"+dataElement.id)
		refreshExpression()
		
		when:
		def sum = newSum([(DISTRICT_HOSPITAL_GROUP): expression, (HEALTH_CENTER_GROUP): expression], CODE(1), Type.TYPE_NUMBER())
		def result = expressionService.calculate(sum, OrganisationUnit.findByName(organisationName), period)
		
		then:
		result.hasMissingValues == false
		result.hasMissingExpression == false
		result.value == value
		
		where:
		organisationName	| value
		KIVUYE				| v("1")
		BUTARO				| v("2")
		BURERA				| v("3")
		NORTH				| v("3")
		RWANDA				| v("3")
	}
	
	def "test sum with missing values"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def dataElement = newDataElement(CODE(4), Type.TYPE_NUMBER())
		newDataValue(dataElement, period, OrganisationUnit.findByName(KIVUYE), v("1"))
		def expression = newExpression(CODE(5), Type.TYPE_NUMBER(), "\$"+dataElement.id)
		refreshExpression()
		
		when:
		def sum = newSum([(DISTRICT_HOSPITAL_GROUP): expression, (HEALTH_CENTER_GROUP): expression], CODE(1), Type.TYPE_NUMBER())
		def result = expressionService.calculate(sum, OrganisationUnit.findByName(organisationName), period)
		
		then:
		result.hasMissingValues == missingValues
		result.hasMissingExpression == false
		result.value == value
		
		where:
		organisationName	| value		| missingValues
		BUTARO				| v("0")	| true
		KIVUYE				| v("1")	| false
		BURERA				| v("1")	| true
		NORTH				| v("1")	| true	
		RWANDA				| v("1")	| true
		
	}
	
	def "test average"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def kivuye = OrganisationUnit.findByName(KIVUYE)
		def butaro = OrganisationUnit.findByName(BUTARO)
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "1")
		refreshExpression()
		
		when:
		def average = newAverage([(DISTRICT_HOSPITAL_GROUP): expression, (HEALTH_CENTER_GROUP): expression], CODE(2), Type.TYPE_NUMBER())
		def result = expressionService.calculate(average, OrganisationUnit.findByName(organisationName), period)
		
		then:
		result.hasMissingValues == false
		result.hasMissingExpression == false  
		result.value == value
		
		where:
		organisationName	| value
		BUTARO				| v("1")
		KIVUYE				| v("1")
		BURERA				| v("1")
		NORTH				| v("1")
		RWANDA				| v("1")
	}
	
	def "test average with missing expression"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
				
		when:
		def average = newAverage([:], CODE(1), Type.TYPE_NUMBER())
		def result = expressionService.calculate(average, OrganisationUnit.findByName(organisationName), period)
		
		then:
		result.hasMissingValues == false
		result.hasMissingExpression == true
		result.value == Value.NULL
		
		where:
		organisationName << [BUTARO, KIVUYE, BURERA, NORTH, RWANDA]
	}
	
	def "test average with data element"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def dataElement = newDataElement(CODE(2), Type.TYPE_NUMBER())
		newDataValue(dataElement, period, OrganisationUnit.findByName(KIVUYE), v("1"))
		newDataValue(dataElement, period, OrganisationUnit.findByName(BUTARO), v("2"))
		def expression = newExpression(CODE(3), Type.TYPE_NUMBER(), "\$"+dataElement.id)
		refreshExpression()
		
		when:
		def average = newAverage([(DISTRICT_HOSPITAL_GROUP): expression, (HEALTH_CENTER_GROUP): expression], CODE(1), Type.TYPE_NUMBER())
		def result = expressionService.calculate(average, OrganisationUnit.findByName(organisationName), period)
		
		then:
		result.hasMissingValues == false
		result.hasMissingExpression == false
		result.value == value
		
		where:
		organisationName	| value
		BUTARO				| v("2")
		KIVUYE				| v("1")
		BURERA				| v("1.5")
		NORTH				| v("1.5")
		RWANDA				| v("1.5")
	}
		
	def "test average with missing values"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def dataElement = newDataElement(CODE(4), Type.TYPE_NUMBER())
		newDataValue(dataElement, period, OrganisationUnit.findByName(KIVUYE), v("1"))
		def expression = newExpression(CODE(5), Type.TYPE_NUMBER(), "\$"+dataElement.id)
		refreshExpression()
		
		when:
		def average = newAverage([(DISTRICT_HOSPITAL_GROUP): expression, (HEALTH_CENTER_GROUP): expression], CODE(1), Type.TYPE_NUMBER())
		def result = expressionService.calculate(average, OrganisationUnit.findByName(organisationName), period)
		
		then:
		result.hasMissingValues == missingValues
		result.hasMissingExpression == false
		result.value == value
		
		where:
		organisationName	| value		| missingValues
		BUTARO				| Value.NULL| true
		KIVUYE				| v("1")	| false
		BURERA				| v("1")	| true
		NORTH				| v("1")	| true
		RWANDA				| v("1")	| true
	}
	
	def "data element in expression when wrong format"() {
		when:
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		def dataElements = expressionService.getDataInExpression("\$"+dataElement.id)
		
		then:
		dataElements.size() == 1
		dataElements.getAt("\$"+dataElement.id).equals(dataElement)
		
		when:
		dataElements = expressionService.getDataInExpression("\$0")
		
		then:
		dataElements.size() == 1
		dataElements.get("\$0") == null
		
		when:
		dataElements = expressionService.getDataInExpression("\$test")
		
		then:
		dataElements.size() == 0
	}
	
	def "data elements in expression"() {
		when:
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		def dataElements = expressionService.getDataInExpression("\$"+dataElement.id)
		
		then:
		dataElements.size() == 1
		dataElements.values().iterator().next().equals(dataElement)		
	}
	
	def "test expression validation"() {

		setup:
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		def formula = null
				
		when:
		formula = "(1"
		
		then:
		!expressionService.expressionIsValid(formula)
		
		when:
		formula = "if((10,1,0)"
		
		then:
		!expressionService.expressionIsValid(formula)
		
		when:
		formula = "123"
		
		then:
		expressionService.expressionIsValid(formula)
		
		when:
		formula = "\$"+dataElement.id+" == 1"
		
		then:
		expressionService.expressionIsValid(formula)
		
		when:
		formula = "\$"+dataElement.id+" == \"a\""
		
		then:
		!expressionService.expressionIsValid(formula)
		
		when:
		formula = "\$0"
		
		then:
		!expressionService.expressionIsValid(formula)
		
		when:
		formula = "if (\$"+dataElement.id+" == null) true else false"
		
		then:
		expressionService.expressionIsValid(formula)
		
		when:
		def dataElement2 = newDataElement(CODE(2), Type.TYPE_STRING())
		formula = "convert(\$"+dataElement2.id+", schema double)"
		
		then:
		expressionService.expressionIsValid(formula)
		
	}
	
}

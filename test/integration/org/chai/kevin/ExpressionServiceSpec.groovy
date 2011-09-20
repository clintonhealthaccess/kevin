package org.chai.kevin;

import org.chai.kevin.data.Average;
import org.chai.kevin.data.Sum;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Expression;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionValue;
import org.chai.kevin.value.ExpressionValue.Status;
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
	
	def setup() {
		Initializer.createDummyStructure();
		IntegrationTestInitializer.createExpressions()
	}
	
	def setupData() {
		new DataElement(names:j(["en":"Element Int"]), code: "CODEINT", descriptions:j(["en":"Description"]), type: JSONUtils.TYPE_NUMBER).save(faileOnError: true)
		
		new DataValue(
			dataElement: DataElement.findByCode("CODEINT"),
			period: Period.list()[1],
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			value: v("1"),
			timestamp: new Date()
		).save(failOnError: true)
		
		new DataValue(
			dataElement: DataElement.findByCode("CODEINT"),
			period: Period.list()[1],
			organisationUnit: OrganisationUnit.findByName("Kivuye HC"),
			value: v("2"),
			timestamp: new Date()
		).save(failOnError: true)
		
		new Expression(names:j(["en":"Enum"]), code:"EXPRBOOL", expression: "if(\$"+DataElement.findByCode("CODEINT").id+"==2) 1 else 0", type: JSONUtils.TYPE_BOOL, timestamp: new Date()).save(failOnError: true)
		new Expression(names:j(["en":"Enum"]), code:"EXPRCONST", expression: "1", type: JSONUtils.TYPE_BOOL, timestamp: new Date()).save(failOnError: true)
	}
	
	def setupSum() {
		new Sum(expressions: [
			"District Hospital": Expression.findByCode("EXPRBOOL"),
			"Health Center": Expression.findByCode("EXPRBOOL")
		], timestamp:new Date(), type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		new Sum(expressions: [
			"District Hospital": Expression.findByCode("EXPRCONST"),
			"Health Center": Expression.findByCode("EXPRCONST")
		], timestamp:new Date(), type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
	}

	
	def "normal expression"() {
		setup:
		IntegrationTestInitializer.createDataElements();
		
		new DataValue(
			dataElement: DataElement.findByCode("CODE"),
			period: Period.list()[1],
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			value: v("40"),
			timestamp: new Date(),
		).save(failOnError: true)
		
		when:
		def expression = new Expression(names:j(["en":"Enum"]), code:"EXPR", expression: formula, type: JSONUtils.TYPE_NUMBER, timestamp: new Date()).save(failOnError: true)
		def organisation = IntegrationTests.getOrganisation(organisationName)
		def period = Period.list()[1]
		
		then:
		def result = expressionService.calculate(expression, organisation.organisationUnit, period)
		result.value == value
		result.status == status
		
		where:
		formula					| organisationName	| value		| status
		"1 == 0"			 	| "Kivuye HC"		| "false"	| Status.VALID
		"\$7 + 40" 				| "Butaro DH"		| "80"		| Status.VALID
		"if (\$8==1) 1 else 0"	| "Kivuye HC"		| null		| Status.MISSING_DATA_ELEMENT
		"\$15==\"a\""			| "Butaro DH"		| null		| Status.INVALID
	}
	
	def "aggregated value"() {
		setup:
		def enume = new Enum(code:"ENUM").save(failOnError: true)
		enume.addEnumOption(new EnumOption(code: "ENUMtest", value: "test"))
		enume.addEnumOption(new EnumOption(code: "ENUMtest", value: "absent"))
		enume.save(failOnError: true)
		new DataElement(names:j(["en":"Element Enum"]), code: "CODEENUM", descriptions:j(["en":"Description"]), type: JSONUtils.TYPE_ENUM (enume.id)).save(faileOnError: true, flush: true)
		new DataElement(names:j(["en":"Element Int"]), code: "CODEINT", descriptions:j(["en":"Description"]), type: JSONUtils.TYPE_NUMBER).save(faileOnError: true)
		
		new DataValue(
			dataElement: DataElement.findByCode("CODEENUM"),
			period: Period.list()[1],
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			value: v("\"test\""),
			timestamp: new Date()
		).save(failOnError: true)
		
		new DataValue(
			dataElement: DataElement.findByCode("CODEENUM"),
			period: Period.list()[1],
			organisationUnit: OrganisationUnit.findByName("Kivuye HC"),
			value: v("\"absent\""),
			timestamp: new Date()
		).save(failOnError: true)
		
		new DataValue(
			dataElement: DataElement.findByCode("CODEINT"),
			period: Period.list()[1],
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			value: v("20"),
			timestamp: new Date()
		).save(failOnError: true)
		
		new DataValue(
			dataElement: DataElement.findByCode("CODEINT"),
			period: Period.list()[1],
			organisationUnit: OrganisationUnit.findByName("Kivuye HC"),
			value: v("10"),
			timestamp: new Date()
		).save(failOnError: true)
		
		new Expression(names:j(["en":"Enum"]), code:"EXPRENUM", expression: "if(\$"+DataElement.findByCode("CODEENUM").id+"==\"test\") 20 else 10", type: JSONUtils.TYPE_NUMBER, timestamp: new Date()).save(failOnError: true)
		new Expression(names:j(["en":"Int"]), code:"EXPRINT", expression: "\$"+DataElement.findByCode("CODEINT").id, type: JSONUtils.TYPE_NUMBER, timestamp: new Date()).save(failOnError: true)
		
		when:
		def period = Period.list()[1]
		def expression = Expression.findByCode(expressionName)
		def organisation = IntegrationTests.getOrganisation(organisationName)
		ExpressionValue value = expressionService.calculate(expression, organisation.organisationUnit, period)
		
		then:
		value.status == status
		if (value.status == Status.VALID) value.value == expectedValue+""
				
		where:
		expressionName	| organisationName	| status				| expectedValue
		"CONST10"		| "Butaro DH"		| Status.VALID			| 10d
		"CONST10"		| "Kivuye HC"		| Status.VALID			| 10d
		"CONST10"		| "Burera"			| Status.VALID			| 10d
		"CONST10"		| "North"			| Status.VALID			| 10d
		"EXPRENUM"		| "Butaro DH"		| Status.VALID			| 20d
		"EXPRENUM"		| "Kivuye HC"		| Status.VALID			| 10d
		"EXPRENUM"		| "Burera"			| Status.NOT_AGGREGATABLE	| null
		"EXPRENUM"		| "North"			| Status.NOT_AGGREGATABLE	| null
		"EXPRINT"		| "Butaro DH"		| Status.VALID			| 20d
		"EXPRINT"		| "Kivuye HC"		| Status.VALID			| 10d
		"EXPRINT"		| "Burera"			| Status.VALID			| 30d
	}
	
	def "test sum"() {
		setup:
		setupData();
		setupSum();
		expressionService.refreshExpressions()	

		when:
		def period = Period.list()[1]
		def organisation = IntegrationTests.getOrganisation(organisationName)
		def countT = Sum.list()[countNum]
		CalculationValue value = expressionService.calculate(countT, organisation.organisationUnit, period)
		
		then:
		value.value == expectedValue+""
				
		where:
		countNum	| organisationName	| expectedValue
		0			| "Butaro DH"		| 0d
		0			| "Kivuye HC"		| 1d
		0			| "Burera"			| 1d
		1			| "Butaro DH"		| 1d
		1			| "Kivuye HC"		| 1d
		1			| "Burera"			| 2d
	}
	
	def "test average"() {
		setup:
		setupData();
		
		new Average(expressions: [
			"District Hospital": Expression.findByCode("EXPRBOOL"),
			"Health Center": Expression.findByCode("EXPRBOOL")
		], timestamp:new Date(), type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		new Average(expressions: [
			"District Hospital": Expression.findByCode("EXPRCONST"),
			"Health Center": Expression.findByCode("EXPRCONST")
		], timestamp:new Date(), type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		
		expressionService.refreshExpressions();
		
		when:
		def period = Period.list()[1]
		def organisation = IntegrationTests.getOrganisation(organisationName)
		def average = Average.list()[averageNum]
		CalculationValue value = expressionService.calculate(average, organisation.organisationUnit, period)
		
		then:
		value.value == expectedValue+""
				
		where:
		averageNum	| organisationName	| expectedValue
		0			| "Butaro DH"		| 0d
		0			| "Kivuye HC"		| 1d
		0			| "Burera"			| 0.5d
		1			| "Butaro DH"		| 1d
		1			| "Kivuye HC"		| 1d
		1			| "Burera"			| 1d
	}
	
	def "test sum refresh"() {
		setup:
		setupData();
		setupSum();
		expressionService.refreshExpressions();
		expressionService.refreshCalculations();
		
		when:
		def period = Period.list()[1]
		def organisation = IntegrationTests.getOrganisation(organisationName)
		def countT = Sum.list()[countNum]
		CalculationValue value = valueService.getValue(countT, organisation.organisationUnit, period)
		
		then:
		value.value == expectedValue+""
				
		where:
		countNum	| organisationName	| expectedValue
		0			| "Butaro DH"		| 0d
		0			| "Kivuye HC"		| 1d
		0			| "Burera"			| 1d
		1			| "Butaro DH"		| 1d
		1			| "Kivuye HC"		| 1d
		1			| "Burera"			| 2d
		
	}
	
	def "data element in expression when wrong format"() {
		when:
		def dataElements = expressionService.getDataInExpression("\$1")
		
		then:
		dataElements.size() == 1
		
		when:
		dataElements = expressionService.getDataInExpression("\$test")
		
		then:
		dataElements.size() == 0
	}
	
	def "data elements in expression"() {
		setup: 
		new DataElement(names:j(["en":"Element Int"]), code: "CODEINT", descriptions:j(["en":"Description"]), type: JSONUtils.TYPE_NUMBER).save(faileOnError: true)

		when:
		def dataElements = expressionService.getDataInExpression("\$"+DataElement.findByCode("CODEINT").id)
		
		then:
		dataElements.size() == 1
		dataElements.values().iterator().next().equals(DataElement.findByCode("CODEINT"))		
	}
	
	def "test expression validation"() {

		setup:
		def dataElement = new DataElement(names:j(["en":"Element Int"]), code: "CODEINT", descriptions:j(["en":"Description"]), type: JSONUtils.TYPE_NUMBER).save(faileOnError: true)
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
		formula = "if (\$"+dataElement.id+" == null) true else false"
		
		then:
		expressionService.expressionIsValid(formula)
		
		when:
		dataElement = new DataElement(names:j(["en":"Element Int"]), code: "CODESTRING", descriptions:j(["en":"Description"]), type: JSONUtils.TYPE_STRING).save(faileOnError: true)
		formula = "convert(\$"+dataElement.id+", schema double)"
		
		then:
		expressionService.expressionIsValid(formula)
		
		
	}
	
}

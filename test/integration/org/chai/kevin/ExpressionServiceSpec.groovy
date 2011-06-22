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

import org.chai.kevin.value.ExpressionValue.Status;
import java.util.Date;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.Initializer;
import org.chai.kevin.DataElement;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionValue;
import org.chai.kevin.value.ExpressionValue.Status;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class ExpressionServiceSpec extends IntegrationTests {

	ExpressionService expressionService;
	
	def setup() {
		
		Initializer.createDummyStructure();
		IntegrationTestInitializer.createExpressions()
	}
	
	def "aggregated value"() {
		setup:
		new DataElement(names:j(["en":"Element Enum"]), code: "CODEENUM", descriptions:j(["en":"Description"]), type: ValueType.ENUM).save(faileOnError: true)
		new DataElement(names:j(["en":"Element Int"]), code: "CODEINT", descriptions:j(["en":"Description"]), type: ValueType.VALUE).save(faileOnError: true)
		
		new DataValue(
			dataElement: DataElement.findByCode("CODEENUM"),
			period: Period.list()[1],
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			value: "test",
			timestamp: new Date()
		).save(failOnError: true)
		
		new DataValue(
			dataElement: DataElement.findByCode("CODEENUM"),
			period: Period.list()[1],
			organisationUnit: OrganisationUnit.findByName("Kivuye HC"),
			value: "absent",
			timestamp: new Date()
		).save(failOnError: true)
		
		new DataValue(
			dataElement: DataElement.findByCode("CODEINT"),
			period: Period.list()[1],
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			value: "20",
			timestamp: new Date()
		).save(failOnError: true)
		
		new DataValue(
			dataElement: DataElement.findByCode("CODEINT"),
			period: Period.list()[1],
			organisationUnit: OrganisationUnit.findByName("Kivuye HC"),
			value: "10",
			timestamp: new Date()
		).save(failOnError: true)
		
		new Expression(names:j(["en":"Enum"]), code:"EXPRENUM", expression: "if(\"["+DataElement.findByCode("CODEENUM").id+"]\"==\"test\",20,10)", type: ValueType.VALUE, timestamp: new Date()).save(failOnError: true)
		new Expression(names:j(["en":"Int"]), code:"EXPRINT", expression: "["+DataElement.findByCode("CODEINT").id+"]", type: ValueType.VALUE, timestamp: new Date()).save(failOnError: true)
		
		when:
		def period = Period.list()[1]
		def expression = Expression.findByCode(expressionName)
		def organisation = IntegrationTests.getOrganisation(organisationName)
		ExpressionValue value = expressionService.calculateValue(expression, period, organisation)
		
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

	def "data element in expression when wrong format"() {
		when:
		def dataElements = expressionService.getDataElementsInExpression("[test]")
		
		then:
		dataElements.size() == 0
	}
	
	def "data elements in expression"() {
		setup: 
		new DataElement(names:j(["en":"Element Int"]), code: "CODEINT", descriptions:j(["en":"Description"]), type: ValueType.VALUE).save(faileOnError: true)

		when:
		def dataElements = expressionService.getDataElementsInExpression("["+DataElement.findByCode("CODEINT").id+"]")
		
		then:
		dataElements.size() == 1
		dataElements.iterator().next().equals(DataElement.findByCode("CODEINT"))		
	}
}

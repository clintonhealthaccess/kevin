package org.chai.kevin

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
		"EXPRENUM"		| "Burera"			| Status.MISSING_VALUE	| null
		"EXPRENUM"		| "North"			| Status.MISSING_VALUE	| null
		"EXPRINT"		| "Butaro DH"		| Status.VALID			| 20d
		"EXPRINT"		| "Kivuye HC"		| Status.VALID			| 10d
		"EXPRINT"		| "Burera"			| Status.VALID			| 30d
	}

	
}

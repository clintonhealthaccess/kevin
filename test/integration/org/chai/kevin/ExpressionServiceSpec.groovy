package org.chai.kevin

import java.util.Date;

import org.chai.kevin.ExpressionService;
import org.chai.kevin.Initializer;
import org.chai.kevin.DataElement;
import org.chai.kevin.DataValue;
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
//			optionCombo: DataElementCategoryOptionCombo.list()[0],
			value: "test",
//			comment: "Comment",
//			storedBy: "StoredBy",
			timestamp: new Date()
//			followup: false,
		).save(failOnError: true)
		
		new DataValue(
				dataElement: DataElement.findByCode("CODEENUM"),
				period: Period.list()[1],
	//			optionCombo: DataElementCategoryOptionCombo.list()[0],
				organisationUnit: OrganisationUnit.findByName("Kivuye HC"),
			value: "absent",
//			comment: "Comment",
//			storedBy: "StoredBy",
			timestamp: new Date()
//			followup: false,
		).save(failOnError: true)
		
		new DataValue(
				dataElement: DataElement.findByCode("CODEINT"),
				period: Period.list()[1],
	//			optionCombo: DataElementCategoryOptionCombo.list()[0],
				organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			value: "20",
//			comment: "Comment",
//			storedBy: "StoredBy",
			timestamp: new Date()
//			followup: false,
		).save(failOnError: true)
		
		new DataValue(
				dataElement: DataElement.findByCode("CODEINT"),
				period: Period.list()[1],
	//			optionCombo: DataElementCategoryOptionCombo.list()[0],
				organisationUnit: OrganisationUnit.findByName("Kivuye HC"),
			value: "10",
//			comment: "Comment",
//			storedBy: "StoredBy",
			timestamp: new Date()
//			followup: false,
		).save(failOnError: true)
		
		new Expression(names:j(["en":"Enum"]), code:"EXPRENUM", expression: "if(\"["+DataElement.findByCode("CODEENUM").id+"]\"==\"test\",20,10)", type: ValueType.VALUE).save(failOnError: true)
		new Expression(names:j(["en":"Int"]), code:"EXPRINT", expression: "["+DataElement.findByCode("CODEINT").id+"]", type: ValueType.VALUE).save(failOnError: true)
		
		
		when:
		def period = Period.list()[1]
		def expression = Expression.findByCode(expressionName)
		def organisation = IntegrationTests.getOrganisation(organisationName)
		Double value = expressionService.getAggregatedValue(expression, period, organisation, new HashMap())
		
		then:
		value == expectedValue
				
		where:
		expressionName	| organisationName	| expectedValue
		"CONST10"		| "Butaro DH"		| 10d
		"CONST10"		| "Kivuye HC"		| 10d
		"CONST10"		| "Burera"			| 10d
		"CONST10"		| "North"			| 10d
		"EXPRENUM"		| "Butaro DH"		| 20d
		"EXPRENUM"		| "Kivuye HC"		| 10d
		"EXPRENUM"		| "Burera"			| 15d
		"EXPRINT"		| "Butaro DH"		| 20d
		"EXPRINT"		| "Kivuye HC"		| 10d
		"EXPRINT"		| "Burera"			| 30d
		
		
	}

	
}

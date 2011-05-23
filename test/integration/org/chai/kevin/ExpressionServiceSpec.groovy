package org.chai.kevin

import java.util.Date;

import org.chai.kevin.DataElement.DataElementType;
import org.chai.kevin.Expression.ExpressionType;
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
		new DataElement(name:"Element Enum", shortName: "Element Enum", code: "CODEENUM", description: "Description", type: DataElementType.ENUM).save(faileOnError: true)
		new DataElement(name:"Element Int", shortName: "Element Int", code: "CODEINT", description: "Description", type: DataElementType.INT).save(faileOnError: true)
		
		new DataValue(
				dataElement: DataElement.findByName("Element Enum"),
				period: Period.list()[1],
				source: OrganisationUnit.findByName("Butaro DH"),
//			optionCombo: DataElementCategoryOptionCombo.list()[0],
			value: "test",
//			comment: "Comment",
//			storedBy: "StoredBy",
			timestamp: new Date(),
//			followup: false,
		).save(failOnError: true)
		
		new DataValue(
				dataElement: DataElement.findByName("Element Enum"),
				period: Period.list()[1],
	//			optionCombo: DataElementCategoryOptionCombo.list()[0],
				source: OrganisationUnit.findByName("Kivuye HC"),
			value: "absent",
//			comment: "Comment",
//			storedBy: "StoredBy",
			timestamp: new Date(),
//			followup: false,
		).save(failOnError: true)
		
		new DataValue(
				dataElement: DataElement.findByName("Element Int"),
				period: Period.list()[1],
	//			optionCombo: DataElementCategoryOptionCombo.list()[0],
				source: OrganisationUnit.findByName("Butaro DH"),
			value: "20",
//			comment: "Comment",
//			storedBy: "StoredBy",
			timestamp: new Date(),
//			followup: false,
		).save(failOnError: true)
		
		new DataValue(
				dataElement: DataElement.findByName("Element Int"),
				period: Period.list()[1],
	//			optionCombo: DataElementCategoryOptionCombo.list()[0],
				source: OrganisationUnit.findByName("Kivuye HC"),
			value: "10",
//			comment: "Comment",
//			storedBy: "StoredBy",
			timestamp: new Date(),
//			followup: false,
		).save(failOnError: true)
		
		new Expression(name:"Enum", expression: "if(\"["+DataElement.findByName("Element Enum").id+"]\"==\"test\",20,10)", type: ExpressionType.VALUE).save(failOnError: true)
		new Expression(name:"Int", expression: "["+DataElement.findByName("Element Int").id+"]", type: ExpressionType.VALUE).save(failOnError: true)
		
		
		when:
		def period = Period.list()[1]
		def expression = Expression.findByName(expressionName)
		def organisation = IntegrationTests.getOrganisation(organisationName)
		Double value = expressionService.getAggregatedValue(expression, period, organisation, new HashMap())
		
		then:
		value == expectedValue
				
		where:
		expressionName	| organisationName	| expectedValue
		"Constant 10"	| "Butaro DH"		| 10d
		"Constant 10"	| "Kivuye HC"		| 10d
		"Constant 10"	| "Burera"			| 10d
		"Constant 10"	| "North"			| 10d
		"Enum"			| "Butaro DH"		| 20d
		"Enum"			| "Kivuye HC"		| 10d
		"Enum"			| "Burera"			| 15d
		"Int"			| "Butaro DH"		| 20d
		"Int"			| "Kivuye HC"		| 10d
		"Int"			| "Burera"			| 30d
		
		
	}

	
}

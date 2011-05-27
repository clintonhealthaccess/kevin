package org.hisp.dhis

import java.util.Date;

import org.chai.kevin.ExpressionService;
import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class AggregationSpec extends IntegrationTests {

	ExpressionService expressionService;
	
	def setup() {
		Initializer.createDummyStructure();
		IntegrationTestInitializer.createExpressions()
		IntegrationTestInitializer.createDashboard();
		IntegrationTestInitializer.createDataElements()
		IntegrationTestInitializer.addNonConstantData();
	}
	
	def "call twice in a row"() {
		
		when:
		def period = Period.list()[1]
		def dataElement = DataElement.findByCode(dataElementCode)
		def organisation = getOrganisation(organisationName)
		
		then:
		expressionService.getDataValue(dataElement, period, organisation, [:]) == value
				
		where:
		dataElementCode	| organisationName	| value
		"CODE"			| "Butaro DH"		| 40d
		
	}
	
//	def "ids"() {
//		
//		expect:
//		def periods = Period.list()
//		periods.size() == 1
//		periods[0].id == periodId
//		def dataElements = DataElement.list()
//		dataElements.size() == 1
//		dataElements[0].id == dataElementId
//		
//		where:
//		periodId	| dataElementId
//		1			| 1
//		2			| 2
//		3			| 3
//		4			| 4
//		
//	}
	
}

package org.hisp.dhis

import java.util.Date;

import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.aggregation.impl.cache.AggregationCache;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class AggregationSpec extends IntegrationTests {

	AggregationService aggregationService;
	
	def setup() {
		Initializer.createDummyStructure();
		IntegrationTestInitializer.createExpressions()
		IntegrationTestInitializer.createDashboard();
		IntegrationTestInitializer.createDataElements()
		IntegrationTestInitializer.addNonConstantData();
	}
	
	def "call twice in a row"() {
		
		when:
		def dataElement = DataElement.findByName(dataElementName)
		def organisation = OrganisationUnit.findByName(organisationName)
		
		then:
		aggregationService.getNonAggregatedDataValue(dataElement, null, Initializer.mar011, Initializer.mar311, organisation) == value+""
		aggregationService.getAggregatedDataValue(dataElement, null, Initializer.mar011, Initializer.mar311, organisation) == new Double(value)

				
		where:
		dataElementName	| organisationName	| value
		"Element 1"		| "Butaro DH"		| 40
		
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

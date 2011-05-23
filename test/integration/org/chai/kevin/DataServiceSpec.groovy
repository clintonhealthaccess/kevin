package org.chai.kevin

import org.hisp.dhis.dataelement.Constant;
import org.chai.kevin.DataElement;

import grails.plugin.spock.UnitSpec;

class DataServiceSpec extends IntegrationTests {

	def dataService;
	
	def setup() {
		Initializer.createDummyStructure()
		IntegrationTestInitializer.createDataElements()
		IntegrationTestInitializer.createConstants()
	}
	
	def "search for constant works"() {
		expect:
		def constants = dataService.searchConstants("con")
		constants == [Constant.findByName("Constant 1000")]
	}
	
	def "search for data element works"() {
		expect:
		def dataElements = dataService.searchDataElements("ele", null)
		dataElements == [DataElement.findByName("Element 1")]
	}
	
}

package org.chai.kevin

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
		constants == [Constant.findByCode("CONST1")]
	}
	
	def "search for data element works"() {
		expect:
		def dataElements = dataService.searchDataElements("ele")
		dataElements == [DataElement.findByCode("CODE")]
	}
	
}

package org.chai.kevin

class FilterTagLibSpec extends IntegrationTests {

	def filterTagLib
	
	def "organisation sets missing level"() {
		
		setup:
		setupOrganisationUnitTree()
		filterTagLib = new FilterTagLib()
		
		expect:
		filterTagLib.createLinkByFilter([
			controller:'controller', 
			action:'action', 
			params: [organisation: getOrganisation(RWANDA).id, filter: 'organisation']
		], null) == "/controller/action?level=2&organisation="+getOrganisation(RWANDA).id+"&filter=organisation"
		
	}
	
}

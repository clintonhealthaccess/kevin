package org.chai.kevin

import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;

class FilterTagLibSpec extends IntegrationTests {

	def filterTagLib
	
	def "organisation sets missing level"() {
		
		setup:
		setupLocationTree()
		filterTagLib = new FilterTagLib()
		
		expect:
		filterTagLib.createLinkByFilter([
			controller:'controller', 
			action:'action', 
			params: [organisation: LocationEntity.findByCode(RWANDA).id+'', filter: 'organisation']
		], null) == "/controller/action?level="+LocationLevel.findByCode(PROVINCE).id+"&organisation="+LocationEntity.findByCode(RWANDA).id+"&filter=organisation"
		
	}
	
}

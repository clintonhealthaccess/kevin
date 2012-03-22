package org.chai.kevin

import org.chai.kevin.data.Type
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram;
import org.hisp.dhis.period.Period;

class FilterTagLibSpec extends IntegrationTests {

	def filterTagLib	
	
	def "location sets missing level"() {
		setup:
		setupLocationTree()
		filterTagLib = new FilterTagLib()
		
		expect:
		filterTagLib.createLinkByFilter([
			controller:'controller',
			action:'action',
			params: [location: LocationEntity.findByCode(RWANDA).id+'', filter: 'location']
		], null) == "/controller/action?level="+LocationLevel.findByCode(PROVINCE).id+"&location="+LocationEntity.findByCode(RWANDA).id+"&filter=location"
	}

}
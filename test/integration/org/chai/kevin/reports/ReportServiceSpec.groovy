package org.chai.kevin.reports

import org.chai.kevin.location.LocationLevel;

class ReportServiceSpec extends ReportIntegrationTests {

	def reportService
	
	def "get report skip levels"(){
		setup:
		setupLocationTree()
		
		when:
		def reportSkipLevels = reportService.getSkipLocationLevels(null)
		
		then:
		reportSkipLevels.equals(s([LocationLevel.findByCode(SECTOR)]))
	}
	
}

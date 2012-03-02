package org.chai.kevin.reports

import org.chai.kevin.location.LocationLevel;

class ReportServiceSpec extends ReportIntegrationTests {

	def reportService
	
	def "get report skip levels"(){
		setup:
		setupLocationTree()
		
		when:
		def skipLevels = reportService.getSkipLevels()
		def reportSkipLevels = reportService.getSkipLocationLevels(null)
		
		then:
		getLocationLevels(skipLevels) == [LocationLevel.findByCode(SECTOR)]
		reportSkipLevels == [LocationLevel.findByCode(SECTOR)]
	}
	
//	def "get default report skip levels when null"(){
//		setup:
//		setupLocationTree()
//		
//		when:
//		def skipLevels = reportService.getSkipLevels()
//		
//		then:
//		def skipLocationLevels = reportService.getSkipLocationLevels(null)
//		skipLocationLevels.size() == 1
//		skipLocationLevels == [LocationLevel.findByCode(SECTOR)]
//		
//	}
}

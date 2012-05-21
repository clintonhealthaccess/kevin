package org.chai.kevin.reports

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.dashboard.DashboardIntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.dsr.DsrIntegrationTests;
import org.chai.kevin.dsr.DsrTarget;
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
	
	def "get program tree"() {
		setup:
		newPeriod()
		setupProgramTree()
		
		expect:
		reportService.getProgramTree(DsrTarget.class).empty
		
		when:
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_STRING())
		DsrIntegrationTests.newDsrTarget(CODE(1), 1, dataElement, ReportProgram.findByCode(PROGRAM1))
		
		then:
		reportService.getProgramTree(DsrTarget.class).equals([ReportProgram.findByCode(PROGRAM1), ReportProgram.findByCode(ROOT)])
	}
	
}

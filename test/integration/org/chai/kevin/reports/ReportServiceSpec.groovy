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
	
	def "collect report tree"() {
		setup:
		newPeriod()
		setupProgramTree()
		def collectedPrograms
		def collectedTargets
		
		when:
		collectedPrograms = []
		collectedTargets = []
		reportService.collectReportTree(DsrTarget.class, ReportProgram.findByCode(ROOT), collectedPrograms, collectedTargets)
		
		then:
		collectedPrograms.empty
		collectedTargets.empty
		
		when:
		collectedPrograms = []
		collectedTargets = []
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_STRING())
		def category = DsrIntegrationTests.newDsrTargetCategory(CODE(2), 1)
		def target = DsrIntegrationTests.newDsrTarget(CODE(1), 1, dataElement, ReportProgram.findByCode(PROGRAM1), category)
		reportService.collectReportTree(DsrTarget.class, ReportProgram.findByCode(PROGRAM1), collectedPrograms, collectedTargets)
		
		then:
		collectedPrograms.equals([ReportProgram.findByCode(PROGRAM1)])
		collectedTargets.equals([target])
		
		when:
		collectedPrograms = []
		collectedTargets = []
		reportService.collectReportTree(DsrTarget.class, ReportProgram.findByCode(ROOT), collectedPrograms, collectedTargets)
		
		then:
		collectedPrograms.equals([ReportProgram.findByCode(PROGRAM1), ReportProgram.findByCode(ROOT)])
		collectedTargets.equals([target])
	}
	
}

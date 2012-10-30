package org.chai.kevin.reports

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.dashboard.DashboardIntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.dsr.DsrIntegrationTests;
import org.chai.kevin.dsr.DsrTarget;
import org.chai.kevin.fct.FctIntegrationTests;
import org.chai.location.LocationLevel;

class ReportServiceSpec extends ReportIntegrationTests {

	def reportService
	
	def "get report skip levels"(){
		setup:
		setupLocationTree()
		
		when:
		def reportSkipLevels = reportService.getSkipReportLevels(null)
		
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
	
	def "get report targets"() {
		setup:
		def sum = newSum("1", CODE(2))
		def program = newReportProgram(CODE(1))
		
		when:
		def dashboardProgram = DashboardIntegrationTests.newDashboardProgram(CODE(2), program)
		def dashboardTarget = DashboardIntegrationTests.newDashboardTarget(CODE(3), sum, program, 1)
		
		then:
		reportService.getReportTargets(sum) == [dashboardTarget]
		
		when:
		def dsrCategory = DsrIntegrationTests.newDsrTargetCategory(CODE(4), 1)
		def dsrTarget = DsrIntegrationTests.newDsrTarget(CODE(5), sum, program, dsrCategory)
		
		then:
		reportService.getReportTargets(sum) == [dashboardTarget, dsrTarget]
		
		when:
		def fctTarget = FctIntegrationTests.newFctTarget(CODE(6), program)
		def fctTargetOption = FctIntegrationTests.newFctTargetOption(CODE(7), fctTarget, sum)
		
		then:
		reportService.getReportTargets(sum) == [dashboardTarget, dsrTarget, fctTargetOption]
	}
	
}

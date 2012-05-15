package org.chai.kevin.reports

class ReportProgramControllerSpec extends ReportIntegrationTests {

	def reportProgramController
	
	def "save program"() {
		setup:
		reportProgramController = new ReportProgramController()
		
		when:
		reportProgramController.params.code = CODE(2)
		reportProgramController.saveWithoutTokenCheck()
		
		then:
		ReportProgram.count() == 1
	}
	
	def "list programs"() {
		setup:
		def program = newReportProgram(CODE(1))
		reportProgramController = new ReportProgramController()
		
		when:
		reportProgramController.list()
		
		then:
		reportProgramController.modelAndView.model.entities.equals([program])
	}
	
}

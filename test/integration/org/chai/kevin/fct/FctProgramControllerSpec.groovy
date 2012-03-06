package org.chai.kevin.fct

import org.chai.kevin.reports.ReportProgram;

class FctProgramControllerSpec extends FctIntegrationTests {

	def fctProgramController
	def reportProgram
	
	def "create program"() {
		setup:
		fctProgramController = new FctProgramController()
		
		when:
		fctProgramController.params.code = CODE(1)
		fctProgramController.saveWithoutTokenCheck()
		
		then:
		ReportProgram.count() == 1
	}
	
}

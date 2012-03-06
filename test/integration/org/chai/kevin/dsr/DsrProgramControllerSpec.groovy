package org.chai.kevin.dsr

import org.chai.kevin.reports.ReportProgram;

class DsrProgramControllerSpec extends DsrIntegrationTests {

	def dsrProgramController
	
	def "create program"() {
		setup:
		dsrProgramController = new DsrProgramController()
		
		when:
		dsrProgramController.params.code = CODE(1)
		dsrProgramController.saveWithoutTokenCheck()
		
		then:
		ReportProgram.count() == 1
	}
	
}

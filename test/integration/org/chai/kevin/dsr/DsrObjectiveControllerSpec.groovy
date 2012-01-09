package org.chai.kevin.dsr

import org.chai.kevin.reports.ReportObjective;

class DsrObjectiveControllerSpec extends DsrIntegrationTests {

	def dsrObjectiveController
	
	def "create objective"() {
		setup:
		dsrObjectiveController = new DsrObjectiveController()
		
		when:
		dsrObjectiveController.params.code = CODE(1)
		dsrObjectiveController.saveWithoutTokenCheck()
		
		then:
		ReportObjective.count() == 1
	}
	
}

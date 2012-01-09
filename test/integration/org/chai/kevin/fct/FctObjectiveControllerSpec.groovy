package org.chai.kevin.fct

import org.chai.kevin.reports.ReportObjective;

class FctObjectiveControllerSpec extends FctIntegrationTests {

	def fctObjectiveController
	def reportObjective
	
	def "create objective"() {
		setup:
		fctObjectiveController = new FctObjectiveController()
		
		when:
		fctObjectiveController.params.code = CODE(1)
		fctObjectiveController.saveWithoutTokenCheck()
		
		then:
		ReportObjective.count() == 1
	}
	
}

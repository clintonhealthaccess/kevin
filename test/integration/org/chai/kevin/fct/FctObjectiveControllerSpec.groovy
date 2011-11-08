package org.chai.kevin.fct

class FctObjectiveControllerSpec extends FctIntegrationTests {

	def fctObjectiveController
	
	def "create objective"() {
		setup:
		fctObjectiveController = new FctObjectiveController()
		
		when:
		fctObjectiveController.params.code = CODE(1)
		fctObjectiveController.saveWithoutTokenCheck()
		
		then:
		FctObjective.count() == 1
	}
	
}

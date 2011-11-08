package org.chai.kevin.dsr

class DsrObjectiveControllerSpec extends DsrIntegrationTests {

	def dsrObjectiveController
	
	def "create objective"() {
		setup:
		dsrObjectiveController = new DsrObjectiveController()
		
		when:
		dsrObjectiveController.params.code = CODE(1)
		dsrObjectiveController.saveWithoutTokenCheck()
		
		then:
		DsrObjective.count() == 1
	}
	
}

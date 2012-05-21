package org.chai.kevin.planning

class PlanningControllerSpec extends PlanningIntegrationTests {

	def planningController
	
	def "planning list"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		planningController = new PlanningController()
		
		when:
		planningController.list()
		
		then:
		planningController.modelAndView.model.entities.equals([planning])
	}
	
	def "create planning works ok"() {
		setup:
		def period = newPeriod()
		planningController = new PlanningController()
		
		when:
		planningController.params['period.id'] = period.id
		planningController.saveWithoutTokenCheck()

		then:
		Planning.count() == 1
		Planning.list()[0].period.equals(period)
	}
	
	def "create planning with active flag resets active flag on other planning"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [], true)
		planningController = new PlanningController()

		when:
		planningController.params['period.id'] = period.id
		planningController.params.active = true
		planningController.saveWithoutTokenCheck()
		
		then:
		Planning.count() == 2
		Planning.list()[1].active == true
		Planning.list()[0].active == false
		
	}
	
	def "create planning with active flag does not reset active flag on other planning if planning incomplete"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [], true)
		planningController = new PlanningController()

		when:
		planningController.params.active = true
		planningController.saveWithoutTokenCheck()
		
		then:
		Planning.count() == 1
		Planning.list()[0].active == true
	}
	
}

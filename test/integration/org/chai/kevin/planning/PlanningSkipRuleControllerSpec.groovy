package org.chai.kevin.planning

class PlanningSkipRuleControllerSpec extends PlanningIntegrationTests {

	def planningSkipRuleController
	
	def "list works"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def planningSkipRule = newPlanningSkipRule(planning, "true")
		planningSkipRuleController = new PlanningSkipRuleController()
		
		when:
		planningSkipRuleController.params['planning.id'] = planning.id
		planningSkipRuleController.list()
		
		then:
		planningSkipRuleController.modelAndView.model.entities.equals([planningSkipRule])
	}
	
	def "list with no planning returns 404"() {
		setup:
		planningSkipRuleController = new PlanningSkipRuleController()
		
		when:
		planningSkipRuleController.list()
		
		then:
		planningSkipRuleController.modelAndView == null
	}
	
	def "create skip rule works"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		planningSkipRuleController = new PlanningSkipRuleController()
		
		when:
		planningSkipRuleController.params['planning.id'] = planning.id
		planningSkipRuleController.params.expression = "true"
		planningSkipRuleController.saveWithoutTokenCheck()
		
		then:
		PlanningSkipRule.count() == 1
	}
	
}

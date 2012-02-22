package org.chai.kevin.planning

import org.chai.kevin.data.Type;

class PlanningTypeControllerSpec extends PlanningIntegrationTests {

	def planningTypeController
	
	def "planning type list"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period)
		def planningType = newPlanningType(newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_NUMBER()]))), "[_].key", planning)
		planningTypeController = new PlanningTypeController()
		
		when:
		planningTypeController.params['planning'] = planning.id
		planningTypeController.list()
		
		then:
		planningTypeController.modelAndView.model.entities.equals([planningType])
	}
	
	def "planning type list with no planning"() {
		setup:
		def period = newPeriod()
		planningTypeController = new PlanningTypeController()
		
		when:
		planningTypeController.list()
		
		then:
		planningTypeController.modelAndView.model.entities.equals([])
	}
	
	def "create planning type works ok"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period)
		planningTypeController = new PlanningTypeController()
		
		when:
		planningTypeController.params['planning.id'] = planning
		planningTypeController.params['dataElement.id'] = dataElement
		planningTypeController.params['discriminator'] = '[_]'
		planningTypeController.saveWithoutTokenCheck()

		then:
		PlanningType.count() == 1
		PlanningType.list()[0].period.equals(period)
	}
	
}

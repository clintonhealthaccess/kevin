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
		planningTypeController.params['planning.id'] = planning.id
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
		planningTypeController.modelAndView == null
	}
	
	def "create planning type works ok"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period)
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_NUMBER()])))
		planningTypeController = new PlanningTypeController()
		
		when:
		planningTypeController.params['planning.id'] = planning.id
		planningTypeController.params['dataElement.id'] = dataElement.id
		planningTypeController.params['discriminator'] = '[_].key'
		planningTypeController.params['namesPlural'] = ['en': 'Activities']
		planningTypeController.saveWithoutTokenCheck()

		then:
		PlanningType.count() == 1
		PlanningType.list()[0].period.equals(period)
		PlanningType.list()[0].namesPlural.en.equals("Activities")
		
	}
	
}

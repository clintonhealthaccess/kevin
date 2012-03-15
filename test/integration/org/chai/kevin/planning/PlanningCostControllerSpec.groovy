package org.chai.kevin.planning

import org.chai.kevin.data.Type;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;

class PlanningCostControllerSpec extends PlanningIntegrationTests {

	def planningCostController
	
	def "planning cost list"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period)
		def planningType = newPlanningType(newFormElement(newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")])))), "[_].key", "[_].key", planning)
		def dataElement = newNormalizedDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_NUMBER()), e([:]))
		def planningCost = newPlanningCost(PlanningCostType.INCOMING, dataElement, "[_].key", "value", planningType)
		planningCostController = new PlanningCostController()
		
		when:
		planningCostController.params['planningType.id'] = planningType.id
		planningCostController.list()
		
		then:
		planningCostController.modelAndView.model.entities.equals([planningCost])
	}
	
	def "planning cost list with no planning type"() {
		setup:
		planningCostController = new PlanningCostController()
		
		when:
		planningCostController.list()
		
		then:
		planningCostController.modelAndView == null
	}
	
	def "create planning type works ok"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period)
		def planningType = newPlanningType(newFormElement(newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")])))), "[_].key", "[_].key", planning)
		def dataElement = newNormalizedDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_NUMBER()), e([:]))
		planningCostController = new PlanningCostController()
		
		when:
		planningCostController.params['planningType.id'] = planningType.id
		planningCostController.params['dataElement.id'] = dataElement.id
		planningCostController.params['discriminatorValues'] = 'value'
		planningCostController.params['type'] = 'INCOMING'
		planningCostController.params['section'] = '[_].key'
		planningCostController.saveWithoutTokenCheck()

		then:
		PlanningCost.count() == 1
		PlanningCost.list()[0].planningType.equals(planningType)
		PlanningCost.list()[0].discriminatorValueString.equals("value")
		PlanningCost.list()[0].dataElement.equals(dataElement)
	}
	
}

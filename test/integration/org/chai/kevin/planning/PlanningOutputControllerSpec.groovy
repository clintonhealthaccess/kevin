package org.chai.kevin.planning

import org.chai.kevin.data.Type;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;

class PlanningOutputControllerSpec extends PlanningIntegrationTests {

	def planningOutputController
	
	def "planning output list"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def planningOutput = newPlanningOutput(planning, newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")]))), "[_].key")
		planningOutputController = new PlanningOutputController()
		
		when:
		planningOutputController.params['planning.id'] = planning.id
		planningOutputController.list()
		
		then:
		planningOutputController.modelAndView.model.entities.equals([planningOutput])
	}
	
	def "planning output list with no planning type"() {
		setup:
		planningOutputController = new PlanningOutputController()
		
		when:
		planningOutputController.list()
		
		then:
		planningOutputController.modelAndView == null
	}
	
	def "create planning output works ok"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")])))
		planningOutputController = new PlanningOutputController()
		
		when:
		planningOutputController.params['planning.id'] = planning.id
		planningOutputController.params['dataElement.id'] = dataElement.id
		planningOutputController.params['fixedHeader'] = '[_].key'
		planningOutputController.params["names"] = ["en": "Name"]
		planningOutputController.params["helps"] = ["en": "Help"]
		planningOutputController.params["captions"] = ["en": "Caption"]
		planningOutputController.saveWithoutTokenCheck()

		then:
		PlanningOutput.count() == 1
		PlanningOutput.list()[0].planning.equals(planning)
		PlanningOutput.list()[0].dataElement.equals(dataElement)
		PlanningOutput.list()[0].names.en == "Name"
		PlanningOutput.list()[0].helps.en == "Help"
		PlanningOutput.list()[0].captions.en == "Caption"
	}
	
}

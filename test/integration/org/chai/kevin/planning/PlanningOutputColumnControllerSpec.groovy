package org.chai.kevin.planning

import org.chai.kevin.data.Type;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;

class PlanningOutputColumnControllerSpec extends PlanningIntegrationTests {

	def planningOutputColumnController
	
	def "planning output column list"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def planningOutput = newPlanningOutput(planning, newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")]))), "[_].key")
		def planningOutputColumn = newPlanningOutputColumn(planningOutput, '[_].key')
		planningOutputColumnController = new PlanningOutputColumnController()
		
		when:
		planningOutputColumnController.params['planningOutput.id'] = planningOutput.id
		planningOutputColumnController.list()
		
		then:
		planningOutputColumnController.modelAndView.model.entities.equals([planningOutputColumn])
	}
	
	def "planning output column list with no planning type"() {
		setup:
		planningOutputColumnController = new PlanningOutputColumnController()
		
		when:
		planningOutputColumnController.list()
		
		then:
		planningOutputColumnController.modelAndView == null
	}
	
	def "create output planning column output works ok"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def planningOutput = newPlanningOutput(planning, newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")]))), "[_].key")
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_NUMBER()), e([:]))
		planningOutputColumnController = new PlanningOutputColumnController()
		
		when:
		planningOutputColumnController.params['planningOutput.id'] = planningOutput.id
		planningOutputColumnController.params['prefix'] = '[_]'
		planningOutputColumnController.saveWithoutTokenCheck()

		then:
		PlanningOutputColumn.count() == 1
		PlanningOutputColumn.list()[0].planningOutput.equals(planningOutput)
		PlanningOutputColumn.list()[0].prefix.equals('[_]')
	}
	
}

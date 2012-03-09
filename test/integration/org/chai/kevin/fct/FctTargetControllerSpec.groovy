package org.chai.kevin.fct

class FctTargetControllerSpec extends FctIntegrationTests {

	def fctTargetController
	def locationService
	
	def "save target"() {
		setup:
		def program = newReportProgram(CODE(1))
		def sum = newSum("1", CODE(3))
		fctTargetController = new FctTargetController()
		fctTargetController.locationService = locationService
		
		when:
		fctTargetController.params.code = CODE(2)
		fctTargetController.params['program.id'] = program.id+""
		fctTargetController.params['sum.id'] = sum.id+""
		fctTargetController.params.typeCodes = [DISTRICT_HOSPITAL_GROUP]
		fctTargetController.saveWithoutTokenCheck()
		
		then:
		FctTarget.count() == 1
		FctTarget.list()[0].sum.equals(sum)
	}
	
	def "fct target list"() {
		setup:
		def program = newReportProgram(CODE(1))
		def sum = newSum("1", CODE(3))
		def target = newFctTarget(CODE(1), sum, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		fctTargetController = new FctTargetController()
		
		when:
		fctTargetController.list()
		
		then:
		fctTargetController.modelAndView.model.entities.equals([target])
		fctTargetController.modelAndView.model.entityCount == 1
		
	}
	
}

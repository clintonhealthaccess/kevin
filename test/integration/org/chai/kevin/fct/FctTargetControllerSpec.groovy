package org.chai.kevin.fct

class FctTargetControllerSpec extends FctIntegrationTests {

	def fctTargetController
	def locationService
	
	def "save target"() {
		setup:
		def program = newReportProgram(CODE(1))
		fctTargetController = new FctTargetController()
		fctTargetController.locationService = locationService
		
		when:
		fctTargetController.params.code = CODE(2)
		fctTargetController.params['program.id'] = program.id+""
		fctTargetController.params.typeCodes = [DISTRICT_HOSPITAL_GROUP]
		fctTargetController.saveWithoutTokenCheck()
		
		then:
		FctTarget.count() == 1
	}
	
	def "fct target list"() {
		setup:
		def program = newReportProgram(CODE(1))
		def target = newFctTarget(CODE(1), 1, program)
		fctTargetController = new FctTargetController()
		
		when:
		fctTargetController.list()
		
		then:
		fctTargetController.modelAndView.model.entities.equals([target])
		fctTargetController.modelAndView.model.entityCount == 1
		
	}
	
	
	def "delete target" () {
		setup:
		setupLocationTree()
		def program = newReportProgram(CODE(1))
		def target = newFctTarget(CODE(1), 1, program)
		fctTargetController = new FctTargetController()
		
		when:
		fctTargetController.params.id = target.id
		fctTargetController.delete()
		
		then:
		FctTarget.count() == 0
	}
	
}

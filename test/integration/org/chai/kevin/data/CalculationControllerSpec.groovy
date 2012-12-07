package org.chai.kevin.data

class CalculationControllerSpec {

	def calculationController
	
	def "search sum"() {
		setup:
		def sum = newSum('1', 'sum')
		calculationController = new CalculationController()
		
		when:
		calculationController.params.q = 'sum'
		calculationController.search()
		
		then:
		calculationController.modelAndView.model.entities == [sum]
		calculationController.modelAndView.model.entityCount == 1
	}
	
	def "search mode"() {
		setup:
		def mode = newMode('1', 'mode')
		calculationController = new CalculationController()
		
		when:
		calculationController.params.q = 'mode'
		calculationController.search()
		
		then:
		calculationController.modelAndView.model.entities == [mode]
		calculationController.modelAndView.model.entityCount == 1
	}
	
}

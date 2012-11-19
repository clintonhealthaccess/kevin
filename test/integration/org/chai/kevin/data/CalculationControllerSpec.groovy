package org.chai.kevin.data

class CalculationControllerSpec {

	def calculationController
	
	def "search"() {
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
	
}

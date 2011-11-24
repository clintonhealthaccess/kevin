package org.chai.kevin.data

class AverageControllerSpec {

	def averageController
	
	def "save works"() {
		setup:
		averageController = new AverageController()
		
		when:
		averageController.params.code = CODE(1)
		averageController.params.expression = "1"
		averageController.saveWithoutTokenCheck()
		
		then:
		Average.count() == 1
		Average.list()[0].code == CODE(1)
		Average.list()[0].expression == "1"
		
	}

	def "save validates"() {
		setup:
		averageController = new AverageController()
		
		when:
		averageController.params.code = CODE(1)
		averageController.saveWithoutTokenCheck()
		
		then:
		Average.count() == 0
	}
		
}

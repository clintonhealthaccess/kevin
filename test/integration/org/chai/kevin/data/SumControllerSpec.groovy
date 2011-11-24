package org.chai.kevin.data

class SumControllerSpec {

	def sumController
	
	def "save works"() {
		setup:
		sumController = new SumController()
		
		when:
		sumController.params.code = CODE(1)
		sumController.params.expression = "1"
		sumController.saveWithoutTokenCheck()
		
		then:
		Sum.count() == 1
		Sum.list()[0].code == CODE(1)
		Sum.list()[0].expression == "1"
		
	}

	def "save validates"() {
		setup:
		sumController = new SumController()
		
		when:
		sumController.params.code = CODE(1)
		sumController.saveWithoutTokenCheck()
		
		then:
		Sum.count() == 0
	}
		
}

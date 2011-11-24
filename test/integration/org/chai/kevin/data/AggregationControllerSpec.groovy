package org.chai.kevin.data

class AggregationControllerSpec {

	def aggregationController
	
	def "save works"() {
		setup:
		aggregationController = new AggregationController()
		
		when:
		aggregationController.params.code = CODE(1)
		aggregationController.params.expression = "1"
		aggregationController.saveWithoutTokenCheck()
		
		then:
		Aggregation.count() == 1
		Aggregation.list()[0].code == CODE(1)
		Aggregation.list()[0].expression == "1"
		
	}

	def "save validates"() {
		setup:
		aggregationController = new AggregationController()
		
		when:
		aggregationController.params.code = CODE(1)
		aggregationController.saveWithoutTokenCheck()
		
		then:
		Aggregation.count() == 0
	}
		
}

package org.chai.kevin.data

class AggregationController extends AbstractCalculationController {
	
	def getEntity(def id) {
		return Aggregation.get(id)
	}
	
	def createEntity() {
		def aggregation = new Aggregation();
		aggregation.type = new Type()
		return aggregation
	}
	
	def getLabel() {
		return 'aggregation.label'
	}
	
	def getEntityClass(){
		return Aggregation.class;
	}
}

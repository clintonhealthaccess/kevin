package org.chai.kevin.data

class AggregationController extends AbstractCalculationController {
	
	def getEntity(def id) {
		return Aggregation.get(id)
	}
	
	def createEntity() {
		return new Aggregation();
	}
	
	def getLabel() {
		return 'aggregation.label'
	}
	
	def getEntityClass(){
		return Aggregation.class;
	}
}

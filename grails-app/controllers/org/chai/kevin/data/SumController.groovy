package org.chai.kevin.data

class SumController extends AbstractCalculationController {
	
	def getEntity(def id) {
		return Sum.get(id)
	}
	
	def createEntity() {
		return new Sum();
	}
	
	def getLabel() {
		return 'sum.label'
	}
	
	def exportEntity(){
		return Sum.class;
	}
}

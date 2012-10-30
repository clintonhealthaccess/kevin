package org.chai.kevin.data

class SumController extends AbstractCalculationController {
	
	def getEntity(def id) {
		return Summ.get(id)
	}
	
	def createEntity() {
		return new Summ();
	}
	
	def getLabel() {
		return 'sum.label'
	}
	
	def getEntityClass(){
		return Summ.class;
	}
}

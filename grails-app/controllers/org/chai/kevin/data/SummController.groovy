package org.chai.kevin.data

class SummController extends AbstractCalculationController {
	
	def getEntity(def id) {
		return Summ.get(id)
	}
	
	def createEntity() {
		return new Summ();
	}
	
	def getLabel() {
		return 'summ.label'
	}
	
	def getEntityClass(){
		return Summ.class;
	}
}

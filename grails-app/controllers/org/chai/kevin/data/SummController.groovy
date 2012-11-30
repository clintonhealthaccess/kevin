package org.chai.kevin.data

class SummController extends AbstractCalculationController {
	
	def getEntity(def id) {
		return Summ.get(id)
	}
	
	def createEntity() {
		def summ = new Summ();
		summ.type = new Type()
		return summ
	}
	
	def getLabel() {
		return 'sum.label'
	}
	
	def getEntityClass(){
		return Summ.class;
	}
}

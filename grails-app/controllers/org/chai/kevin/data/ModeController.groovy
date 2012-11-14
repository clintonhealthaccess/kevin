package org.chai.kevin.data

class ModeController extends AbstractCalculationController {
	
	def getEntity(def id) {
		return Mode.get(id)
	}
	
	def createEntity() {
		return new Mode();
	}
	
	def getLabel() {
		return 'mode.label'
	}
	
	def getEntityClass(){
		return Mode.class;
	}
}

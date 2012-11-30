package org.chai.kevin.data

class ModeController extends AbstractCalculationController {
	
	def getEntity(def id) {
		return Mode.get(id)
	}
	
	def createEntity() {
		def mode = new Mode()
		mode.type = new Type()
		return mode
	}
	
	def getLabel() {
		return 'mode.label'
	}
	
	def getEntityClass(){
		return Mode.class;
	}
}

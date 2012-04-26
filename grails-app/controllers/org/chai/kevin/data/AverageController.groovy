package org.chai.kevin.data

class AverageController extends AbstractCalculationController {
	
	def getEntity(def id) {
		return Average.get(id)
	}
	
	def createEntity() {
		return new Average();
	}
	
	def getLabel() {
		return 'average.label'
	}
	
	def exportEntity(){
		return Average.class;
	}
	
}

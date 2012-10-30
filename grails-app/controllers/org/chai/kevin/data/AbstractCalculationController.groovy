package org.chai.kevin.data;

import org.chai.kevin.AbstractEntityController;

abstract class AbstractCalculationController extends AbstractEntityController {

	def dataService
	def valueService
	
	def getTemplate() {
		return "/entity/data/createCalculation"
	}
	
	def deleteEntity(def entity) {
		valueService.deleteValues(entity, null, null)
		dataService.delete(entity)
	}
	
	def getModel(def entity) {
		[calculation: entity]
	}
	
//	protected abstract def getEntityClass();

	def saveEntity(def entity) {
		if (entity.id != null) valueService.deleteValues(entity, null, null)
		
		entity.setTimestamp(new Date());
		entity.save(flush: true)
	}
	
	def bindParams(def entity) {
		entity.properties = params
	}
	
}

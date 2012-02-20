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

	def saveEntity(def entity) {
		if (entity.id != null) valueService.deleteValues(entity, null, null)
		
		entity.setTimestamp(new Date());
		entity.save()
	}
	
	def bindParams(def entity) {
		entity.properties = params
		
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = params.descriptions
	}
	
}

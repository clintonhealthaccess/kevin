package org.chai.kevin

import org.chai.kevin.DataElement;

class ConstantController extends AbstractEntityController {
	
	def getEntity(def id) {
		return Constant.get(id)
	}
	
	def createEntity() {
		return new Constant()
	}
	
	def getTemplate() {
		return "createConstant";
	}
	
	def getModel(def entity) {
		return [constant: entity]
	}

	def validateEntity(def entity) {
		return entity.validate()
	}
	
	def saveEntity(def entity) {
		entity.save()
	}
	
	def deleteEntity(def entity) {
		entity.delete()
	}
	
	def bindParams(def entity) {
		entity.properties = params
		
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = entity.descriptions
	}
	
	def list = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		[constants: Constant.list(params), constantCount: Constant.count()]
	}
	
}

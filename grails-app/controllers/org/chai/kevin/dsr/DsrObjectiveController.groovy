package org.chai.kevin.dsr

import org.chai.kevin.AbstractEntityController


class DsrObjectiveController extends AbstractEntityController{
	
	def organisationService
	
	def getEntity(def id) {
		return DsrObjective.get(id)
	}
	
	def createEntity() {
		return new DsrObjective()
	}
	
	def getTemplate() {
		return "createObjective"
	}
	
	def getModel(def entity) {
		[ target: entity, expressions: Expression.list() ]
	}
	
	def validateEntity(def entity) {
		return entity.validate()
	}
	
	def saveEntity(def entity) {
		entity.save();
	}
	
	def deleteEntity(def entity) {
		entity.delete();
	}
	
	def bindParams(def entity) {
		entity.properties = params
	}
	


}

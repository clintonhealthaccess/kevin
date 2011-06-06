package org.chai.kevin.dsr

import org.chai.kevin.AbstractEntityController;

class DsrTargetCategoryController extends AbstractEntityController {
	
	def organisationService
	
	def getEntity(def id) {
		return DsrTargetCategory.get(id);
	}
	
	def createEntity() {
		return new DsrTargetCategory();
	}
	
	def getTemplate() {
		return "/dsr/createTargetCategory"
	}
	
	def getModel(def entity) {
		[ category: entity ]
	}
	
	def validateEntity(def entity) {
		return entity.validate()
	}
	
	def saveEntity(def entity) {
		entity.save()
	}
		
	def deleteEntity(def entity) {
		//TODO delete target from category
		for (def target : entity.targets) {
			target.category = null
			target.save()
		}
		entity.delete();
	}
	
	def bindParams(def entity) {
		entity.properties = params
	
	
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = params.descriptions
	}

}

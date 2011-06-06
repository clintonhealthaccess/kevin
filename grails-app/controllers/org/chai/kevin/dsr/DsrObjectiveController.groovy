package org.chai.kevin.dsr
import org.apache.commons.lang.StringUtils;
import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.Expression;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;


class DsrObjectiveController extends AbstractEntityController{
	
	def organisationService
	
	def getEntity(def id) {
		return DsrObjective.get(id)
	}
	
	def createEntity() {
		return new DsrObjective()
	}
	
	def getTemplate() {
		return "/dsr/createObjective"
	}
	
	def getModel(def entity) {
		[ objective: entity ]
	}
	
	def validateEntity(def entity) {
		return entity.validate()
	}
	
	def saveEntity(def entity) {
		entity.save()
	}
		
	def deleteEntity(def entity) {
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

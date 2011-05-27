package org.chai.kevin.maps

import org.apache.commons.lang.StringUtils;
import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.Expression;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.chai.kevin.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

import com.sun.tools.javac.code.Type.ForAll;

class MapsTargetController extends AbstractEntityController {
	
	def organisationService
	
	def getEntity(def id) {
		return MapsTarget.get(id)
	}
	
	def createEntity() {
		return new MapsTarget()
	}
	
	def getTemplate() {
		return "createTarget"
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
		entity.delete()
	}
	
	def bindParams(def entity) {
		entity.properties = params
		
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = entity.descriptions
	}
	
	
}

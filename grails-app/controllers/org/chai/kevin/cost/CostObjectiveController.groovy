package org.chai.kevin.cost

import org.apache.commons.lang.StringUtils;
import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.Expression;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.chai.kevin.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

import com.sun.tools.javac.code.Type.ForAll;

class CostObjectiveController extends AbstractEntityController {
	
	def organisationService
	
	def getEntity(def id) {
		return CostObjective.get(id)
	}
	
	def createEntity() {
		return new CostObjective()
	}
	
	def getTemplate() {
		return "createObjective"
	}
	
	def getModel(def entity) {
		[ objective: entity ]
	}
	
	def validate(def entity) {
		return entity.validate()
	}
	
	def save(def entity) {
		entity.save();
	}
	
	def bindParams(def entity) {
		entity.properties = params
	}
	
	
}

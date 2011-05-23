package org.chai.kevin.dashboard

import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.Expression;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.chai.kevin.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

import com.sun.tools.javac.code.Type.ForAll;

class DashboardTargetController extends AbstractObjectiveController {

	def createEntity() {
		def entity = new DashboardObjectiveEntry()
		entity.entry = new DashboardTarget()
		return entity
	}
	
	def getTemplate() {
		return 'createTarget'
	}
	
	def bindParams(def objectiveEntry) {
		// GRAILS-6388 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6388
		objectiveEntry.entry.calculations.each() { key, value ->
			value.expression = params['objective.calculations['+key+'].expression.id'] != 'null'?new Expression():null
		}
		objectiveEntry.properties = params;
	}
	
}

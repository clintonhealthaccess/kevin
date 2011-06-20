package org.chai.kevin.dashboard

import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.Expression;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjective;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.chai.kevin.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

import com.sun.tools.javac.code.Type.ForAll;

class DashboardObjectiveController extends AbstractObjectiveController {

	def createEntity() {
		def entity = new DashboardObjectiveEntry()
		entity.entry = new DashboardObjective()
		return entity
	}
	
	def getTemplate() {
		return '/dashboard/dashboardObjective/createObjective';
	}
	
	def bindParams(def objectiveEntry) {
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.entry?.names!=null) objectiveEntry.entry.names = params.entry?.names
		if (params.entry?.descriptions!=null) objectiveEntry.entry.descriptions = params.entry?.descriptions
		
		objectiveEntry.properties = params;
	}
	
}

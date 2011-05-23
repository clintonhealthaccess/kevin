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
		return 'createObjective';
	}
	
	def bindParams(def entity) {
		entity.properties = params;
	}
	
}

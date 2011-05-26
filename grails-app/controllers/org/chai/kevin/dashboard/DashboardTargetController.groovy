package org.chai.kevin.dashboard

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import org.apache.catalina.util.RequestUtil;
import org.apache.jasper.compiler.Node.ParamsAction;
import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.Expression;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.JSONUtils;
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
		log.error(params);
		
		// FIXME GRAILS-6388 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6388
		objectiveEntry.entry.calculations.each() { key, value ->
			log.error('entry.calculations['+key+'].expression.id');
			log.error(params['entry.calculations['+key+'].expression.id'])
			value.expression = params['entry.calculations['+key+'].expression.id'] != 'null'?new Expression():null
		}
		
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.entry?.names!=null) objectiveEntry.entry.names = params.entry?.names
		if (params.entry?.descriptions!=null) objectiveEntry.entry.descriptions = params.entry?.descriptions
		
		objectiveEntry.properties = params;
	}
		
}

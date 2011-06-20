package org.chai.kevin.dashboard

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import org.apache.catalina.util.RequestUtil;
import org.apache.jasper.compiler.Node.ParamsAction;
import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.Calculation;
import org.chai.kevin.Expression;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

import com.sun.tools.javac.code.Type.ForAll;

class DashboardTargetController extends AbstractObjectiveController {

	def createEntity() {
		def entity = new DashboardObjectiveEntry()
		entity.entry = new DashboardTarget()
		entity.entry.calculation = new Calculation()
		return entity
	}
	
	def getTemplate() {
		return '/dashboard/dashboardTarget/createTarget'
	}
	
	def saveEntity(def entity) {
		super.saveEntity(entity)
		entity.entry.calculation.timestamp = new Date()
		entity.entry.calculation.save()
	}
	
	def bindParams(def objectiveEntry) {
		
		// FIXME GRAILS-6388 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6388
//		objectiveEntry.entry.calculation.expressions.each() { key, value ->
//			value.expression = params['entry.calculation.expressions['+key+'].expression.id'] != 'null'?new Expression():null
//		}
		
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.entry?.names!=null) objectiveEntry.entry.names = params.entry?.names
		if (params.entry?.descriptions!=null) objectiveEntry.entry.descriptions = params.entry?.descriptions
		
		objectiveEntry.properties = params;
		
	}
		
}

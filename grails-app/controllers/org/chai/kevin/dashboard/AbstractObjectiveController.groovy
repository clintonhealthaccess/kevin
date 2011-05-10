package org.chai.kevin.dashboard

import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.Expression;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjective;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

import com.sun.tools.javac.code.Type.ForAll;

abstract class AbstractObjectiveController extends AbstractEntityController {

	def organisationService
	
	def validate(def entity) {
		return entity.entry.validate()&entity.validate()
	}

	def getEntity(def id) {
		return DashboardObjectiveEntry.get(id);
	}
	
	def save(def entity) {
		if (entity.id == null) {
			def currentObjective = DashboardObjective.get(params['currentObjective']);
			currentObjective.addObjectiveEntry entity
			currentObjective.save();
		}
		else {
			entity.save();
		}
	}
	
	def getModel(def entity) {
		def currentObjective = null;
		if (params['currentObjective']) {
			currentObjective = DashboardObjective.get(params['currentObjective']);
			if (log.isInfoEnabled()) log.info('fetched current objective: '+currentObjective);
		}
		
		def groups = new GroupCollection(organisationService.getGroupsForExpression())
		return [objectiveEntry: entity, groups: groups, expressions: Expression.list(), currentObjective: currentObjective]
	}
}

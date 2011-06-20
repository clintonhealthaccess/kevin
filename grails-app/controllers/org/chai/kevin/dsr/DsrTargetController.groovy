package org.chai.kevin.dsr

import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.Expression;
import org.chai.kevin.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import com.sun.tools.javac.code.Type.ForAll;

class DsrTargetController extends AbstractEntityController {
	
	def organisationService
	
	def getEntity(def id) {
		return DsrTarget.get(id)
	}
	
	def createEntity() {
		return new DsrTarget()
	}
	
	def getTemplate() {
		return "createTarget"
	}
	
	def getModel(def entity) {
		def objective = null;
		if (params['objective']) {
			objective = DsrObjective.get(params['objective']);
			if (log.isInfoEnabled()) log.info('fetched current objective: '+objective);
		}
		def groups = new GroupCollection(organisationService.getGroupsForExpression())
		[
			target: entity,
			groupUuids: DsrService.getGroupUuids(entity.groupUuidString),
			objective: objective,
			expressions: Expression.list(),
		]
	}
	
	def validateEntity(def entity) {
		return entity.validate()
	}
	
	def saveEntity(def entity) {
		if (entity.id == null) {
			def objective = DsrObjective.get(params['objective']);
			objective.addTarget entity
			objective.save();
		}
		else {
			entity.save();
		}
	}
	
	def deleteEntity(def entity) {
		entity.delete();
	}
	
	def bindParams(def entity) {
		entity.properties = params
		entity.groupUuidString = DsrService.getGroupUuidString(params['groupUuids']);
	}
	

	
}

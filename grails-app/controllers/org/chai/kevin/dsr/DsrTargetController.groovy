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
		return "/dsr/createTarget"
	}

	def getModel(def entity) {
		def groups = new GroupCollection(organisationService.getGroupsForExpression())
		[
					target: entity,
					objectives: DsrObjective.list(),
					expressions: Expression.list(),
					categories: DsrTargetCategory.list()
				]
	}

	def validateEntity(def entity) {
		return entity.validate()
	}

	def saveEntity(def entity) {
		entity.save();
	}

	def deleteEntity(def entity) {
		if(entity.category != null){
			entity.category.targets.remove(entity)
			entity.category.save()
		}
		entity.objective.targets.remove(entity)
		entity.objective.save()
		entity.delete()
	}

	def bindParams(def entity) {
		entity.properties = params

		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = params.descriptions
	}
}

package org.chai.kevin.fct;
/**
 * @author Jean Kahigiso M.
 *
 */

import grails.plugin.springcache.annotations.CacheFlush;
import org.chai.kevin.AbstractEntityController

class FctTargetOptionController extends AbstractEntityController {

	def getEntity(def id) {
		return FctTargetOption.get(id)
	}

	def createEntity() {
		return new FctTargetOption()
	}

	def getLabel() {
		return "fct.targetoption.label"
	}
	
	def getTemplate() {
		return "/entity/fct/createTargetOption"
	}

	def getModel(def entity) {
		[
			targetOption: entity,
			targets: FctTarget.list(),
			sums: entity.sum!=null?[entity.sum]:[]
		]
	}

	def getEntityClass(){
		return FctTargetOption.class;
	}
	
//	def validateEntity(def entity) {
//		return entity.validate()
//	}

	def saveEntity(def entity) {
		entity.save();
	}

	def deleteEntity(def entity) {	
		entity.target.targetOptions.remove(entity)
		entity.target.save()
		entity.delete()
	}

	def bindParams(def entity) {
		entity.properties = params

		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = params.descriptions
		
		return[entity:entity]
	}
	
	@CacheFlush("fctCache")
	def save = {
		super.save()
	}
	
	@CacheFlush("fctCache")
	def delete = {
		super.delete()
	}
	
	@CacheFlush("fctCache")
	def edit = {
		super.edit()
	}
	
	def list = {
		adaptParamsForList()				
		List<FctTargetOption> targetOptions = FctTargetOption.list(params);
		
		render (view: '/entity/list', model:[
			entities: targetOptions,
			template: "fct/targetOptionList",
			code: getLabel(),
			entityCount: FctTargetOption.count(),
			entityClass: getEntityClass()
		])
	}
	
}

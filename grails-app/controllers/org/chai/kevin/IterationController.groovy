package org.chai.kevin

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.hisp.dhis.period.Period

class IterationController extends AbstractEntityController  {

	def periodService

	def getLabel() {
		return 'iteration.label'
	}
	
	def getEntity(def id) {
		return Period.get(id)
	}

	def createEntity() {
		def entity = new Period()
		entity.setStartDate(new Date())
		entity.setEndDate(new Date())
		entity.setPeriodType(periodService.getDefaultPeriodType())
		return entity;
	}

	def getTemplate() {
		return "/entity/iteration/createIteration";
	}

	def getModel(def entity) {
		return [iteration: entity]
	}

	def bindParams(def entity) {
		entity.properties = params
		// TODO FIXME assumption : one and only one period type in database
		if (entity.periodType==null) entity.periodType = periodService.getDefaultPeriodType()
	}

	def list = {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		def iterations=Period.list(params)
		Collections.sort(iterations,new PeriodSorter())
		
		render(view:'/entity/list', model: [
			entities: iterations, 
			entityCount: Period.count(),
			code: getLabel(),
			template: 'iteration/iterationList'
		])
	}
}

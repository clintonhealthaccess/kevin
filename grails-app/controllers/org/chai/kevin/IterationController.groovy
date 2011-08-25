package org.chai.kevin

import java.text.DateFormat
import java.text.SimpleDateFormat
import org.hisp.dhis.period.Period
import org.hisp.dhis.period.PeriodType;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.chai.kevin.PeriodSorter;

class IterationController extends AbstractEntityController  {

	def periodService

	def getEntity(def id) {
		return Period.get(id)
	}

	def createEntity() {
		return new Period()
	}

	def getTemplate() {
		return "/entity/iteration/createIteration";
	}

	def getModel(def entity) {
		return [iteration: entity]
	}

	def validateEntity(def entity) {
		return entity.validate()
	}

	def saveEntity(def entity) {
		entity.save()
	}

	def deleteEntity(def entity) {
		entity.delete()
	}

	def bindParams(def entity) {
		entity.properties = params
		// TODO FIXME assumption : one and only one period type in database
		if (entity.periodType==null) entity.periodType = periodService.getDefaultPeriodType()
	}

	def list = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		def iterations=Period.list(params)
		Collections.sort(iterations,new PeriodSorter())
		
		render(view:'/entity/list', model: [
			entities: iterations, 
			entityCount: Period.count(),
			code: 'iteration.label',
			template: 'iteration/iterationList'
		])
	}
}

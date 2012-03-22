package org.chai.kevin

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.hisp.dhis.period.Period

class PeriodController extends AbstractEntityController  {

	def periodService

	def getLabel() {
		return 'period.label'
	}
	
	def getEntity(def id) {
		return Period.get(id)
	}

	def createEntity() {
		def entity = new Period()
		entity.setStartDate(new Date())
		entity.setEndDate(new Date())
		return entity;
	}

	def getTemplate() {
		return "/entity/period/createPeriod";
	}

	def getModel(def entity) {
		return [period: entity]
	}

	def bindParams(def entity) {
		entity.properties = params
	}

	def list = {
		adaptParamsForList()
		
		def periods = Period.list(params)
		Collections.sort(periods,new PeriodSorter())
		
		render(view:'/entity/list', model: [
			entities: periods, 
			entityCount: Period.count(),
			code: getLabel(),
			template: 'period/periodList'
		])
	}
}

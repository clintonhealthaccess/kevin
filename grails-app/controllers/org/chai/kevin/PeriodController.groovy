package org.chai.kevin

import org.chai.kevin.exports.DataExport;
import org.chai.kevin.planning.Planning;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.value.ValueService;
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class PeriodController extends AbstractEntityController  {

	def periodService
	def valueService
	
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

	def getEntityClass(){
		return Period.class;
	}
	
	def saveEntity(def entity) {
		if (entity.defaultSelected) {
			// we reset all other planning
			Period.list().each {
				if (!it.equals(entity)) {
					it.defaultSelected = false
					it.save()
				}
			}
		}
		super.saveEntity(entity)
	}
	
	def bindParams(def entity) {
		entity.properties = params
	}
	
	def deleteEntity(def entity) {
		// we check if there are surveys / planning associated
		if (Survey.countByPeriod(entity) > 0 || Survey.countByLastPeriod(entity) > 0) {
			flash.message = message(code: 'period.delete.hassurvey', default: 'Cannot delete period, it still has associated surveys.')
		}
		else if (Planning.countByPeriod(entity) > 0) {
			flash.message = message(code: 'period.delete.hasplanning', default: 'Cannot delete period, it still has associated plannings.')
		}
		else if (valueService.getNumberOfValues(entity) > 0) {
			flash.message = message(code: "period.delete.hasvalues", default: "Could not delete period, it still has values");
		}
		else {
			def exports = DataExport.withCriteria{periods {eq('id', entity.id)}}.each {export ->
				export.removeFromPeriods(entity)
				export.save()
			}
			
			super.deleteEntity(entity)
		}
	}

	def list = {
		adaptParamsForList()
		
		def periods = Period.list(params)
		
		render(view:'/entity/list', model: [
			entities: periods, 
			entityCount: Period.count(),
			code: getLabel(),
			template: 'period/periodList',
			entityClass: getEntityClass()
		])
	}
}

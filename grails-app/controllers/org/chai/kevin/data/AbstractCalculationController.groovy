package org.chai.kevin.data;

import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.reports.AbstractReportTarget;

abstract class AbstractCalculationController extends AbstractEntityController {

	def dataService
	def valueService
	
	def getTemplate() {
		return "/entity/data/createCalculation"
	}
	
	def deleteEntity(def entity) {
		if (!AbstractReportTarget.findAllByData(entity).isEmpty()) {
			flash.message = message(code: "data.delete.hasreporttargets", default: "Could not delete element, some reports use this data element.")
		}
		else {
			valueService.deleteValues(entity, null, null)
			dataService.delete(entity)
		}
	}
	
	def getModel(def entity) {
		[calculation: entity]
	}
	
//	protected abstract def getEntityClass();

	def saveEntity(def entity) {
		if (entity.id != null) valueService.deleteValues(entity, null, null)
		
		entity.setTimestamp(new Date());
		entity.save(flush: true)
	}
	
	def bindParams(def entity) {
		entity.properties = params
	}
	
}

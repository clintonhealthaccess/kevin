package org.chai.kevin

import org.apache.commons.lang.math.NumberUtils;
import org.hisp.dhis.dataelement.Constant;
import org.chai.kevin.DataElement;

class ExpressionController extends AbstractEntityController {

	DataService dataService
	
	def getEntity(def id) {
		return Expression.get(id)
	}
	
	def createEntity() {
		return new Expression()
	}
	
	def getTemplate() {
		return "createExpression";
	}
	
	def getModel(def entity) {
		return [expression: entity, dataSets: dataService.getDataSets()]
	}

	def validate(def entity) {
		return entity.validate()
	}
	
	def save(def entity) {
		entity.save()
	}
	
	def bindParams(def entity) {
		entity.properties = params
	}
	
	def list = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		[expressions: Expression.list(params), expressionCount: Expression.count()]
	}

	def getConstantDescription = {
		def constant = null;
		if (NumberUtils.isNumber(params['constant'])) {
			constant = Constant.get(params['constant'])
		}
		
		if (constant == null) {
			render(contentType:"text/json") {
				result = 'error'
			}
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: 'constantDescription', model: [constant: constant])
			}
		}
	}
	
	def getData = {
		def dataSet = null
		if (NumberUtils.isNumber(params['dataSetFilter'])) {
			dataSet = DataSet.get(params['dataSetFilter'])
		}
		if (params['type'] == 'constant') {
			def constants = dataService.searchConstants(params['searchText']);
			render(contentType:"text/json") {
				result = 'success'
				html = g.render(template:'constants', model:[constants: constants])
			}
		}
		else {
			def dataElements = dataService.searchDataElements(params['searchText'], dataSet);
			render(contentType:"text/json") {
				result = 'success'
				html = g.render(template:'dataElements', model:[dataElements: dataElements])
			}
		}
	}

	def getDataElementDescription = {
		def dataElement = null;
		if (NumberUtils.isNumber(params['dataElement'])) {
			dataElement = DataElement.get(params['dataElement'])
		}
		def enume = null;
		if (dataElement != null) enume = dataElement.getEnume()
		
		if (dataElement == null) {
			render(contentType:"text/json") {
				result = 'error'
			}
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: 'dataElementDescription', model: [dataElement: dataElement, enume: enume])
			}
		}
	}
	
}

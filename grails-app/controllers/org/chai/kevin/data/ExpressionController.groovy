package org.chai.kevin.data

import org.chai.kevin.Period;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;

class ExpressionController {

	def expressionService
	
	def test = {
		render (view: 'test')
	}
	
	def doTest = { ExpressionTestCommand cmd ->
		if (cmd.hasErrors()) {
			render (view: 'test', model: [cmd: cmd])
		}
		else {
			def periods = Period.list()
			def dataLocationTypes = DataLocationType.list()
			
			NormalizedDataElement dataElement = new NormalizedDataElement()
			dataElement.type = cmd.type
			
			def expressionMap = new ExpressionMap()
			for (def period : periods) {
				def typeMap = [:]
				for (def type : dataLocationTypes) {
					typeMap.put(type.code, cmd.expression)
				}
				expressionMap.put(period.id+'', typeMap)
			}
			dataElement.expressionMap = expressionMap
			
			def locations = DataLocation.list()
			def valueMap = [:]
			for (def period : periods) {
				def valueList = []
				for (def location : locations) {
					valueList.add(expressionService.calculateValue(dataElement, location, period))
				}
				valueMap.put(period, valueList)
			}
			
			render (view: '/entity/list', model:[
				periods: periods,
				entities: valueMap,
				template: "value/data"+dataElement.class.simpleName+"List",
				code: 'dataelementvalue.label',
				search: true
			])
		}
	}
	
}

class ExpressionTestCommand {
	String expression
	Type type
	
	static constraints = {
		expression (blank: false, expressionValid: true)
		type (blank: false, nullable: false, validator: {val, obj ->
			return val.isValid();
		})
	}
}

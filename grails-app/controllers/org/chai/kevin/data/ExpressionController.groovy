package org.chai.kevin.data

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.ListUtils;
import org.chai.kevin.Period;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;

class ExpressionController {

	def expressionService
	
	def test = {
		render (view: 'builder', model: [periods: Period.list()])
	}
	
	def doTest = { ExpressionTestCommand cmd ->
		if (cmd.hasErrors()) {
			render (view: 'builder', model: [cmd: cmd, periods: Period.list()])
		}
		else {
			def periods = cmd.periodIds.findAll {it!=null} collect {Period.get(it)}
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
	List<Long> periodIds
	
	static constraints = {
		expression (blank: false, expressionValid: true)
		type (blank: false, nullable: false, validator: {val, obj ->
			return val.isValid();
		})
		periodIds (blank: false)
	}
}

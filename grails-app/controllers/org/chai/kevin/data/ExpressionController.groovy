package org.chai.kevin.data

import java.util.Set;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.ListUtils;
import org.chai.kevin.Period;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.util.Utils;

class ExpressionController {

	def expressionService
	def sessionFactory
	def locationService
	
	def test = {
		render (view: 'builder', model: [periods: Period.list([cache: true]), types: DataLocationType.list()])
	}
	
	def doTest = { ExpressionTestCommand cmd ->
		if (cmd.hasErrors()) {
			def exception = null

			if (cmd.errors.hasFieldErrors("expression")) {
				try {
					expressionService.expressionIsValid(cmd.expression, DataElement.class)
				} catch (IllegalArgumentException e) {
					flash.message = message(code:'expression.invalid.exception', args:[e.getCause().getMessage()]);
				}
			}
			
			render (view: 'builder', model: [cmd: cmd, periods: Period.list([cache: true])])
		}
		else {
			def periods = cmd.periodIds.findAll {it!=null} collect {Period.get(it)}
			def dataLocationTypes = new HashSet( cmd.typeCodes.collect { DataLocationType.findByCode(it) } )
			def locations = locationService.getRootLocation().collectDataLocations(null, dataLocationTypes)
			
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
			
			def valueMap = [:]
			for (def period : periods) {
				def valueList = []
				for (def location : locations) {
					valueList.add(expressionService.calculateValue(dataElement, location, period))
					sessionFactory.currentSession.clear()
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
	String typeCodeString
	
	Set<String> getTypeCodes() {
		return Utils.split(typeCodeString);
	}
	void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = Utils.unsplit(typeCodes);
	}
	
	static constraints = {
		expression (blank: false, expressionValid: true)
		type (blank: false, nullable: false, validator: {val, obj ->
			return val.isValid();
		})
		periodIds (blank: false)
	}
}

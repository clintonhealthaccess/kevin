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
			
			render (view: 'builder', model: [cmd: cmd, periods: Period.list([cache: true]), types: DataLocationType.list()])
		}
		else {
			def period = Period.get(cmd.periodId)
			def dataLocationTypes = new HashSet( cmd.typeCodes.collect { DataLocationType.findByCode(it) } )
			def locations = locationService.getRootLocation().collectDataLocations(null, dataLocationTypes)
			
			NormalizedDataElement dataElement = new NormalizedDataElement()
			dataElement.type = cmd.type
			
			def expressionMap = new ExpressionMap()
			def typeMap = [:]
			for (def type : dataLocationTypes) {
				typeMap.put(type.code, cmd.expression)
			}
			expressionMap.put(period.id+'', typeMap)
			dataElement.expressionMap = expressionMap
			
			def values = []
			int i = 0;
			for (def location : locations) {
				values.add(expressionService.calculateValue(dataElement, location, period))
				if (i++ == 20) {
					if (log.debugEnabled) log.debug('clearing session')
					sessionFactory.currentSession.clear();
					i = 0;
				}
			}
			
			render (view: '/entity/list', model:[
				periods: [period],
				selectedPeriod: period,
				entities: values,
				entityCount: values.size(),
				template: "value/data"+dataElement.class.simpleName+"List",
				code: 'datavalue.label',
				search: true
			])
		}
	}
	
}

class ExpressionTestCommand {
	String expression
	Type type
	Long periodId
	String typeCodeString
	
	Set<String> getTypeCodes() {
		return Utils.split(typeCodeString, DataLocationType.DEFAULT_CODE_DELIMITER);
	}
	void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = Utils.unsplit(typeCodes, DataLocationType.DEFAULT_CODE_DELIMITER);
	}
	
	static constraints = {
		expression (blank: false, expressionValid: true)
		type (blank: false, nullable: false, validator: {val, obj ->
			return val.isValid();
		})
		periodId (blank: false)
	}
}

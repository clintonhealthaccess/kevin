package org.chai.kevin

import org.chai.kevin.ExpressionService;

import grails.plugin.spock.UnitSpec;

class ExpressionServiceUnitSpec extends UnitSpec {

	ExpressionService expressionService;
	
	def setup() {
		expressionService = new ExpressionService();
	}
	
	def "test value"() {
		
		expect:
		newExpression == expressionService.convertStringExpression(expression, map)
				
		where:
		expression	| map					| newExpression
		"1"			| new HashMap()			| "1"
		"[1]"		| [(new Long(1)): "test"]	| "test"
	}
	
	def "test bad value"() {
		// "[1.1]"		| [1: "test"]	| "test"
	}
	
}

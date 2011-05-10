package org.hisp.dhis

import org.hisp.dhis.expression.DefaultExpressionService;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionService;

import grails.plugin.spock.UnitSpec;

class ExpressionServiceSpec extends UnitSpec {

	ExpressionService expressionService;
	
	def setup() {
		expressionService = new DefaultExpressionService();
	}
	
	def "test value"() {
		
		expect:
		newExpression == expressionService.convertStringExpression(expression, map)
				
		where:
		expression	| map			| newExpression
		"1"			| new HashMap()	| "1"
		"[1]"		| [1: "test"]	| "test"
		"[1.1]"		| [1: "test"]	| "test"
	}
	
}

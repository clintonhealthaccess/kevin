package org.chai.kevin

import org.hisp.dhis.expression.ExpressionService;


class ExpressionValidConstraint {
	def expressionService;
	
	def validate = {
		propertyValue -> return expressionService.expressionIsValid(propertyValue).equals(ExpressionService.VALID)
	}
}

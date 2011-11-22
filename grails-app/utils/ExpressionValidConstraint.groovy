class ExpressionValidConstraint {

	def expressionService
	
	def validate = { val ->
		return expressionService.expressionIsValid(val)
	}
	
}

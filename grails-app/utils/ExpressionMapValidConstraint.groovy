class ExpressionMapValidConstraint {

	def expressionService
	
	def validate = { val ->
		boolean valid = true 
		val.values().each { groupMap ->
			groupMap.values().each { expression ->
				valid = valid && expressionService.expressionIsValid(expression)
			}
		}
		return valid
	}
	
}

import org.hisp.dhis.period.Period;

class ExpressionMapValidConstraint {

	def expressionService
	
	def validate = { val, obj, errors ->
		def invalidExpressions = []
		val.each { period, groupMap ->
			groupMap.each { group, expression ->
				if (!expressionService.expressionIsValid(expression)) {
					invalidExpressions << expression
					errors.rejectValue('expressionMap', 'normalizeddataelement.expression.invalid', [expression, Period.get(period), group] as Object[], 'Expression {0} is invalid for period {1} and group {2}.')
				}
			}
		}
		
		return invalidExpressions.isEmpty()
	}
	
}

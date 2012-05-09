import org.chai.kevin.Period;
import org.chai.kevin.data.RawDataElement;

class ExpressionMapValidConstraint {

	def expressionService
	
	def validate = { val, obj, errors ->
		def invalidExpressions = []
		val.each { period, groupMap ->
			groupMap.each { group, expression ->
				def valid = false
				try {
					valid = expressionService.expressionIsValid(expression, RawDataElement.class)
				} catch (IllegalArgumentException e) {
					valid = false
				}
				if (expression.trim() != '' && !valid) {
					invalidExpressions << expression
					errors.rejectValue('expressionMap', 'normalizeddataelement.expression.invalid', [expression, Period.get(period), group] as Object[], 'Expression {0} is invalid for period {1} and group {2}.')
				}
			}
		}
		
		return invalidExpressions.isEmpty()
	}
	
}

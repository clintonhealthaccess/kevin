import org.chai.kevin.data.RawDataElement;
import org.hisp.dhis.period.Period;

class ExpressionMapValidConstraint {

	def expressionService
	
	def validate = { val, obj, errors ->
		def invalidExpressions = []
		val.each { period, groupMap ->
			groupMap.each { group, expression ->
				if (expression.trim() != '' && !expressionService.expressionIsValid(expression, RawDataElement.class)) {
					invalidExpressions << expression
					errors.rejectValue('expressionMap', 'normalizeddataelement.expression.invalid', [expression, Period.get(period), group] as Object[], 'Expression {0} is invalid for period {1} and group {2}.')
				}
			}
		}
		
		return invalidExpressions.isEmpty()
	}
	
}

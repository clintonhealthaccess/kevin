import org.chai.kevin.Period;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.RawDataElement;

class ExpressionMapValidConstraint {

	def expressionService
	
	def validate = { val, obj, errors ->
		def invalidExpressions = []
		val.each { period, groupMap ->
			groupMap.each { group, expression ->
				if (expression.trim() != '' && !expressionService.expressionIsValid(expression, DataElement.class)) {
					invalidExpressions << expression
					errors.rejectValue('expressionMap', 'normalizeddataelement.expression.invalid', [expression, Period.get(period), group] as Object[], 'Expression {0} is invalid for period {1} and group {2}.')
				}
			}
		}
		
		return invalidExpressions.isEmpty()
	}
	
}

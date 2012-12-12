import org.chai.kevin.Period;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.RawDataElement;

class ExpressionMapValidConstraint {

	def expressionService
	
	def validate = { val, obj, errors ->
		if (log.isDebugEnabled()) log.debug('validating expression map on object: '+obj+', with val: '+val+', and expressionMap: '+obj.expressionMap+', and expressionMapString: '+obj.expressionMapString)
		
		def invalidExpressions = []
		val.each { period, groupMap ->
			groupMap.each { group, expression ->
				def valid = false
				try {
					valid = expressionService.expressionIsValid(expression, DataElement.class)
				} catch (IllegalArgumentException e) {
					log.debug("caught exception parsing expression ${expression}", e)
					valid = false
				}
				if (expression.trim() != '' && !valid) {
					invalidExpressions << expression
					errors.rejectValue('expressionMap', 'normalizeddataelement.expression.invalid', [expression, Period.get(period), group] as Object[], 'Expression {0} is invalid for period {1} and group {2}.')
				}
			}
		}
		
		if (log.isDebugEnabled()) log.debug('invalid expressions found: '+invalidExpressions)
		return invalidExpressions.isEmpty()
	}
	
}

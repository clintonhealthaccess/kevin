import org.hisp.dhis.expression.ExpressionService;

class ExpressionValidConstraint {

	def expressionService
	
	def validate = { val -> 
		expressionService.expressionIsValid(val)
	}
	
}

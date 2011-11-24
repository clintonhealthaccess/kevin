import org.chai.kevin.data.DataElement;

class ExpressionValidConstraint {

	def expressionService
	
	def validate = { val ->
		return expressionService.expressionIsValid(val, DataElement.class)
	}
	
}

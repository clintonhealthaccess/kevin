import org.chai.kevin.data.DataElement;

class ExpressionValidConstraint {

	def expressionService
	
	def validate = { val ->
		 
		try {
			return expressionService.expressionIsValid(val, DataElement.class)
		} catch (IllegalArgumentException e) {
			return false;
		}
		
	}
	
}

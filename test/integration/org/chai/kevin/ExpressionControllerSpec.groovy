package org.chai.kevin;

import static org.junit.Assert.*;

import org.chai.kevin.data.DataElementController;
import org.chai.kevin.data.Expression;
import org.chai.kevin.data.ExpressionController;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.ExpressionValue;

class ExpressionControllerSpec extends IntegrationTests {

	def expressionController

	def "deleting expression deletes expression values"() {
		setup:
		expressionController = new ExpressionController()
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER, expression: "1")
		def organisation = newOrganisationUnit(BUTARO)
		def period = newPeriod()
		
		when:
		newExpressionValue(expression, period, organisation)
		expressionController.params.id = expression.id
		expressionController.delete()
		
		then:
		Expression.count() == 0
		ExpressionValue.count() == 0
	}
	
}

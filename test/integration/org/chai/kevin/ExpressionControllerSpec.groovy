package org.chai.kevin;

import org.chai.kevin.data.Expression;
import org.chai.kevin.data.ExpressionController;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.NormalizedDataElementValue;

class ExpressionControllerSpec extends IntegrationTests {

	def expressionController

	def "deleting expression deletes expression values"() {
		setup:
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "1")
		def organisation = newOrganisationUnit(BUTARO)
		def period = newPeriod()
		expressionController = new ExpressionController()
		
		when:
		newExpressionValue(expression, period, organisation)
		expressionController.params.id = expression.id
		expressionController.delete()
		
		then:
		Expression.count() == 0
		NormalizedDataElementValue.count() == 0
	}
	
	def "cannot delete expression if there are associated calculations"() {
		setup:
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "1")
		
		def calculation = newAverage([(DISTRICT_HOSPITAL_GROUP): expression], CODE(2), Type.TYPE_NUMBER())
		def organisation = newOrganisationUnit(BUTARO)
		def period = newPeriod()
		expressionController = new ExpressionController()
		
		when:
		expressionController.params.id = expression.id
		expressionController.delete()
		
		then:
		Expression.count() == 1
		
	}
	
	def "search expression"() {
		setup:
		def expression = newExpression(j(["en": "Expression"]), CODE(1), Type.TYPE_NUMBER(), "1")
		expressionController = new ExpressionController()
		
		when:
		expressionController.params.q = "expr"
		expressionController.search()
		
		then:
		expressionController.modelAndView.model.entities.size() == 1
		expressionController.modelAndView.model.entities[0].equals(expression)
		expressionController.modelAndView.model.entityCount == 1
	}
	
}

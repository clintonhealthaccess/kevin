package org.chai.kevin;

import org.chai.kevin.data.Type;
import org.chai.kevin.value.NormalizedDataElementValue;

class NormalizedDataElementControllerSpec extends IntegrationTests {

	def normalizedDataElementController

	def "deleting expression deletes expression values"() {
		setup:
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "1")
		def organisation = newOrganisationUnit(BUTARO)
		def period = newPeriod()
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		newExpressionValue(expression, period, organisation)
		normalizedDataElementController.params.id = expression.id
		normalizedDataElementController.delete()
		
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
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		normalizedDataElementController.params.id = expression.id
		normalizedDataElementController.delete()
		
		then:
		Expression.count() == 1
		
	}
	
	def "search expression"() {
		setup:
		def expression = newExpression(j(["en": "Expression"]), CODE(1), Type.TYPE_NUMBER(), "1")
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		normalizedDataElementController.params.q = "expr"
		normalizedDataElementController.search()
		
		then:
		normalizedDataElementController.modelAndView.model.entities.size() == 1
		normalizedDataElementController.modelAndView.model.entities[0].equals(expression)
		normalizedDataElementController.modelAndView.model.entityCount == 1
	}
	
}

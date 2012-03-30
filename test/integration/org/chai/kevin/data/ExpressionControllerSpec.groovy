package org.chai.kevin.data

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.value.NormalizedDataElementValue;

class ExpressionControllerSpec extends IntegrationTests {

	def expressionController
	
	def "test values do test"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		expressionController = new ExpressionController()
		
		when:
		ExpressionTestCommand cmd = new ExpressionTestCommand(type: new Type("{\"type\":\"number\"}"), expression: '123', periodIds: [period.id])
		expressionController.doTest(cmd)
		
		then:
		expressionController.modelAndView.model.periods.equals([period])
		expressionController.modelAndView.model.entities[period].size() == 2
		expressionController.modelAndView.model.entities[period][0].value.numberValue == 123d
	}
	
}

package org.chai.kevin

class ExpressionSpec extends GebTests {
	
	
	def "save new empty expression displays error"() {
		when:
			browser.to(ExpressionPage)
			addExpression()
			createExpression.save()
		
		then:
			browser.at(ExpressionPage)
			createExpression.entityFormContainer.displayed
			createExpression.hasError(createExpression.codeField)
	}
	
	def "add expression works"() {
		when:
			browser.to(ExpressionPage)
			addExpression()
			
		then:
			browser.at(ExpressionPage)
			createExpression.entityFormContainer.displayed
	}
	
	def "cancel new expression"() {
		when:
			browser.to(ExpressionPage)
			addExpression()
			createExpression.cancel()
		
		then:
			browser.at(ExpressionPage)
			!createExpression.entityFormContainer.displayed
	}
	
	
}

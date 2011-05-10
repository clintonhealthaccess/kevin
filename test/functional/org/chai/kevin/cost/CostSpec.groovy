package org.chai.kevin.cost

import org.chai.kevin.GebTests;
import org.chai.kevin.Initializer;

import grails.plugin.geb.GebSpec;

class CostSpec extends GebTests {

	def transactional = true
	
	def setupSpec() {
		Initializer.createUsers();
		Initializer.createDummyStructure();
		Initializer.createDataElementsAndExpressions();
		Initializer.createCost();
	}
	
	def "costing page works"() {
		when:
			browser.to(CostPage)
			
		then:
			browser.at(CostPage)
	}
	
	def "add target gets displayed"() {
		when:
			browser.to(CostPage)
			addTarget()
			
		then:
			browser.at(CostPage)
			createTarget.saveButton.present
			!createTarget.hasError(createTarget.nameField)
			createTarget.hasExpression("Constant 10")
	}
		
	def "add empty expression displays error"() {
		when:
			browser.to(CostPage)
			addTarget()
			createTarget.addExpression()
			createTarget.createExpression.save()
			
		then:
			browser.at(CostPage)
			createTarget.createExpression.hasError(createTarget.createExpression.nameField)
			createTarget.createExpression.hasError(createTarget.createExpression.expressionField)
	}
	
	def "cancel new expression and save empty target displays error"() {
		when:
			browser.to(CostPage)
			addTarget()
			createTarget.addExpression()
			createTarget.createExpression.cancel()
			createTarget.save()
			
		then:
			browser.at(CostPage)
			createTarget.hasError(createTarget.nameField)
			createTarget.hasError(createTarget.weightField)
	}
	
	def "cancel new expression and save target works"() {
		when:
			browser.to(CostPage)
			addTarget()
			createTarget.addExpression()
			createTarget.createExpression.cancel()
			createTarget.nameField.value("Test Target")
			createTarget.weightField.value("1")
			createTarget.orderField.value("10")
			createTarget.save()
			
		then:
			browser.at(CostPage)
			!createTarget.displayed
			getTarget("Test Target").unique().text().contains "Test Target"
	}
	
}

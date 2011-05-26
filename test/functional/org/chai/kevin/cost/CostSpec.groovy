package org.chai.kevin.cost

import org.chai.kevin.GebTests;
import org.chai.kevin.Initializer;

import grails.plugin.geb.GebSpec;

class CostSpec extends GebTests {

	def transactional = true
	
	def setupSpec() {
		Initializer.createDummyStructure();
		Initializer.createDataElementsAndExpressions();
		Initializer.createCost();
	}
	
	def "costing page works"() {
		when:
			browser.to(CostPage)
			
		then:
			browser.at(CostPage)
			
		when:
			pickObjective("Geographical Access")
			
		then:
			browser.at(CostPage)
			
		when:
			pickOrganisation("Burera")
			
		then:
			browser.at(CostPage)
	}
	
	def "cancel new expression and save target works"() {
		when:
			browser.to(CostPage)
			pickOrganisation("Burera")
			pickObjective("Geographical Access")
			addTarget()
			createTarget.addExpression()
			createTarget.createExpression.cancel()
			createTarget.nameField.value("Test Target")
			createTarget.orderField.value("10")
			createTarget.expressionFields.first().value("1")
			createTarget.save()
			
		then:
			browser.at(CostPage)
			costTable.displayed
			getTarget("Test Target").unique().text().contains "Test Target"
	}
	
	def "add target gets displayed"() {
		when:
			browser.to(CostPage)
			pickOrganisation("Burera")
			pickObjective("Geographical Access")
			addTarget()
			
		then:
			browser.at(CostPage)
			createTarget.saveButton.present
			!createTarget.hasError(createTarget.nameField)
			createTarget.hasExpression("Constant 10")
	}
	
	def "edit target gets displayed"() {
		when:
			browser.to(CostPage)
			pickOrganisation("Burera")
			pickObjective("Geographical Access")
			editTarget()
			
		then:
			browser.at(CostPage)
			createTarget.saveButton.present
			!createTarget.hasError(createTarget.nameField)
	}
	
	def "add empty target displays error"() {
		when:
			browser.to(CostPage)
			pickOrganisation("Burera")
			pickObjective("Geographical Access")
			addTarget()
			createTarget.save()
			
		then:
			browser.at(CostPage)
			!costTable.displayed
			createTarget.hasError(createTarget.nameField)
	}
	
	def "add targets displays it on page"() {
		when:
			browser.to(CostPage)
			pickOrganisation("Burera")
			pickObjective("Geographical Access")
			addTarget()
			createTarget.nameField.value("Test Target 2")
			createTarget.orderField.value("11")
			createTarget.expressionFields.first().value("1")
			createTarget.save()
			
		then:
			browser.at(CostPage)
			costTable.displayed
			getTarget("Test Target 2").unique().text().contains "Test Target 2"
	}
		
	def "add empty expression displays error"() {
		when:
			browser.to(CostPage)
			pickOrganisation("Burera")
			pickObjective("Geographical Access")
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
			pickOrganisation("Burera")
			pickObjective("Geographical Access")
			addTarget()
			createTarget.addExpression()
			createTarget.createExpression.cancel()
			createTarget.save()
			
		then:
			browser.at(CostPage)
			createTarget.hasError(createTarget.nameField)
	}

}

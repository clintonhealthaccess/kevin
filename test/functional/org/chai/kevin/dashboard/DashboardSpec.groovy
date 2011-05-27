package org.chai.kevin.dashboard

import org.chai.kevin.GebTests;
import org.chai.kevin.Initializer;

import grails.plugin.geb.GebSpec;
import geb.Browser;


class DashboardSpec extends GebTests {

	def setupSpec() {
		Initializer.createDummyStructure();
		Initializer.createDataElementsAndExpressions();
		Initializer.createDashboard();
	} 
	
    def "objective container is there"() {
        when:
			browser.to(DashboardPage)
        then:
        	browser.at(DashboardPage)
    }
	
	def "refresh"() {
		when:
			browser.to(DashboardPage)
			clickRefresh()
			
		then:
			browser.at(ProgressPage)
			while (!browser.at(DashboardPage)) {
				Thread.sleep 1000
			}
			browser.at(DashboardPage)
			hasValues()
	}
	
	def "explain non leaf target"() {
		when:
			browser.to(DashboardPage)
			clickOnFirstCell()
			
		then:
			browser.at(DashboardPage)
			explanation.present
			explanation.hasValues()
	}
		
	def "cancel new expression and save empty target displays error"() {
		when:
			browser.to(DashboardPage)
			addTarget()
			createTarget.addExpression()
			createTarget.createExpression.save()
			createTarget.createExpression.cancel()
			createTarget.save()
			
		then:
			browser.at(DashboardPage)
			createTarget.hasError(createTarget.codeField)
			createTarget.hasError(createTarget.weightField)
	}
	
	def "cancel new expression and save target works"() {
		when:
			browser.to(DashboardPage)
			addTarget()
			createTarget.addExpression()
			createTarget.createExpression.save()
			createTarget.createExpression.cancel()
			createTarget.codeField.value("TEST1")
			createTarget.nameField.value("Test Target")
			createTarget.weightField.value("1")
			createTarget.orderField.value("10")
			createTarget.save()
			
		then:
			browser.at(DashboardPage)
			dashboard.displayed
			dashboardHeader.displayed
			getTarget("Test Target").unique().text().contains "Test Target"
	}
	
	def "show data elements in expression creation"() {
		when:
			browser.to(DashboardPage)
			addTarget()
			createTarget.addExpression()
			createTarget.createExpression.searchDataElement()
			
		then:
			browser.at(DashboardPage)
			createTarget.createExpression.hasDataElements()
	}
	
	def "add target entry gets displayed"() {
		when:
			browser.to(DashboardPage)
			addTarget()
		then:
			browser.at(DashboardPage)
			createTarget.saveButton.present
			!createTarget.hasError(createTarget.codeField)
			createTarget.hasExpression("Constant 10")
	}
	
	def "add objective entry gets displayed"() {
		when:
			browser.to(DashboardPage)
			addObjective()
		then:
			browser.at(DashboardPage)
			createObjective.saveButton.present
			!createObjective.hasError(createObjective.codeField)
	}
	
	def "save empty target displays error message"() {
		when:
			browser.to(DashboardPage)
			addTarget()
			createTarget.save()
			
		then:
			browser.at(DashboardPage)
			createTarget.hasError(createTarget.codeField)
			createTarget.hasError(createTarget.weightField)
	}
	
	def "save target preserves indicator values"() {
		when:
			browser.to(DashboardPage)
			addTarget()
			createTarget.expressionFields.each { it.value("1") }
			createTarget.save()
			
		then:
			browser.at(DashboardPage)
			createTarget.hasError(createTarget.codeField)
			createTarget.hasError(createTarget.weightField)
			createTarget.expressionFields.each { it.value() == "1" }
	}
 
	def "save empty objective displays error message"() {
		when:
			browser.to(DashboardPage)
			addObjective()
			createObjective.save()
			
		then:
			browser.at(DashboardPage)
			createObjective.hasError(createObjective.codeField)
			createObjective.hasError(createObjective.weightField)
	}
	
	def "save new target displays it on page"() {
		when:
			browser.to(DashboardPage)
			addTarget()
			createTarget.nameField.value("Test Target")
			createTarget.codeField.value("TEST2")
			createTarget.weightField.value("1")
			createTarget.orderField.value("10")
			createTarget.save()
			
		then:
			browser.at(DashboardPage)
			dashboard.displayed
			dashboardHeader.displayed
			getTarget("Test Target").unique().text().contains "Test Target"
	}
 
	def "save new objective displays it on page"() {
		when:
			browser.to(DashboardPage)
			addObjective()
			createObjective.nameField.value("Test Objective")
			createObjective.codeField.value("OBJECTIVE1")
			createObjective.weightField.value("1")
			createObjective.orderField.value("5")
			createObjective.save()
			
		then:
			browser.at(DashboardPage)
			dashboard.displayed
			dashboardHeader.displayed
			getObjective("Test Objective").unique().text().contains "Test Objective"
	}
	
	def "add empty expression displays error"() {
		when:
			browser.to(DashboardPage)
			addTarget()
			createTarget.addExpression()
			createTarget.createExpression.save()
			
		then:
			browser.at(DashboardPage)
			createTarget.createExpression.hasError(createTarget.createExpression.codeField)
			createTarget.createExpression.hasError(createTarget.createExpression.expressionField)
	}
 
	def "add new expression display it in list"() {
		when:
			browser.to(DashboardPage)
			addTarget()
			createTarget.addExpression()
			createTarget.createExpression.nameField.value("Test Expression")
			createTarget.createExpression.codeField.value("EXPRESSION1")
			createTarget.createExpression.expressionField.value("1")
			createTarget.createExpression.save()
			
		then:
			browser.at(DashboardPage)
			!createTarget.createExpression.entityFormContainer.displayed
			createTarget.hasExpression("Test Expression")
//			addObjectiveContainer.displayed
			// TODO list contains expression + change select value
	}
	
	def "cancel new expression"() {
		when:
			browser.to(DashboardPage)
			addTarget()
			createTarget.addExpression()
			createTarget.createExpression.cancel()
			
		then:
			browser.at(DashboardPage)
			createTarget.saveButton.present
			!createTarget.hasError(createTarget.codeField)
			createTarget.hasExpression("Constant 10")
	}

	
	def "explain leaf target"() {
		
	}
	
	def "explain objective"() {
		
	}
	
}
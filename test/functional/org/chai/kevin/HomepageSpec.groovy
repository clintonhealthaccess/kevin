package org.chai.kevin

import org.chai.kevin.dashboard.DashboardPage;

import grails.plugin.geb.GebSpec;

class HomepageSpec extends GebTests {
	
	def transactional = true
	
	def setupSpec() {
		Initializer.createDummyStructure();
		Initializer.createDataElementsAndExpressions();
		Initializer.createDashboard();
		Initializer.createCost();
	}
	
	def "header links are present"() {
		when:
			browser.to(HomepagePage)
		then:
			browser.at(HomepagePage)
			header.present
			!header.hasLink("Non-existing Link")
			header.hasLink("Dashboard")
			header.hasLink("Costing")
			header.hasLink("Expressions")
			header.hasLink("Constants")
	}
	
	def "dashboard link works"() {
		when:
			browser.to(HomepagePage)
			header.click("Dashboard")
			
		then:
			browser.at(DashboardPage)
	}

	def "expression link works"() {
		when:
			browser.to(HomepagePage)
			header.click("Expressions")
			
		then:
			browser.at(ExpressionPage)
	}

}

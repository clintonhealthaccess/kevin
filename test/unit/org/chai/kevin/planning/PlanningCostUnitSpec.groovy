package org.chai.kevin.planning

import grails.plugin.spock.UnitSpec

import org.chai.kevin.LanguageService

class PlanningCostUnitSpec extends UnitSpec {

	def "get groups and display name"() {
		when:
		def planningCost = new PlanningCost(names_en: "Cost - Revenue")
		
		then:
		planningCost.getGroups() == ["Cost"]
		planningCost.getDisplayName() == "Revenue"
	}
	
	def "get groups and display on default langauge"() {
		when:
		def planningCost = new PlanningCost(names_en: "Cost - Revenue")
		
		then:
		planningCost.getGroups() == ["Cost"]
		planningCost.getDisplayName() == "Revenue"
	}
	
	def "get groups and display on empty name"() {
		when:
		def planningCost = new PlanningCost()
		
		then:
		planningCost.getGroups() == []
		planningCost.getDisplayName() == ""
	}
	
}

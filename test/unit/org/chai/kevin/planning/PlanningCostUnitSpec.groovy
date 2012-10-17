package org.chai.kevin.planning

import org.chai.kevin.LanguageService;
import org.chai.kevin.UnitTests;

import grails.plugin.spock.UnitSpec;

class PlanningCostUnitSpec extends UnitTests {

	def "get groups and display name"() {
		when:
		def languageService = new LanguageService()
		languageService.metaClass.getCurrentLanguage = {"en"}
		def planningCost = new PlanningCost(names: ["en": "Cost - Revenue"])
		
		then:
		planningCost.getGroups(languageService) == ["Cost"]
		planningCost.getDisplayName(languageService) == "Revenue"
	}
	
	def "get groups and display on default langauge"() {
		when:
		def languageService = new LanguageService()
		languageService.metaClass.getCurrentLanguage = {"fr"}
		languageService.metaClass.getFallbackLanguage = {"en"}
		def planningCost = new PlanningCost(names: ["en": "Cost - Revenue"])
		
		then:
		planningCost.getGroups(languageService) == ["Cost"]
		planningCost.getDisplayName(languageService) == "Revenue"
	}
	
	def "get groups and display on empty name"() {
		when:
		def languageService = new LanguageService()
		languageService.metaClass.getCurrentLanguage = {"fr"}
		languageService.metaClass.getFallbackLanguage = {"en"}
		def planningCost = new PlanningCost(names: [:])
		
		then:
		planningCost.getGroups(languageService) == []
		planningCost.getDisplayName(languageService) == ""
	}
	
}

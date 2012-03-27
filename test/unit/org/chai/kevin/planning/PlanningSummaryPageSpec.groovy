package org.chai.kevin.planning

import org.chai.kevin.location.DataLocation;

import grails.plugin.spock.UnitSpec;

class PlanningSummaryPageSpec extends UnitSpec {

	def "test sort by planning type"() {
		
		setup:
		def planningTypes = [
			new PlanningType(id: 1)
		]
		def dataLocations = [
			new DataLocation(code: "dataLocation1"),
			new DataLocation(code: "dataLocation2")
		]
		def summaries = [
			(planningTypes[0]): new PlanningTypeSummary(planningTypes[0], 
				[(dataLocations[0]):2,
				(dataLocations[1]):1]
			)	
		]
		def summaryPage = new PlanningSummaryPage(planningTypes, dataLocations, summaries)
		def originalDataLocations = new ArrayList(dataLocations)
		
		when:
		summaryPage.sort("1", "asc", null)
		
		then:
		summaryPage.dataLocations.equals( [originalDataLocations[1], originalDataLocations[0]] )
		
		when:
		summaryPage.sort("1", "desc", null)
		
		then:
		summaryPage.dataLocations.equals( [originalDataLocations[0], originalDataLocations[1]] )
		
	}
	
}

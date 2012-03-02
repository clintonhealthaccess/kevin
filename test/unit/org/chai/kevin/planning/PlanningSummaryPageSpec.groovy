package org.chai.kevin.planning

import org.chai.kevin.location.DataLocationEntity;

import grails.plugin.spock.UnitSpec;

class PlanningSummaryPageSpec extends UnitSpec {

	def "test sort by planning type"() {
		
		setup:
		def planningTypes = [
			new PlanningType(id: 1)
		]
		def dataEntities = [
			new DataLocationEntity(code: "entity1"),
			new DataLocationEntity(code: "entity2")
		]
		def summaries = [
			(planningTypes[0]): new PlanningTypeSummary(planningTypes[0], 
				[(dataEntities[0]):2,
				(dataEntities[1]):1]
			)	
		]
		def summaryPage = new PlanningSummaryPage(planningTypes, dataEntities, summaries)
		def originalDataEntities = new ArrayList(dataEntities)
		
		when:
		summaryPage.sort("1", "asc", null)
		
		then:
		summaryPage.dataEntities.equals( [originalDataEntities[1], originalDataEntities[0]] )
		
		when:
		summaryPage.sort("1", "desc", null)
		
		then:
		summaryPage.dataEntities.equals( [originalDataEntities[0], originalDataEntities[1]] )
		
	}
	
}

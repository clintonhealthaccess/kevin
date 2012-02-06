package org.chai.kevin.planning

import org.chai.kevin.IntegrationTests;

class PlanningIntegrationTests extends IntegrationTests {

	def newPlanningType(def dataElement, def discriminator, def sections) {
		return new PlanningType(
			sections: sections,
			dataElement: dataElement,
			discriminator: discriminator
		).save(failOnError: true)
	}
	
	def newPlanningCost(def type, def sum, def section, def discriminatorValue, def planningType) {
		def planningCost = new PlanningCost(
			type: type,
			sum: sum,
			section: section,
			discriminatorValue: discriminatorValue,
			planningType: planningType
		).save(failOnError: true)
		
		planningType.costs << planningCost
		planningType.save(failOnError: true)
		return planningCost
	}
	
}

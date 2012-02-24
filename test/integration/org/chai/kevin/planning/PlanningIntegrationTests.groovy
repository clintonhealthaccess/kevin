package org.chai.kevin.planning

import org.chai.kevin.IntegrationTests;
import org.hibernate.loader.custom.Return;

abstract class PlanningIntegrationTests extends IntegrationTests {

	def newPlanning(def period) {
		return newPlanning(period, false)
	}
	
	def newPlanning(def period, def active) {
		return new Planning(period: period, active: active).save(failOnError: true)
	}
	
	def newPlanningType(def dataElement, def discriminator, def fixedHeader, def planning) {
		def planningType = new PlanningType(
			dataElement: dataElement,
			discriminator: discriminator,
			fixedHeader: fixedHeader,
			planning: planning
		).save(failOnError: true)
		planning.planningTypes << planningType
		planning.save(failOnError: true)
		return planningType
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

package org.chai.kevin.planning

import org.chai.kevin.IntegrationTests;
import org.hibernate.loader.custom.Return;

abstract class PlanningIntegrationTests extends IntegrationTests {

	static def newPlanning(def period) {
		return newPlanning(period, false)
	}
	
	static def newPlanning(def period, def active) {
		return new Planning(period: period, active: active).save(failOnError: true)
	}
	
	static def newPlanningType(def formElement, def discriminator, def fixedHeader, def planning) {
		def planningType = new PlanningType(
			formElement: formElement,
			discriminator: discriminator,
			fixedHeader: fixedHeader,
			planning: planning
		).save(failOnError: true)
		planning.planningTypes << planningType
		planning.save(failOnError: true)
		return planningType
	}
	
	static def newPlanningCost(def type, def dataElement, def discriminatorValueString, def planningType) {
		def planningCost = new PlanningCost(
			type: type,
			dataElement: dataElement,
			discriminatorValueString: discriminatorValueString,
			planningType: planningType
		).save(failOnError: true)
		
		planningType.costs << planningCost
		planningType.save(failOnError: true)
		return planningCost
	}
	
	static def newPlanningSkipRule(def planning, def expression) {
		def skipRule = new PlanningSkipRule(planning: planning, expression: expression).save(failOnError: true)
		planning.addSkipRule(skipRule)
		planning.save(failOnError: true)
		return skipRule
	}
	
}

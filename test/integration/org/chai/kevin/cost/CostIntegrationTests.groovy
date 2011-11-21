package org.chai.kevin.cost;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.cost.CostTarget.CostType;
import org.chai.kevin.util.Utils;

abstract class CostIntegrationTests extends IntegrationTests {

	def newCostRampUp(def code, def years) {
		return newCostRampUp([:], code, years)
	}
	
	def newCostRampUp(def names, def code, def years) {
		return new CostRampUp(names: names, code: code, years: years).save(failOnError: true)
	}
	
	def newCostRampUpYear(def year, def value) {
		return new CostRampUpYear(year: year, value: value).save(failOnError: true)
	} 
	
	def newCostObjective(def code) {
		return newCostObjective([:], code)
	}
	
	def newCostObjective(def names, def code) {
		return new CostObjective(names: names, code: code).save(failOnError: true)
	}
	
	def newCostTarget(def code, def dataElement, def costRampUp, def type, def groups, def objective) {
		return newCostTarget([:], code, dataElement, null, costRampUp, type, groups, objective)
	}
	
	def newCostTarget(def code, def dataElement, def dataElementEnd, def costRampUp, def type, def groups, def objective) {
		return newCostTarget([:], code, dataElement, dataElementEnd, costRampUp, type, groups, objective)
	}
	
	def newCostTarget(def names, def code, def dataElement, def dataElementEnd, def costRampUp, def type, def groups, def objective) {
		def target = new CostTarget(names: names, code: code, dataElement: dataElement, dataElementEnd: dataElementEnd, costRampUp: costRampUp, costType: type, groupUuidString: Utils.unsplit(groups), parent: objective).save(failOnError: true)
		objective.targets << target
		objective.save(failOnError: true)
		return target
	}

	def CONSTANT_RAMP_UP() {
		return newCostRampUp("CONSTANT",
			[	1: newCostRampUpYear(1, 0.2),
				2: newCostRampUpYear(2, 0.2),
				3: newCostRampUpYear(3, 0.2),
				4: newCostRampUpYear(4, 0.2),
				5: newCostRampUpYear(5, 0.2),
			])
	}
}

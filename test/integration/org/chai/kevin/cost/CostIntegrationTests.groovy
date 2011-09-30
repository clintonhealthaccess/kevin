package org.chai.kevin.cost;

import org.chai.kevin.IntegrationTests;
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
	
	def newCostObejctive(def code) {
		return newCostObjective([:], code)
	}
	
	def newCostObjective(def names, def code) {
		return new CostObjective(names: names, code: code).save(failOnError: true)
	}
	
	def newCostTarget(def code, def expression, def costRampUp, def type, def groups, def objective) {
		return newCostTarget([:], code, expression, null, costRampUp, type, groups, objective)
	}
	
	def newCostTarget(def code, def expression, def expressionEnd, def costRampUp, def type, def groups, def objective) {
		return newCostTarget([:], code, expression, expressionEnd, costRampUp, tpe, groups, objective)
	}
	
	def newCostTarget(def names, def code, def expression, def expressionEnd, def costRampUp, def type, def groups, def objective) {
		def target = new CostTarget(names: names, code: code, expression: expression, expressionEnd: expressionEnd, costRampUp: costRampUp, costType: type, groupUuidString: Utils.unsplit(groups), parent: objective).save(failOnError: true)
		objective.targets << target
		objective.save(failOnError: true)
		return target
	}

	def CONSTANT_RAMP_UP() {
		return newCostRampUp("CONSTANT",
			[	newCostRampUpYear(1, 0.2),
				newCostRampUpYear(2, 0.2),
				newCostRampUpYear(3, 0.2),
				newCostRampUpYear(4, 0.2),
				newCostRampUpYear(5, 0.2),
			])
	}
}

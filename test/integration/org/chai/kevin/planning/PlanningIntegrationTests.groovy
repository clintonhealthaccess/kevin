package org.chai.kevin.planning

import org.chai.kevin.IntegrationTests;
import org.chai.location.DataLocationType;
import org.chai.kevin.util.Utils;
import org.hibernate.loader.custom.Return;

abstract class PlanningIntegrationTests extends IntegrationTests {

	static def newPlanning(def period) {
		return newPlanning(period, false)
	}
	
	static def newPlanning(def period, def types) {
		return newPlanning(period, types, false)
	}
	
	static def newPlanning(def period, def types, def active) {
		return new Planning(period: period, typeCodeString: Utils.unsplit(types, Utils.DEFAULT_TYPE_CODE_DELIMITER), active: active).save(failOnError: true)
	}
	
	static def newPlanningType(def names, def formElement, def fixedHeader, def planning, def maxNumber) {
		def planningType = new PlanningType(formElement: formElement, fixedHeader: fixedHeader, maxNumber: maxNumber, planning: planning).save(failOnError: true)
		setLocaleValueInMap(planningType, names, "Names")
		setLocaleValueInMap(planningType, names, "NamesPlural")
		planning.addToPlanningTypes(planningType)
		planning.save(failOnError: true)
		return planningType
	}
	
	static def newPlanningType(def formElement, def fixedHeader, def planning) {
		return newPlanningType(null, formElement, fixedHeader, planning, null)
	}
	
	static def newPlanningCost(def type, def dataElement, def planningType) {
		return newPlanningCost(null, type, dataElement, planningType)
	}
	
	static def newPlanningCost(def names, def type, def dataElement, def planningType) {
		def planningCost = new PlanningCost(type: type, dataElement: dataElement, planningType: planningType).save(failOnError: true)
		setLocaleValueInMap(planningCost, names, "Names")
		planningType.addToCosts(planningCost)
		planningType.save(failOnError: true)
		return planningCost
	}
	
	static def newPlanningSkipRule(def planning, def expression) {
		def skipRule = new PlanningSkipRule(planning: planning, expression: expression).save(failOnError: true)
		planning.addToSkipRules(skipRule)
		planning.save(failOnError: true)
		return skipRule
	}
	
	static def newPlanningOutput(def planning, def dataElement, def fixedHeader) {
		def planningOutput = new PlanningOutput(planning: planning, dataElement: dataElement, fixedHeader: fixedHeader).save(failOnError: true)
		planning.addToPlanningOutputs(planningOutput)
		planning.save(failOnError: true)
		return planningOutput
	}
	
	static def newPlanningOutputColumn(def planningOutput, def prefix, def order) {
		def planningOutputColumn = new PlanningOutputColumn(planningOutput: planningOutput, prefix: prefix, order: order).save(failOnError: true)
		planningOutput.addToColumns(planningOutputColumn)
		planningOutput.save(failOnError: true)
		return planningOutputColumn
	}
	
	static def newPlanningOutputColumn(def planningOutput, def prefix) {
		return newPlanningOutputColumn(planningOutput, prefix, null)
	}
	
}

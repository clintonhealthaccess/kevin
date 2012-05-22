package org.chai.kevin.planning

import grails.plugin.spock.UnitSpec;

import org.chai.kevin.data.Type;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;
import org.chai.kevin.value.NormalizedDataElementValue
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;

class PlanningEntryBudgetUnitSpec extends UnitSpec {
	
	def "get sum when no budget cost for planning cost"() {
		
		when:
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(""), "key1":Type.TYPE_NUMBER()]))
		def value = new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}]}")
		def planningCosts = [
			new PlanningCost(type: PlanningCostType.OUTGOING),
			new PlanningCost(type: PlanningCostType.INCOMING)
		]
		def planningType = new PlanningType(costs: planningCosts)
		def validatable = new ValidatableValue(value, type)
		def planningEntryBudget = new PlanningEntryBudget([:], null, planningType, validatable, 0, null)
		
		then:
		planningEntryBudget.getSum(PlanningCostType.OUTGOING) == 0d
		
	}
	
	
}

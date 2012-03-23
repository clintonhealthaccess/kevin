package org.chai.kevin.planning

import grails.plugin.spock.UnitSpec;

import org.chai.kevin.data.Type;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;
import org.chai.kevin.value.NormalizedDataElementValue
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;

class PlanningEntryBudgetUnitSpec extends UnitSpec {

	def "get group sections with null section"() {
		
		when:
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(""), "key1":Type.TYPE_NUMBER()]))
		def value = new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}]}")
		def planningCosts = [
			new PlanningCost(groupSection: null, type: PlanningCostType.OUTGOING, discriminatorValueString: 'value'),
			new PlanningCost(groupSection: "[_].key1", type: PlanningCostType.INCOMING, discriminatorValueString: 'value')
		]
		def planningType = new PlanningType(costs: planningCosts, discriminator: '[_].key0')
		def validatable = new ValidatableValue(value, type)
		def planningEntryBudget = new PlanningEntryBudget([:], null, planningType, validatable, 0, null)
		
		then:
		planningEntryBudget.getGroupSections(PlanningCostType.OUTGOING) == [null]
		planningEntryBudget.getGroupSections(PlanningCostType.INCOMING) == ['[_].key1']
		
	}
	
	def "get sum when no budget cost for planning cost"() {
		
		when:
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(""), "key1":Type.TYPE_NUMBER()]))
		def value = new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}]}")
		def planningCosts = [
			new PlanningCost(groupSection: null, type: PlanningCostType.OUTGOING, discriminatorValueString: 'value'),
			new PlanningCost(groupSection: "[_].key1", type: PlanningCostType.INCOMING, discriminatorValueString: 'value')
		]
		def planningType = new PlanningType(costs: planningCosts, discriminator: '[_].key0')
		def validatable = new ValidatableValue(value, type)
		def planningEntryBudget = new PlanningEntryBudget([:], null, planningType, validatable, 0, null)
		
		then:
		planningEntryBudget.getSum(PlanningCostType.OUTGOING) == 0d
		
	}
	
	def "get group total when no budget cost for planning cost"() {
		
		when:
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(""), "key1":Type.TYPE_NUMBER()]))
		def value = new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}]}")
		def planningCosts = [
			new PlanningCost(groupSection: null, type: PlanningCostType.OUTGOING, discriminatorValueString: 'value'),
			new PlanningCost(groupSection: "[_].key1", type: PlanningCostType.INCOMING, discriminatorValueString: 'value')
		]
		def planningType = new PlanningType(costs: planningCosts, discriminator: '[_].key0')
		def validatable = new ValidatableValue(value, type)
		def planningEntryBudget = new PlanningEntryBudget([:], null, planningType, validatable, 0, null)
		
		then:
		planningEntryBudget.getGroupTotal(PlanningCostType.OUTGOING, null) == 0d
		
	}
	
	def "get budget costs"() {
		
		setup:
		def planningCost1 = new PlanningCost(groupSection: null, type: PlanningCostType.OUTGOING, discriminatorValueString: 'value')
		def planningCost2 = new PlanningCost(groupSection: "[_].key1", type: PlanningCostType.INCOMING, discriminatorValueString: 'value')
		def planningCosts = [planningCost1, planningCost2]
		
		when:
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(""), "key1":Type.TYPE_NUMBER()]))
		def value = new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}]}")
		def planningType = new PlanningType(costs: planningCosts, discriminator: '[_].key0')
		def validatable = new ValidatableValue(value, type)
		def planningEntryBudget = new PlanningEntryBudget([:], null, planningType, validatable, 0, null)
		
		then:
		planningEntryBudget.getBudgetCosts(PlanningCostType.OUTGOING, null).equals([])
		planningEntryBudget.getBudgetCosts(PlanningCostType.INCOMING, null).equals([])
		
		when:
		planningEntryBudget = new PlanningEntryBudget([(planningCost1): new NormalizedDataElementValue(value: Value.VALUE_LIST([Value.VALUE_NUMBER(1)]))], null, planningType, validatable, 0, null) 
		
		then:
		!planningEntryBudget.getBudgetCosts(PlanningCostType.OUTGOING, null).isEmpty()
		!planningEntryBudget.getBudgetCosts(PlanningCostType.INCOMING, "[_].key1").isEmpty()
		planningEntryBudget.getBudgetCosts(PlanningCostType.INCOMING, null).isEmpty()
		
	}
	
}

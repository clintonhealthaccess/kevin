package org.chai.kevin.planning

import grails.plugin.spock.UnitSpec;

import org.chai.kevin.planning.PlanningCost.PlanningCostType;

class PlanningEntryBudgetUnitSpec extends UnitSpec {

	def "get group sections with null section"() {
		
		when:
		def planningCosts = [
			new PlanningCost(groupSection: null, type: PlanningCostType.OUTGOING),
			new PlanningCost(groupSection: "[_].key1", type: PlanningCostType.INCOMING)
		]
		def planningType = Mock(PlanningType)
		planningType.getCosts() >> planningCosts
		def planningEntryBudget = new PlanningEntryBudget(null, null, planningType, null, null, null)
		
		then:
		planningEntryBudget.getGroupSections(PlanningCostType.OUTGOING) == [null]
		planningEntryBudget.getGroupSections(PlanningCostType.INCOMING) == ['[_].key1']
		
	}
	
	def "get sum when no budget cost for planning cost"() {
		
		when:
		def planningCosts = [
			new PlanningCost(groupSection: null, type: PlanningCostType.OUTGOING),
			new PlanningCost(groupSection: "[_].key1", type: PlanningCostType.INCOMING)
		]
		def planningType = Mock(PlanningType)
		planningType.getCosts() >> planningCosts
		def planningEntryBudget = new PlanningEntryBudget(null, null, planningType, null, null, null)
		
		then:
		planningEntryBudget.getSum(PlanningCostType.OUTGOING) == 0d
		
	}
	
	def "get group total when no budget cost for planning cost"() {
		
		when:
		def planningCosts = [
			new PlanningCost(groupSection: null, type: PlanningCostType.OUTGOING),
			new PlanningCost(groupSection: "[_].key1", type: PlanningCostType.INCOMING)
		]
		def planningType = Mock(PlanningType)
		planningType.getCosts() >> planningCosts
		def planningEntryBudget = new PlanningEntryBudget(null, null, planningType, null, null, null)
		
		then:
		planningEntryBudget.getGroupTotal(PlanningCostType.OUTGOING, null) == 0d
		
	}
	
	def "get budget costs"() {
		
		setup:
		def planningCost1 = new PlanningCost(groupSection: null, type: PlanningCostType.OUTGOING)
		def planningCost2 = new PlanningCost(groupSection: "[_].key1", type: PlanningCostType.INCOMING)
		def planningCosts = [planningCost1, planningCost2]
		def planningType = Mock(PlanningType)
		planningType.getCosts() >> planningCosts
		
		when:
		def planningEntryBudget = new PlanningEntryBudget(null, null, planningType, null, null, null)
		
		then:
		planningEntryBudget.getBudgetCosts(PlanningCostType.OUTGOING, null).equals([])
		planningEntryBudget.getBudgetCosts(PlanningCostType.INCOMING, null).equals([])
		
		when:
		planningEntryBudget = new PlanningEntryBudget(planningEntry, [(planningCost1): new BudgetCost(planningEntry, planningCost1, null)])
		
		then:
		!planningEntryBudget.getBudgetCosts(PlanningCostType.OUTGOING, null).isEmpty()
		!planningEntryBudget.getBudgetCosts(PlanningCostType.INCOMING, "[_].key1").isEmpty()
		planningEntryBudget.getBudgetCosts(PlanningCostType.INCOMING, null).isEmpty()
		
	}
	
}

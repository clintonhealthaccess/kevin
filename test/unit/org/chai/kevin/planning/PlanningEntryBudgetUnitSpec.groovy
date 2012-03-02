package org.chai.kevin.planning

import grails.plugin.spock.UnitSpec;

import org.chai.kevin.planning.PlanningCost.PlanningCostType;
import org.chai.kevin.planning.budget.PlanningEntryBudget;

class PlanningEntryBudgetUnitSpec extends UnitSpec {

	def "get group sections with null section"() {
		
		when:
		def planningCosts = [
			new PlanningCost(groupSection: null, type: PlanningCostType.OUTGOING),
			new PlanningCost(groupSection: "[_].key1", type: PlanningCostType.INCOMING)
		]
		def planningEntry = Mock(PlanningEntry)
		planningEntry.getPlanningCosts() >> planningCosts
		def planningEntryBudget = new PlanningEntryBudget(planningEntry, null)
		
		then:
		planningEntryBudget.getGroupSections(PlanningCostType.OUTGOING) == [null]
		planningEntryBudget.getGroupSections(PlanningCostType.INCOMING) == ['[_].key1']
		
	}
	
}

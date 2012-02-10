package org.chai.kevin.planning.budget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.chai.kevin.Translation;
import org.chai.kevin.planning.PlanningCost;
import org.chai.kevin.planning.PlanningEntry;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;

public class PlanningEntryBudget {

	private PlanningEntry planningEntry;
	private List<BudgetCost> budgetCosts;

	public PlanningEntryBudget(PlanningEntry planningEntry, List<BudgetCost> budgetCosts) {
		this.planningEntry = planningEntry;
		this.budgetCosts = budgetCosts;
	}
	
//	public Translation getNames() {
//		return names;
//	}
	
	public PlanningEntry getPlanningLine() {
		return planningEntry;
	}
	
	public Double getOutgoing() {
		return getSum(PlanningCostType.OUTGOING);
	}
	
	public Double getIncoming() {
		return getSum(PlanningCostType.INCOMING);
	}
	
	protected Double getSum(PlanningCostType costType) {
		Double result = 0d;
		for (BudgetCost cost : budgetCosts) {
			if (cost.getPlanningCost().getType().equals(costType)) {
				result += cost.getValue();
			}
		}
		return result;
	}
	
	public Double getDifference() {
		return getIncoming() - getOutgoing();
	}
	
	public Set<String> getGroupSections(PlanningCostType type) {
		Set<String> result = new HashSet<String>();
		for (BudgetCost cost : budgetCosts) {
			result.add(cost.getPlanningCost().getGroupSection());
		}
		return result;
	}
	
	public List<BudgetCost> getBudgetCosts() {
		return budgetCosts;
	}
	
	public List<BudgetCost> getBudgetCosts(PlanningCostType type, String groupSection) {
		List<BudgetCost> result = new ArrayList<BudgetCost>();
		for (BudgetCost cost : budgetCosts) {
			if (cost.getPlanningCost().getType().equals(type) && cost.getPlanningCost().getGroupSection().equals(groupSection)) result.add(cost);
		}
		return result;
	}

}
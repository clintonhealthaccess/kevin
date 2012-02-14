package org.chai.kevin.planning.budget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.chai.kevin.Translation;
import org.chai.kevin.planning.PlanningCost;
import org.chai.kevin.planning.PlanningEntry;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;

public class PlanningEntryBudget {

	private PlanningEntry planningEntry;
	private Map<PlanningCost, BudgetCost> budgetCosts;

	public PlanningEntryBudget(PlanningEntry planningEntry, Map<PlanningCost, BudgetCost> budgetCosts) {
		this.planningEntry = planningEntry;
		this.budgetCosts = budgetCosts;
	}
	
//	public Translation getNames() {
//		return names;
//	}
	
	public PlanningEntry getPlanningEntry() {
		return planningEntry;
	}
	
	public Double getOutgoing() {
		return getSum(PlanningCostType.OUTGOING);
	}
	
	public Double getIncoming() {
		return getSum(PlanningCostType.INCOMING);
	}
	
	public Double getDifference() {
		return getIncoming() - getOutgoing();
	}

	protected Double getSum(PlanningCostType costType) {
		Double result = 0d;
		for (PlanningCost planningCost : planningEntry.getPlanningCosts()) {
			if (planningCost.getType().equals(costType)) {
				result += getBudgetCost(planningCost).getValue();
			}
		}
		return result;
	}
	
	public Double getGroupTotal(PlanningCostType type, String groupSection) {
		Double result = 0d;
		for (PlanningCost planningCost : planningEntry.getPlanningCosts()) {
			if (planningCost.getType() == type && 
				(	planningCost.getGroupSection() == groupSection 
					||
					planningCost.getGroupSection().equals(groupSection)
				)) {
				result += getBudgetCost(planningCost).getValue();
			}
		}
		return result;
	}
	
	public List<String> getGroupSections(PlanningCostType type) {
		List<String> result = new ArrayList<String>();
		for (PlanningCost planningCost : planningEntry.getPlanningCosts()) {
			if (planningCost.getType().equals(type) && !result.contains(planningCost.getGroupSection())) {
				result.add(planningCost.getGroupSection());
			}
		}
		return result;
	}
	
	public List<PlanningCost> getPlanningCosts(PlanningCostType type, String groupSection) {
		List<PlanningCost> result = new ArrayList<PlanningCost>();
		for (PlanningCost planningCost : planningEntry.getPlanningCosts()) {
			if (	planningCost.getType() == type 
					&& 
					(	planningCost.getGroupSection() == groupSection 
						||
						planningCost.getGroupSection().equals(groupSection)
					)
			) {
				result.add(planningCost);
			}
		}
		return result;
	}
	
	public BudgetCost getBudgetCost(PlanningCost planningCost) {
		return budgetCosts.get(planningCost);
	}

}
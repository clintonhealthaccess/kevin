package org.chai.kevin.planning.budget;

import java.util.List;

import org.chai.kevin.Translation;
import org.chai.kevin.planning.PlanningCost;
import org.chai.kevin.planning.PlanningList;
import org.chai.kevin.planning.PlanningType;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;

public class PlanningTypeBudget {

	private PlanningList planningList;
	private PlanningType planningType;
	private List<PlanningEntryBudget> planningEntries; 
	
	public PlanningTypeBudget(PlanningType planningType, PlanningList planningList, List<PlanningEntryBudget> planningLines) {
		this.planningType = planningType;
		this.planningEntries = planningLines;
		this.planningList = planningList;
	}

	public Double getIncoming() {
		return getSum(PlanningCostType.INCOMING);
	}
	
	public PlanningType getpPlanningType() {
		return planningType;
	}
	
	public PlanningList getPlanningList() {
		return planningList;
	}

	private Double getSum(PlanningCostType costType) {
		Double result = 0d;
		for (PlanningEntryBudget line : planningEntries) {
			result += line.getSum(costType);
		}
		return result;
	}
	
	public Double getOutgoing() {
		return getSum(PlanningCostType.OUTGOING);
	}
	
	public Double getDifference() {
		return getIncoming() - getOutgoing();
	}
	
	public List<PlanningEntryBudget> getBudgetPlanningEntries() {
		return planningEntries;
	}
	
}
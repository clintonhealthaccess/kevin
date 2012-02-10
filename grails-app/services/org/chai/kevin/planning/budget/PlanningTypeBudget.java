package org.chai.kevin.planning.budget;

import java.util.List;

import org.chai.kevin.Translation;
import org.chai.kevin.planning.PlanningCost;
import org.chai.kevin.planning.PlanningType;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;

public class PlanningTypeBudget {

	private PlanningType planningType;
	private List<PlanningEntryBudget> planningLines; 
	
	public PlanningTypeBudget(PlanningType planningType, List<PlanningEntryBudget> planningLines) {
		this.planningType = planningType;
		this.planningLines = planningLines;
	}

	public Translation getNames() {
		return planningType.getNames();
	}
	
	public Double getIncoming() {
		return getSum(PlanningCostType.INCOMING);
	}

	private Double getSum(PlanningCostType costType) {
		Double result = 0d;
		for (PlanningEntryBudget line : planningLines) {
			result += line.getSum(PlanningCostType.INCOMING);
		}
		return result;
	}
	
	public Double getOutgoing() {
		return getSum(PlanningCostType.OUTGOING);
	}
	
	public Double getDifference() {
		return getIncoming() - getOutgoing();
	}
	
	public List<PlanningEntryBudget> getBudgetPlanningLines() {
		return planningLines;
	}
	
}

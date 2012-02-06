package org.chai.kevin.planning;

import java.util.List;

import org.chai.kevin.Translation;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;

public class BudgetPlanningType {

	private PlanningType planningType;
	private List<BudgetPlanningLine> planningLines; 
	
	public BudgetPlanningType(PlanningType planningType, List<BudgetPlanningLine> planningLines) {
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
		for (BudgetPlanningLine line : planningLines) {
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
	
	public List<BudgetPlanningLine> getBudgetPlanningLines() {
		return planningLines;
	}
	
}

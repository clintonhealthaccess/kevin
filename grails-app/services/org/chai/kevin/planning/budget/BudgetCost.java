package org.chai.kevin.planning.budget;

import org.chai.kevin.planning.PlanningCost;
import org.chai.kevin.value.SumValue;

public class BudgetCost {

	private PlanningCost planningCost;
	private SumValue value;
	
	public BudgetCost(PlanningCost planningCost, SumValue value) {
		this.planningCost = planningCost;
		this.value = value;
	}

	public Double getValue() {
		return value.getValue().getNumberValue().doubleValue();
	}
	
	public PlanningCost getPlanningCost() {
		return planningCost;
	}
	
}

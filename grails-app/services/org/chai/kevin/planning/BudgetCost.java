package org.chai.kevin.planning;

import org.chai.kevin.SumValue;

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
	
	protected PlanningCost getPlanningCost() {
		return planningCost;
	}
	
}

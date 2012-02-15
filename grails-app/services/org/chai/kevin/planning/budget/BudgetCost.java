package org.chai.kevin.planning.budget;

import org.chai.kevin.planning.PlanningCost;
import org.chai.kevin.planning.PlanningEntry;
import org.chai.kevin.value.SumValue;

public class BudgetCost {

	private PlanningEntry planningEntry;
	private PlanningCost planningCost;
	private SumValue value;
	
	public BudgetCost(PlanningEntry planningEntry, PlanningCost planningCost, SumValue value) {
		this.planningEntry = planningEntry;
		this.planningCost = planningCost;
		this.value = value;
	}

	public Double getValue() {
		return value.getValue().getNumberValue().doubleValue();
	}

	public PlanningEntry getPlanningEntry() {
		return planningEntry;
	}
	
	public PlanningCost getPlanningCost() {
		return planningCost;
	}
	
}

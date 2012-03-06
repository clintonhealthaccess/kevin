package org.chai.kevin.planning.budget;

import org.chai.kevin.planning.PlanningCost;
import org.chai.kevin.planning.PlanningEntry;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.SumValue;
import org.chai.kevin.value.Value;

public class BudgetCost {
	
	private PlanningEntry planningEntry;
	private PlanningCost planningCost;
	private NormalizedDataElementValue value;
	
	public BudgetCost(PlanningEntry planningEntry, PlanningCost planningCost, NormalizedDataElementValue value) {
		this.planningEntry = planningEntry;
		this.planningCost = planningCost;
		this.value = value;
	}

	public Double getValue() {
		return value.getValue().getListValue().get(planningEntry.getLineNumber()).getNumberValue().doubleValue();
	}
	
	public PlanningEntry getPlanningEntry() {
		return planningEntry;
	}
	
	public PlanningCost getPlanningCost() {
		return planningCost;
	}
	
}

package org.chai.kevin.planning;

import org.chai.kevin.value.NormalizedDataElementValue;

public class BudgetCost {
	
	private PlanningEntry planningEntry;
	private PlanningCost planningCost;
	private NormalizedDataElementValue value;
	
	public BudgetCost(PlanningEntry planningEntry, PlanningCost planningCost, NormalizedDataElementValue value) {
		this.planningEntry = planningEntry;
		this.planningCost = planningCost;
		this.value = value;
	}

	public Boolean isHidden() {
		return planningCost.getHideIfZero() == null ? false: planningCost.getHideIfZero() && getValue() == 0d;
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

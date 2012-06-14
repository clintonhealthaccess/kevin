package org.chai.kevin.planning;

import org.chai.kevin.LanguageService;
import org.chai.kevin.value.NormalizedDataElementValue;
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

	public Boolean isHidden() {
		return planningCost.getHideIfZero() == null ? false: planningCost.getHideIfZero() && getValue().getNumberValue().doubleValue() == 0d;
	}
	
	public Value getValue() {
		return value.getValue().getListValue().get(planningEntry.getLineNumber());
	}
	
	public PlanningEntry getPlanningEntry() {
		return planningEntry;
	}
	
	public PlanningCost getPlanningCost() {
		return planningCost;
	}
	
}

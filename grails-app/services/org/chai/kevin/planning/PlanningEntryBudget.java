package org.chai.kevin.planning;

import java.util.HashMap;
import java.util.Map;

import org.chai.kevin.data.Enum;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.ValidatableValue;

public class PlanningEntryBudget extends PlanningEntry {

	private Map<PlanningCost, BudgetCost> budgetCosts;
	private Map<PlanningCost, NormalizedDataElementValue> budgetValues;
	
	public PlanningEntryBudget(Map<PlanningCost, NormalizedDataElementValue> budgetValues, DataLocation dataLocation, PlanningType type,
			ValidatableValue validatable, Integer lineNumber, Map<String, Enum> enums) {
		super(dataLocation, type, validatable, lineNumber, enums);
		this.budgetValues = budgetValues;
	}
	
	private Map<PlanningCost, BudgetCost> getBudgetCosts() {
		if (budgetCosts == null) {
			budgetCosts = new HashMap<PlanningCost, BudgetCost>();
			for (PlanningCost planningCost : type.getCosts()) {
				NormalizedDataElementValue value = budgetValues.get(planningCost);
				if (value != null && !value.getValue().isNull()) {
					if (!value.getValue().getListValue().get(getLineNumber()).isNull()) {
						budgetCosts.put(planningCost, new BudgetCost(this, planningCost, value));
					}
				}
			}
		}
		return budgetCosts;
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

	public Double getSum(PlanningCostType costType) {
		Double result = 0d;
		for (PlanningCost planningCost : type.getCosts()) {
			if (planningCost.getType().equals(costType)) {
				if (getBudgetCost(planningCost) != null) result += getBudgetCost(planningCost).getValue();
			}
		}
		return result;
	}
	
	protected BudgetCost getBudgetCost(PlanningCost planningCost) {
		return getBudgetCosts().get(planningCost);
	}

}
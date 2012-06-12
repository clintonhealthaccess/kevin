package org.chai.kevin.planning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chai.kevin.LanguageService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.ValidatableValue;

public class PlanningEntryBudget extends PlanningEntry {

	private LanguageService languageService;
	
	private Map<PlanningCost, BudgetCost> budgetCosts;
	private Map<PlanningCost, NormalizedDataElementValue> budgetValues;
	
	public PlanningEntryBudget(Map<PlanningCost, NormalizedDataElementValue> budgetValues, DataLocation dataLocation, PlanningType type,
			ValidatableValue validatable, Integer lineNumber, Map<String, Enum> enums, LanguageService languageService) {
		super(dataLocation, type, validatable, lineNumber, enums);
		this.budgetValues = budgetValues;
		this.languageService = languageService;
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

	public Double getOutgoing(List<PlanningCost> costs) {
		return getSum(PlanningCostType.OUTGOING, costs);
	}
	
	public Double getIncoming(List<PlanningCost> costs) {
		return getSum(PlanningCostType.INCOMING, costs);
	}

	public Double getDifference(List<PlanningCost> costs) {
		return getIncoming(costs) - getOutgoing(costs);
	}

	protected Double getSum(PlanningCostType costType, List<PlanningCost> costs) {
		Double result = 0d;
		for (PlanningCost planningCost : costs) {
			if (planningCost.getType().equals(costType)) {
				if (getBudgetCost(planningCost) != null) result += getBudgetCost(planningCost).getValue();
			}
		}
		return result;
	}
	
	protected BudgetCost getBudgetCost(PlanningCost planningCost) {
		return getBudgetCosts().get(planningCost);
	}
	
	public String getDisplayName(PlanningCost planningCost) {
		return planningCost.getDisplayName(languageService);
	}

}
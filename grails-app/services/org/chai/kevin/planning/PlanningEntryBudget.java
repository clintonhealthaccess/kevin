package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chai.kevin.data.Enum;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.planning.PlanningCost;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;
import org.chai.kevin.planning.PlanningType;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.ValidatableValue;

public class PlanningEntryBudget extends PlanningEntry {

	private Map<PlanningCost, BudgetCost> budgetCosts;
	private Map<PlanningCost, NormalizedDataElementValue> budgetValues;
	
	public PlanningEntryBudget(Map<PlanningCost, NormalizedDataElementValue> budgetValues, DataLocationEntity entity, PlanningType type,
			ValidatableValue validatable, Integer lineNumber, Map<String, Enum> enums) {
		super(entity, type, validatable, lineNumber, enums);
		this.budgetValues = budgetValues;
	}

	
	private Map<PlanningCost, BudgetCost> getBudgetCosts() {
		if (budgetCosts == null) {
			budgetCosts = new HashMap<PlanningCost, BudgetCost>();
			for (PlanningCost planningCost : getPlanningCosts()) {
				NormalizedDataElementValue value = budgetValues.get(planningCost);
				if (!value.getValue().isNull()) {
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
		for (PlanningCost planningCost : getPlanningCosts()) {
			if (planningCost.getType().equals(costType)) {
				if (getBudgetCost(planningCost) != null) result += getBudgetCost(planningCost).getValue();
			}
		}
		return result;
	}
	public Double getGroupTotal(PlanningCostType type, String groupSection) {
		Double result = 0d;
		for (PlanningCost planningCost : getPlanningCosts()) {
			if (planningCost.getType() == type && 
				(	planningCost.getGroupSection() == groupSection 
					||
					planningCost.getGroupSection().equals(groupSection)
				)) {
				if (getBudgetCost(planningCost) != null) result += getBudgetCost(planningCost).getValue();
			}
		}
		return result;
	}
	
	public List<String> getGroupSections(PlanningCostType type) {
		List<String> result = new ArrayList<String>();
		for (PlanningCost planningCost : getPlanningCosts()) {
			if (planningCost.getType().equals(type) && !result.contains(planningCost.getGroupSection())) {
				result.add(planningCost.getGroupSection());
			}
		}
		return result;
	}
	
	public List<BudgetCost> getBudgetCosts(PlanningCostType type, String groupSection) {
		List<BudgetCost> result = new ArrayList<BudgetCost>();
		for (PlanningCost planningCost : getPlanningCosts()) {
			if (	planningCost.getType() == type 
					&& 
					(	planningCost.getGroupSection() == groupSection 
						||
						planningCost.getGroupSection().equals(groupSection)
					)
			) {
				if (getBudgetCost(planningCost) != null) result.add(getBudgetCost(planningCost));
			}
		}
		return result;
	}
	
	protected BudgetCost getBudgetCost(PlanningCost planningCost) {
		return getBudgetCosts().get(planningCost);
	}

}
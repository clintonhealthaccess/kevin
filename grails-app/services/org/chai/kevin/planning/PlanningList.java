package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chai.kevin.data.Enum;
import org.chai.kevin.form.FormElement.ElementSubmitter;
import org.chai.kevin.form.FormElementService;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.planning.budget.BudgetCost;
import org.chai.kevin.planning.budget.PlanningEntryBudget;
import org.chai.kevin.planning.budget.PlanningTypeBudget;
import org.chai.kevin.value.RawDataElementValue;

import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.period.Period;

public class PlanningList {

	private final PlanningType planningType;
	private final Map<String, Enum> enums;
	
	private DataLocationEntity entity;
	private List<PlanningEntry> planningEntries;
	private List<PlanningEntryBudget> planningBudgetEntries;
	private FormEnteredValue formEnteredValue;
	private RawDataElementValue rawDataElementValue;
	
	public PlanningList(PlanningType planningType, DataLocationEntity entity, 
			FormEnteredValue formEnteredValue, RawDataElementValue rawDataElementValue, 
			Map<String, Enum> enums) {
		this.planningType = planningType;
		this.formEnteredValue = formEnteredValue;
		this.rawDataElementValue = rawDataElementValue;
		this.entity = entity;
		this.enums = enums;
	}
	
	public List<PlanningEntryBudget> getPlanningEntryBudgetList() {
		
		List<PlanningEntryBudget> planningEntryBudgets = new ArrayList<PlanningEntryBudget>();
		for (PlanningEntry planningEntry : planningList.getPlanningEntries()) {
			if (planningEntry.isSubmitted()) {
				Map<PlanningCost, BudgetCost> budgetCosts = new HashMap<PlanningCost, BudgetCost>();
				for (PlanningCost planningCost : planningEntry.getPlanningCosts()) {
					NormalizedDataElementValue value = valueService.getDataElementValue(planningCost.getDataElement(), location, type.getPeriod());
					if (!value.getValue().isNull()) {
						if (!value.getValue().getListValue().get(planningEntry.getLineNumber()).isNull())
							budgetCosts.put(planningCost, new BudgetCost(planningEntry, planningCost, value));
					}
				}
				planningEntryBudgets.add(new PlanningEntryBudget(planningEntry, budgetCosts));
			}
		}
		
	}
	
	public List<PlanningEntry> getPlanningEntries() {
		if (planningEntries == null) {
			planningEntries = new ArrayList<PlanningEntry>();
			if (validatableValue != null && !validatableValue.getValue().isNull()) {
				for (int i = 0; i < validatableValue.getValue().getListValue().size(); i++) {
					planningEntries.add(new PlanningEntry(entity, planningType, getValidatableValue(), i, enums));
				}
			}
		}
		return planningEntries;
	}
	
	public PlanningEntry getOrCreatePlanningEntry(Integer lineNumber) {
		PlanningEntry result = null;
		if (lineNumber >= getPlanningEntries().size()) {
			result = new PlanningEntry(entity, planningType, getValidatableValue(), lineNumber, enums);
			result.mergeValues(new HashMap<String, Object>());
		}
		else result = getPlanningEntries().get(lineNumber);
		return result;
	}
	
	public List<PlanningEntry> getLatestEntries(Integer numberOfEntries) {
		Integer fromIndex = Math.max(0, getPlanningEntries().size() - numberOfEntries);
		return getPlanningEntries().subList(fromIndex, getPlanningEntries().size());
	}
	
	public Integer getNextLineNumber() {
		return getPlanningEntries().size();
	}
	
	public boolean isBudgetUpdated() {
		for (PlanningEntry planningEntry : getPlanningEntries()) {
			if (!planningEntry.isBudgetUpdated() && planningEntry.isSubmitted()) return false;
		}
		return true;
	}
	
	public boolean isEmpty() {
		return getPlanningEntries().isEmpty();
	}
	
}

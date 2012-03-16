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
import org.chai.kevin.planning.budget.PlanningTypeBudget;
import org.chai.kevin.value.RawDataElementValue;

import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.period.Period;

public class PlanningList {

	private final PlanningType planningType;
	private final Map<String, Enum> enums;
	
	private DataLocationEntity entity;
	private List<PlanningEntry> planningEntries;
	private ValidatableValue validatableValue;
	
	public PlanningList(PlanningType planningType, DataLocationEntity entity, ValidatableValue validatableValue, Map<String, Enum> enums) {
		this.planningType = planningType;
		this.validatableValue = validatableValue;
		this.entity = entity;
		this.enums = enums;
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
	
	private ValidatableValue getValidatableValue() {
		return validatableValue;
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

package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chai.kevin.data.Enum;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.ValueService;

public class PlanningList {

	private final PlanningType planningType;
	private final RawDataElementValue dataElementValue;
	private final Map<String, Enum> enums;
	
	private List<PlanningEntry> planningEntries;
	private ValidatableValue validatableValue;
	
	public PlanningList(PlanningType planningType, RawDataElementValue dataElementValue, Map<String, Enum> enums) {
		this.planningType = planningType;
		this.dataElementValue = dataElementValue;
		this.enums = enums;
	}
	
	public List<PlanningEntry> getPlanningEntries() {
		if (planningEntries == null) {
			planningEntries = new ArrayList<PlanningEntry>();
			if (dataElementValue != null && !dataElementValue.getValue().isNull()) {
				for (int i = 0; i < dataElementValue.getValue().getListValue().size(); i++) {
					planningEntries.add(new PlanningEntry(planningType, getValidatableValue(), i, enums));
				}
			}
		}
		return planningEntries;
	}
	
	
	private ValidatableValue getValidatableValue() {
		if (validatableValue == null) {
			validatableValue = new ValidatableValue(dataElementValue.getValue(), dataElementValue.getData().getType());
		}
		return validatableValue;
	}
	
	public PlanningEntry getOrCreatePlanningEntry(Integer lineNumber) {
		if (lineNumber >= getPlanningEntries().size()) {
			return new PlanningEntry(planningType, getValidatableValue(), lineNumber, enums);
		}
		else return getPlanningEntries().get(lineNumber);
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
			if (!planningEntry.isBudgetUpdated()) return false;
		}
		return true;
	}
	
	public boolean isEmpty() {
		return getPlanningEntries().isEmpty();
	}
	
	public void save(ValueService valueService) {
		if (dataElementValue != null) valueService.save(dataElementValue);
	}
}

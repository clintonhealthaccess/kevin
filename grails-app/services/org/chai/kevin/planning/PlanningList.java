package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.chai.kevin.data.Enum;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.RawDataElementValue;

public class PlanningList {

	private final PlanningType planningType;
	private final Map<String, Enum> enums;
	
	private DataLocation dataLocation;
	private List<PlanningEntry> planningEntries;
	private List<PlanningEntryBudget> planningBudgetEntries;
	
	private FormEnteredValue formEnteredValue;
	private RawDataElementValue rawDataElementValue;
	private Map<PlanningCost, NormalizedDataElementValue> budgetValues;
	
	public PlanningList(PlanningType planningType, DataLocation dataLocation, 
			FormEnteredValue formEnteredValue, RawDataElementValue rawDataElementValue,
			Map<PlanningCost, NormalizedDataElementValue> budgetValues,
			Map<String, Enum> enums) {
		this.planningType = planningType;
		this.formEnteredValue = formEnteredValue;
		this.rawDataElementValue = rawDataElementValue;
		this.dataLocation = dataLocation;
		this.budgetValues = budgetValues;
		this.enums = enums;
	}
	
	public FormEnteredValue getFormEnteredValue() {
		return formEnteredValue;
	}
	
	public List<PlanningEntryBudget> getPlanningEntryBudgetList() {
		if (planningBudgetEntries == null) {
			planningBudgetEntries = new ArrayList<PlanningEntryBudget>();
			if (rawDataElementValue != null && !rawDataElementValue.getValue().isNull()) {
				for (int i = 0; i < rawDataElementValue.getValue().getListValue().size(); i++) {
					PlanningEntry planningEntry = getPlanningEntry(rawDataElementValue.getValue().getListValue().get(i).getAttribute(PlanningEntry.UUID));
					if (planningEntry != null) planningBudgetEntries.add(new PlanningEntryBudget(budgetValues, dataLocation, planningType, formEnteredValue.getValidatable(), planningEntry.getLineNumber(), enums));
				}
			}
		}
		return planningBudgetEntries;
	}
	
	public PlanningEntry getPlanningEntry(String uuid) {
		for (PlanningEntry planningEntry : getPlanningEntries()) {
			if (planningEntry.getUuid().equals(uuid)) return planningEntry;
		}
		return null;
	}

	public List<PlanningEntry> getPlanningEntries() {
		if (planningEntries == null) {
			planningEntries = new ArrayList<PlanningEntry>();
			if (formEnteredValue != null && !formEnteredValue.getValue().isNull()) {
				for (int i = 0; i < formEnteredValue.getValue().getListValue().size(); i++) {
					planningEntries.add(new PlanningEntry(dataLocation, planningType, formEnteredValue.getValidatable(), i, enums));
				}
			}
		}
		return planningEntries;
	}
	
	public PlanningEntry getOrCreatePlanningEntry(Integer lineNumber) {
		if (planningType.getMaxNumber() != null && planningType.getMaxNumber() <= lineNumber) 
			throw new IllegalArgumentException("lineNumber is bigger than the maximum allowed number of lines");
		
		PlanningEntry result = null;
		if (lineNumber >= getPlanningEntries().size()) {
			result = new PlanningEntry(dataLocation, planningType, formEnteredValue.getValidatable(), lineNumber, enums);
			result.mergeValues(new HashMap<String, Object>());
		}
		else result = getPlanningEntries().get(lineNumber);
		if (result.getUuid() == null) result.setUuid(UUID.randomUUID().toString());
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
	
	public Double getIncoming() {
		return getSum(PlanningCostType.INCOMING);
	}
	
	private Double getSum(PlanningCostType costType) {
		Double result = 0d;
		for (PlanningEntryBudget line : getPlanningEntryBudgetList()) {
			result += line.getSum(costType);
		}
		return result;
	}
	
	public Double getOutgoing() {
		return getSum(PlanningCostType.OUTGOING);
	}
	
	public Double getDifference() {
		return getIncoming() - getOutgoing();
	}
	
}

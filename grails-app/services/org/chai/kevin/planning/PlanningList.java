package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.List;

import org.chai.kevin.data.Enum;
import org.chai.kevin.value.RawDataElementValue;

public class PlanningList {

	private PlanningType planningType;
	private RawDataElementValue dataElementValue;
	private Enum enume;
	private List<PlanningEntry> planningEntries;
	
	public PlanningList(PlanningType planningType, RawDataElementValue dataElementValue, Enum enume) {
		this.planningType = planningType;
		this.dataElementValue = dataElementValue;
		this.enume = enume;
	}
	
	public List<PlanningEntry> getPlanningEntries() {
		if (planningEntries == null) {
			planningEntries = new ArrayList<PlanningEntry>();
			if (dataElementValue != null && !dataElementValue.getValue().isNull()) {
				for (int i = 0; i < dataElementValue.getValue().getListValue().size(); i++) {
					planningEntries.add(new PlanningEntry(planningType, dataElementValue, i, enume));
				}
			}
		}
		return planningEntries;
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
	
}

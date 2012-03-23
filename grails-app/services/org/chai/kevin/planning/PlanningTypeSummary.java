package org.chai.kevin.planning;

import java.util.Map;

import org.chai.kevin.location.DataLocation;

public class PlanningTypeSummary {

	private PlanningType planningType;
	private Map<DataLocation, Integer> numberOfEntries;

	public PlanningTypeSummary(PlanningType planningType, Map<DataLocation, Integer> numberOfEntries) {
		this.planningType = planningType;
		this.numberOfEntries = numberOfEntries;
	}
	
	public Integer getNumberOfEntries(DataLocation dataLocation) {
		return numberOfEntries.get(dataLocation);
	}
	
}

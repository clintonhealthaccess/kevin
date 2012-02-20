package org.chai.kevin.planning;

import java.util.Map;

import org.chai.kevin.location.DataLocationEntity;

public class PlanningTypeSummary {

	private PlanningType planningType;
	private Map<DataLocationEntity, Integer> numberOfEntries;

	public PlanningTypeSummary(PlanningType planningType, Map<DataLocationEntity, Integer> numberOfEntries) {
		this.planningType = planningType;
		this.numberOfEntries = numberOfEntries;
	}
	
	public Integer getNumberOfEntries(DataLocationEntity dataEntity) {
		return numberOfEntries.get(dataEntity);
	}
	
}

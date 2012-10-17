package org.chai.kevin.planning;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.chai.location.DataLocation;

public class PlanningSummaryPage {

	public final static String LOCATION_SORT = "location";
	
	private final List<PlanningType> planningTypes;
	private final List<DataLocation> dataLocations;
	private final Map<PlanningType, PlanningTypeSummary> summaries;
	
	public PlanningSummaryPage(List<PlanningType> planningTypes, List<DataLocation> dataLocations, Map<PlanningType, PlanningTypeSummary> summaries) {
		this.dataLocations = dataLocations;
		this.summaries = summaries;
		this.planningTypes = planningTypes;
	}
	
	public List<PlanningType> getPlanningTypes() {
		return planningTypes;
	}
	
	public List<DataLocation> getDataLocations() {
		return dataLocations;
	}
	
	public Integer getNumberOfEntries(DataLocation dataLocation, PlanningType type) {
		return summaries.get(type).getNumberOfEntries(dataLocation);
	}
	
	public void sort(String parameter, String order, String language) {
		if (parameter == null || order == null) return;
		if (parameter.equals(LOCATION_SORT)) {
//			Collections.sort(dataLocations, LocationSorter.BY_NAME(language));
		}
		else {
			Long planningTypeId = null;
			try {
				planningTypeId = Long.parseLong(parameter.replace("planning-", ""));
			} catch (NumberFormatException e) {}
			
			PlanningType sortPlanningType = null;
			for (PlanningType planningType : planningTypes) {
				if (planningType.getId().equals(planningTypeId)) {
					sortPlanningType = planningType;
					break;
				}
			}
			
			if (sortPlanningType != null) {
				final PlanningType comparePlanningType = sortPlanningType; 
				Collections.sort(dataLocations, new Comparator<DataLocation>() {
					@Override
					public int compare(DataLocation l1, DataLocation l2) {
						return summaries.get(comparePlanningType).getNumberOfEntries(l1)
							.compareTo(summaries.get(comparePlanningType).getNumberOfEntries(l2));
					}
				});
			}
		}
		if (order.equals("desc")) Collections.reverse(dataLocations);
	}
	
}

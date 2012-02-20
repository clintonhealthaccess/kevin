package org.chai.kevin.planning;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.chai.kevin.LocationSorter;
import org.chai.kevin.location.DataLocationEntity;

public class PlanningSummaryPage {

	public final static String FACILITY_SORT = "facility";
	
	private final List<PlanningType> planningTypes;
	private final List<DataLocationEntity> dataEntities;
	private final Map<PlanningType, PlanningTypeSummary> summaries;
	
	public PlanningSummaryPage(List<PlanningType> planningTypes, List<DataLocationEntity> dataEntities, Map<PlanningType, PlanningTypeSummary> summaries) {
		this.dataEntities = dataEntities;
		this.summaries = summaries;
		this.planningTypes = planningTypes;
	}
	
	public List<PlanningType> getPlanningTypes() {
		return planningTypes;
	}
	
	public List<DataLocationEntity> getDataEntities() {
		return dataEntities;
	}
	
	public Integer getNumberOfEntries(DataLocationEntity dataEntity, PlanningType type) {
		return summaries.get(type).getNumberOfEntries(dataEntity);
	}
	
	public void sort(String parameter, String order, String language) {
		if (parameter == null || order == null) return;
		if (parameter.equals(FACILITY_SORT)) {
			Collections.sort(dataEntities, LocationSorter.BY_NAME(language));
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
				Collections.sort(dataEntities, new Comparator<DataLocationEntity>() {
					@Override
					public int compare(DataLocationEntity l1, DataLocationEntity l2) {
						return summaries.get(comparePlanningType).getNumberOfEntries(l1)
							.compareTo(summaries.get(comparePlanningType).getNumberOfEntries(l2));
					}
				});
			}
		}
		if (order.equals("desc")) Collections.reverse(dataEntities);
	}
	
}

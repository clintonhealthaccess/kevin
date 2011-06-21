package org.chai.kevin.dashboard;

import java.util.Map;

import org.chai.kevin.Info;

public class DashboardObjectiveInfo extends Info {

	private DashboardPercentage percentage;
	private Map<DashboardObjectiveEntry, DashboardPercentage> values;
	
	public DashboardObjectiveInfo(DashboardPercentage percentage, Map<DashboardObjectiveEntry, DashboardPercentage> values) {
		this.percentage = percentage;
		this.values = values;
	}
	
	@Override
	public String getTemplate() {
		return "/dashboard/objectiveInfo";
	}

	@Override
	public String getValue() {
		if (percentage.getValue() == null) return null;
		return String.valueOf(percentage.getValue());
	}
	
	public Double getNumberValue() {
		if (percentage.getValue() == null) return null;
		return percentage.getValue();
	}

}

package org.chai.kevin.survey.export;

import java.util.List;

public class SurveyExportData {
	
	private List<SurveyExportDataPoint> dataPoints;

	public SurveyExportData(List<SurveyExportDataPoint> dataPoints){
		this.dataPoints = dataPoints;
	}
	
	public List<SurveyExportDataPoint> getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(List<SurveyExportDataPoint> dataPoints) {
		this.dataPoints = dataPoints;
	}
}

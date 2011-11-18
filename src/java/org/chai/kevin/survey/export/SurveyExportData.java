package org.chai.kevin.survey.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.chai.kevin.Organisation;
import org.chai.kevin.data.Type;
import org.chai.kevin.survey.SurveyQuestion.QuestionType;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class SurveyExportData {
	
	private List<SurveyExportDataHeader> dataHeaders;
	private List<SurveyExportDataPoint> dataPoints;
	
	public SurveyExportData(List<SurveyExportDataHeader> dataHeaders, List<SurveyExportDataPoint> dataPoints){
		this.dataHeaders = dataHeaders;
		this.dataPoints = dataPoints;
	}

	public String[] getExportDataHeaders() {
		List<String> headers = new ArrayList<String>();		
		List<SurveyExportDataHeader> surveyExportDataHeaders = this.dataHeaders;		
		for(SurveyExportDataHeader surveyExportDataHeader : surveyExportDataHeaders){
			headers.add(surveyExportDataHeader.getName());
		}
		return headers.toArray(new String[surveyExportDataHeaders.size()]);
	}

	public List<SurveyExportDataHeader> getDataHeaders() {
		return dataHeaders;
	}

	public void setDataHeaders(List<SurveyExportDataHeader> dataHeaders) {
		this.dataHeaders = dataHeaders;
	}
	
	public List<SurveyExportDataPoint> getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(List<SurveyExportDataPoint> dataPoints) {
		this.dataPoints = dataPoints;
	}
}

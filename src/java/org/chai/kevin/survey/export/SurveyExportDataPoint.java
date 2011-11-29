package org.chai.kevin.survey.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class SurveyExportDataPoint extends ArrayList<String> {
	
	public SurveyExportDataPoint(){
		super();
	}

	public SurveyExportDataPoint(SurveyExportDataPoint exportDataPoint) {
		super(exportDataPoint);
	}

}
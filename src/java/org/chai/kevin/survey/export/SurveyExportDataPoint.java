package org.chai.kevin.survey.export;

import java.util.ArrayList;

public class SurveyExportDataPoint extends ArrayList<String> {
	
	private static final long serialVersionUID = -8812306436104509210L;

	public SurveyExportDataPoint(){
		super();
	}

	public SurveyExportDataPoint(SurveyExportDataPoint exportDataPoint) {
		super(exportDataPoint);
	}

}
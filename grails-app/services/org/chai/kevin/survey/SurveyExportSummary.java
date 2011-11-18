package org.chai.kevin.survey;

import java.util.Map;
import java.util.Set;

import org.chai.kevin.Organisation;
import org.chai.kevin.survey.export.SurveyExportData;

public class SurveyExportSummary {

	private Organisation organisation;
	private Map<Organisation, SurveyExportData> facilityDataPoints;	
	
	public SurveyExportSummary(Organisation organisation, Map<Organisation, SurveyExportData> facilityDataPoints){
		this.organisation = organisation;
		this.facilityDataPoints = facilityDataPoints;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public Map<Organisation, SurveyExportData> getFacilityDataPoints() {
		return facilityDataPoints;
	}
	
	public Set<Organisation> getFacilities(){
		return facilityDataPoints.keySet();
	}
	
	public SurveyExportData getSurveyTypeData(Organisation facility){
		return facilityDataPoints.get(facility);
	}

}

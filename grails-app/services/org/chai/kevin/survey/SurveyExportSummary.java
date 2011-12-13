package org.chai.kevin.survey;

import java.util.Map;
import java.util.Set;

import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.survey.export.SurveyExportData;

public class SurveyExportSummary {

	private CalculationEntity entity;
	private Map<DataEntity, SurveyExportData> facilityDataPoints;	
	
	public SurveyExportSummary(CalculationEntity entity, Map<DataEntity, SurveyExportData> facilityDataPoints){
		this.entity = entity;
		this.facilityDataPoints = facilityDataPoints;
	}

	public CalculationEntity getOrganisation() {
		return entity;
	}

	public Map<DataEntity, SurveyExportData> getFacilityDataPoints() {
		return facilityDataPoints;
	}
	
	public Set<DataEntity> getFacilities(){
		return facilityDataPoints.keySet();
	}
	
	public SurveyExportData getSurveyTypeData(DataEntity facility){
		return facilityDataPoints.get(facility);
	}

}

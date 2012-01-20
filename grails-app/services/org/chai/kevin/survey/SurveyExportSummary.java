package org.chai.kevin.survey;

import java.util.Map;
import java.util.Set;

import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.survey.export.SurveyExportData;

public class SurveyExportSummary {

	private CalculationEntity entity;
	private Map<DataLocationEntity, SurveyExportData> facilityDataPoints;	
	
	public SurveyExportSummary(CalculationEntity entity, Map<DataLocationEntity, SurveyExportData> facilityDataPoints){
		this.entity = entity;
		this.facilityDataPoints = facilityDataPoints;
	}

	public CalculationEntity getLocation() {
		return entity;
	}

	public Map<DataLocationEntity, SurveyExportData> getFacilityDataPoints() {
		return facilityDataPoints;
	}
	
	public Set<DataLocationEntity> getFacilities(){
		return facilityDataPoints.keySet();
	}
	
	public SurveyExportData getSurveyTypeData(DataLocationEntity facility){
		return facilityDataPoints.get(facility);
	}

}

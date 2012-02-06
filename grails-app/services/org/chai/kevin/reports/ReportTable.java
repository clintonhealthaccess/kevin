package org.chai.kevin.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.LocationEntity;

public abstract class ReportTable<T, S extends CalculationEntity> {

	protected Map<S, Map<T, ReportValue>> valueMap;
	protected List<T> targets;
//	protected Map<LocationEntity, List<S>> locationMap;
	
	public ReportTable(Map<S, Map<T, ReportValue>> valueMap, List<T> targets,
			Map<LocationEntity, List<S>> locationMap) {
		this.valueMap = valueMap;
		this.targets = targets;
//		this.locationMap = locationMap;
	}

//	public List<CalculationEntity> getLocations() {
//		return new ArrayList<CalculationEntity>(locationMap.keySet());
//	}
	
	public List<T> getTargets(){
		return targets;
	}
	
	public ReportValue getReportValue(CalculationEntity location, T target){
		ReportValue reportValue = null;
		Map<T, ReportValue> reportValues = valueMap.get(location);
		if(reportValues != null) 
			reportValue = reportValues.get(target);
		return reportValue;
	}

}

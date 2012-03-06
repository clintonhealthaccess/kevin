package org.chai.kevin.reports;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.LocationEntity;

public abstract class ReportTable<T, S extends CalculationEntity> {

	protected Map<S, Map<T, ReportValue>> valueMap;
	protected List<T> targets;
	
	public ReportTable(Map<S, Map<T, ReportValue>> valueMap, List<T> targets) {
		this.valueMap = valueMap;
		this.targets = targets;
	}
	
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
	
	public boolean hasData(){
		return !valueMap.isEmpty();
	}

}

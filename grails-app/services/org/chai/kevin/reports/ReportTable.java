package org.chai.kevin.reports;

import java.util.Map;
import java.util.Set;

import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.value.Value;

public abstract class ReportTable<T, S extends CalculationLocation> {

	protected Map<S, Map<T, ? extends Object>> valueMap;
	
	public ReportTable(Map<S, Map<T, ? extends Object>> valueMap) {
		this.valueMap = valueMap;
	}
	
	public Set<S> getLocations(){
		return valueMap.keySet();
	}
	
	public boolean hasData(){
		return !valueMap.isEmpty();
	}

	public Object getReportValue(CalculationLocation location, T target){
		Object reportValue = null;
		Map<T, ? extends Object> reportValues = valueMap.get(location);
		if (reportValues != null) reportValue = reportValues.get(target);
		return reportValue;
	}
}

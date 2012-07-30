package org.chai.kevin.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.location.CalculationLocation;

public abstract class ReportTable<T, S extends CalculationLocation, U> {

	protected Map<S, Map<T, U>> valueMap;
	
	public ReportTable(Map<S, Map<T, U>> valueMap) {
		this.valueMap = valueMap;
	}
	
	public List<S> getLocations(){
		return new ArrayList<S>(valueMap.keySet());
	}
	
	public boolean hasData(){
		return !valueMap.isEmpty();
	}

	public U getReportValue(CalculationLocation location, T target){
		U reportValue = null;
		Map<T, U> reportValues = valueMap.get(location);
		if (reportValues != null) reportValue = reportValues.get(target);
		return reportValue;
	}
}

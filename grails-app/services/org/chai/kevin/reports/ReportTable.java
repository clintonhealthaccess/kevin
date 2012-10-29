package org.chai.kevin.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.value.Value;

public abstract class ReportTable<T, S extends CalculationLocation, U> {

	protected Map<S, Map<T, U>> valueMap;
	
	public ReportTable(Map<S, Map<T, U>> valueMap) {
		this.valueMap = valueMap;
	}
	
	public List<S> getLocations(){
		List<S> locations = new ArrayList<S>();
		locations.addAll(valueMap.keySet());
		return locations;
	}
	
	public List<T> getIndicators(){
		List<T> indicators = new ArrayList<T>();
		for(CalculationLocation location : valueMap.keySet()){
			Map<T, U> targetMap = valueMap.get(location);
			for(T target: targetMap.keySet()){
				if(!indicators.contains(target)) indicators.add(target);
			}
		}
		return indicators;
	}
	
	//public abstract List getIndicators();
	
	public boolean hasData(){
		return !valueMap.isEmpty();
	}

	public abstract Value getValue(CalculationLocation location, T target);
	
	public U getReportValue(CalculationLocation location, T target){
		U reportValue = null;
		Map<T, U> reportValues = valueMap.get(location);
		if (reportValues != null) reportValue = reportValues.get(target);
		return reportValue;
	}
	
	public abstract U getTableReportValue(CalculationLocation location, T indicator);
	public abstract Value getMapReportValue(CalculationLocation location, T indicator);
}

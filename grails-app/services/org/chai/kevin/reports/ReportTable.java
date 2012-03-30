package org.chai.kevin.reports;

import java.util.Map;

import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.value.Value;

public abstract class ReportTable<T, S extends CalculationLocation> {

	protected Map<S, Map<T, Value>> valueMap;
	
	public ReportTable(Map<S, Map<T, Value>> valueMap) {
		this.valueMap = valueMap;
	}
	
	public boolean hasData(){
		return !valueMap.isEmpty();
	}

	public Value getReportValue(CalculationLocation location, T target){
		Value reportValue = null;
		Map<T, Value> reportValues = valueMap.get(location);
		if (reportValues != null) reportValue = reportValues.get(target);
		return reportValue;
	}
}

package org.chai.kevin.reports;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;

public abstract class ReportTable<T, S extends CalculationLocation> {

	protected Map<S, Map<T, ReportValue>> valueMap;
	
	public ReportTable(Map<S, Map<T, ReportValue>> valueMap) {
		this.valueMap = valueMap;
	}
	
	public boolean hasData(){
		return !valueMap.isEmpty();
	}

}

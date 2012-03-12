package org.chai.kevin.reports;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.LocationEntity;

public abstract class ReportTable<T, S extends CalculationEntity> {

	protected Map<S, Map<T, ReportValue>> valueMap;
	
	public ReportTable(Map<S, Map<T, ReportValue>> valueMap) {
		this.valueMap = valueMap;
	}
	
	public boolean hasData(){
		return !valueMap.isEmpty();
	}

}

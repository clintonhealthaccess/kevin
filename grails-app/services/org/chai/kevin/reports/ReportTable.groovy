package org.chai.kevin.reports;

import java.util.List;
import java.util.Map;

import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.StoredValue;
import org.chai.kevin.value.Value;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;

public class ReportTable {

	private Map<CalculationLocation, Map<AbstractReportTarget, DataValue>> valueMap;
	private List<AbstractReportTarget> indicators;
	
	public ReportTable(Map valueMap, List indicators) {
		this.valueMap = valueMap;
		this.indicators = indicators;
	}
	
	public List<CalculationLocation> getLocations(Location parent, Set<LocationLevel> skipLevels, Set<DataLocationType> types) {
		def result = new ArrayList<CalculationLocation>();
		result.addAll(parent.getChildren(skipLevels));
		result.addAll(parent.getDataLocations(skipLevels, types));
		return result.sort({it.names});
	}
	
	public List<AbstractReportTarget> getIndicators() {
		return indicators.sort({it.order})
	}
	
	public boolean hasData(){
		def result = false
		valueMap.each { location, map -> 
			map.each { entity, value ->
				if (value != null) result = true
			}
		}
		return result
	}

	public DataValue getTableReportValue(CalculationLocation location, AbstractReportTarget indicator) {
		return valueMap.get(location)?.get(indicator);
	}

}

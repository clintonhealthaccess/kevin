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
		return parent.getChildrenLocations(skipLevels, types).sort({it.names});
	}
	
	public List<CalculationLocation> getLocationsWithData(Location parent, Set<LocationLevel> skipLevels, Set<DataLocationType> types) {
		def locationsWithData = []
		def locations = parent.getLocations(parent, skipLevels, types);
		locations.each { location ->
			if(hasData(location)) locationsWithData.add(location)
		}
		return locationsWithData;
	}

	public List<AbstractReportTarget> getIndicators() {
		return indicators.sort({it.order})
	}
	
	public boolean hasData(){
		def result = false
		valueMap.each { location, map -> 
			if(hasData(location)) result = true
		}
		return result
	}

	public boolean hasData(CalculationLocation location){
		def result = false
		def map = valueMap.get(location);
		if(map != null){
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

package org.chai.kevin.fct;

import grails.plugin.springcache.annotations.Cacheable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LanguageService;
import org.chai.kevin.LocationSorter;
import org.chai.kevin.Period;
import org.chai.kevin.data.Sum;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.SumValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.springframework.transaction.annotation.Transactional;

public class FctService {
	private static final Log log = LogFactory.getLog(FctService.class);
	
	private LanguageService languageService;
	private ReportService reportService;
	private ValueService valueService;
	private Set<String> skipLevels;
	
	@Cacheable("fctCache")
	@Transactional(readOnly = true)
	public FctTable getFctTable(Location location, ReportProgram program, FctTarget target, Period period, LocationLevel level, Set<DataLocationType> types) {		
		if (log.isDebugEnabled()) 
			log.debug("getFctTable(period="+period+",location="+location+",level="+level+",program="+program+",target="+target+")");				
		
		List<CalculationLocation> topLevelLocations = new ArrayList<CalculationLocation>();
		List<FctTargetOption> targetOptions = target.getTargetOptions();
		Map<CalculationLocation, Map<FctTargetOption, Value>> valueMap = new HashMap<CalculationLocation, Map<FctTargetOption, Value>>();
		
		if(targetOptions.isEmpty())
			return new FctTable(valueMap, targetOptions, topLevelLocations);
		
		Set<LocationLevel> skips = reportService.getSkipLocationLevels(skipLevels);
		List<Location> treeLocations = location.collectTreeWithDataLocations(skips, types);
		List<DataLocation> dataLocations = location.collectDataLocations(skips, types);
		
		for (Location treeLocation : treeLocations) {
			Map<FctTargetOption, Value> targetMap = new HashMap<FctTargetOption, Value>();
			for(FctTargetOption targetOption : targetOptions){
				if (log.isDebugEnabled()) log.debug("getting values for sum fct with calculation: "+targetOption.getSum());
				targetMap.put(targetOption, getFctValue(targetOption, treeLocation, period, types));
			}
			valueMap.put(treeLocation, targetMap);
			
//			if(level != null && treeLocation.getLevel().equals(level))
//				topLevelLocations.add(treeLocation);
		}
		
		for (DataLocation dataLocation : dataLocations) {
			Map<FctTargetOption, Value> targetMap = new HashMap<FctTargetOption, Value>();
			for(FctTargetOption targetOption : targetOptions){
				if (log.isDebugEnabled()) log.debug("getting values for sum fct with calculation: "+targetOption.getSum());
				Set<DataLocationType> dataLocationTypes = new HashSet<DataLocationType>();
				dataLocationTypes.add(dataLocation.getType());
				targetMap.put(targetOption, getFctValue(targetOption, dataLocation, period, dataLocationTypes));
			}
			valueMap.put(dataLocation, targetMap);						
		}
		
		topLevelLocations.addAll(location.getChildrenEntitiesWithDataLocations(skips, types));
		
		//TODO sort location map keys
//		List<CalculationLocation> sortedLocations = new ArrayList<CalculationLocation>(valueMap.keySet());
//		Collections.sort(sortedLocations, LocationSorter.BY_LEVEL(languageService.getCurrentLanguage()));		
//		Map<Location, Map<FctTargetOption, Value>> sortedValueMap = new LinkedHashMap<Location, Map<FctTargetOption, Value>>();		
//		for (Location sortedLocation : sortedLocations){		
//			sortedValueMap.put(sortedLocation, valueMap.get(sortedLocation));
//		}
//		
//		FctTable fctTable = new FctTable(sortedValueMap, targetOptions);
		
		FctTable fctTable = new FctTable(valueMap, targetOptions, topLevelLocations);
		if (log.isDebugEnabled()) log.debug("getFctTable(...)="+fctTable);
		return fctTable;
	}

	private Value getFctValue(FctTargetOption targetOption, CalculationLocation location, Period period, Set<DataLocationType> types) {
		Value value = null;
		CalculationValue<?> calculationValue = valueService.getCalculationValue(targetOption.getSum(), location, period, types);
		if (calculationValue != null) 
			value = calculationValue.getValue();
		return value;
	}
	
//	private Value getFctValue2(FctTargetOption targetOption, CalculationLocation location, Period period, Set<DataLocationType> types) {
//		Value value = null;
//		targetOption.getSum().getExpression();
//		RawDataElementValue rawDataElementValue = null;
//		rawDataElementValue = valueService.getDataElementValue(data, location, period);
//		CalculationValue<?> calculationValue = valueService.getCalculationValue(targetOption.getSum(), location, period, types);
//		if (calculationValue != null) 
//			value = calculationValue.getValue();
//		return value;
//	}
	
	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}
	
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setSkipLevels(Set<String> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public Set<LocationLevel> getSkipLocationLevels(){
		return reportService.getSkipLocationLevels(skipLevels);
	}
}
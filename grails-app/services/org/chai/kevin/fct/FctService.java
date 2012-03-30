package org.chai.kevin.fct;

import grails.plugin.springcache.annotations.Cacheable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LanguageService;
import org.chai.kevin.LocationSorter;
import org.chai.kevin.Period;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.value.CalculationValue;
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
		
		List<FctTargetOption> targetOptions = target.getTargetOptions();
		Map<Location, Map<FctTargetOption, Value>> valueMap = new HashMap<Location, Map<FctTargetOption, Value>>();
		
		if(targetOptions.isEmpty())
			return new FctTable(valueMap, targetOptions);		
		
		Set<LocationLevel> skips = reportService.getSkipLocationLevels(skipLevels);
		List<Location> childLocations = location.collectTreeWithDataLocations(skips, types);
		
		for (Location child : childLocations) {
			Map<FctTargetOption, Value> targetMap = new HashMap<FctTargetOption, Value>();
			for(FctTargetOption targetOption : targetOptions){
				if (log.isDebugEnabled()) log.debug("getting values for sum fct with calculation: "+targetOption.getSum());
				targetMap.put(targetOption, getFctValue(targetOption, child, period, types));
			}
			valueMap.put(child, targetMap);
		}
		
		//sort location map keys
		List<Location> sortedLocations = new ArrayList<Location>(valueMap.keySet());
		Collections.sort(sortedLocations, LocationSorter.BY_LEVEL(languageService.getCurrentLanguage()));		
		Map<Location, Map<FctTargetOption, Value>> sortedValueMap = new LinkedHashMap<Location, Map<FctTargetOption, Value>>();		
		for (Location sortedLocation : sortedLocations){		
			sortedValueMap.put(sortedLocation, valueMap.get(sortedLocation));
		}
		
		FctTable fctTable = new FctTable(sortedValueMap, targetOptions);
		if (log.isDebugEnabled()) log.debug("getFctTable(...)="+fctTable);
		return fctTable;
	}

	private Value getFctValue(FctTargetOption targetOption, CalculationLocation location, Period period, Set<DataLocationType> types) {
		Value value = null;
		CalculationValue<?> calculationValue = valueService.getCalculationValue(targetOption.getSum(), location, period, types);
		if (calculationValue != null) value = calculationValue.getValue();
		return value;
	}

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
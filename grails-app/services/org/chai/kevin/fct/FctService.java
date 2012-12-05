package org.chai.kevin.fct;

import grails.plugin.springcache.annotations.Cacheable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Period;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.util.Utils.ReportType;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.SumValue;
import org.chai.kevin.value.ValueService;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.springframework.transaction.annotation.Transactional;

public class FctService {
	private static final Log log = LogFactory.getLog(FctService.class);
	
	private ReportService reportService;
	private ValueService valueService;
	private Set<String> locationSkipLevels;
	
	@Cacheable("fctCache")
	@Transactional(readOnly = true)
	public FctTable getFctTable(Location location, FctTarget target, Period period, Set<DataLocationType> types, ReportType reportType) {		
		if (log.isDebugEnabled()) 
			log.debug("getFctTable(period="+period+",location="+location+",target="+target+")");				
				
		List<CalculationLocation> calculationLocations = new ArrayList<CalculationLocation>();
		
		Set<LocationLevel> skips = reportService.getSkipReportLevels(locationSkipLevels);			
		switch(reportType){
			case MAP:
				calculationLocations.addAll(location.getChildrenEntitiesWithDataLocations(skips, types));
				calculationLocations.addAll(location.getDataLocations(skips, types));
				break;
			case TABLE:
			default:
				calculationLocations.addAll(location.collectTreeWithDataLocations(skips, types));
				calculationLocations.addAll(location.collectDataLocations(types));
		}
		
		Map<CalculationLocation, Map<FctTargetOption, DataValue>> valueMap = new HashMap<CalculationLocation, Map<FctTargetOption, DataValue>>();
		List<FctTargetOption> targetOptions = target.getAllTargetOptions();
		for (CalculationLocation treeLocation : calculationLocations) {
			Map<FctTargetOption, DataValue> targetMap = new HashMap<FctTargetOption, DataValue>();
			for(FctTargetOption targetOption : targetOptions){
				if (log.isDebugEnabled()) log.debug("getting values for sum fct with calculation: "+targetOption.getSum());
				targetMap.put(targetOption, getFctValue(targetOption, treeLocation, period, types));
			}
			valueMap.put(treeLocation, targetMap);
		}				
		
		FctTable fctTable = new FctTable(valueMap, targetOptions);
		if (log.isDebugEnabled()) log.debug("getFctTable(...)="+fctTable);
		return fctTable;
	}

	private SumValue getFctValue(FctTargetOption targetOption, CalculationLocation location, Period period, Set<DataLocationType> types) {
		SumValue sumValue = (SumValue) valueService.getCalculationValue(targetOption.getSum(), location, period, types);
		return sumValue;
	}

	public List<FctTarget> getFctTargetsWithOptions(ReportProgram program){
		List<FctTarget> result = new ArrayList<FctTarget>();
		List<FctTarget> targets = program.getReportTargets(FctTarget.class);
		for(FctTarget target : targets){
			if(!target.getAllTargetOptions().isEmpty())
				result.add(target);
		}
		return result;
	}
	
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setLocationSkipLevels(Set<String> locationSkipLevels) {
		this.locationSkipLevels = locationSkipLevels;
	}
	
	public Set<LocationLevel> getSkipLocationLevels(){
		return reportService.getSkipReportLevels(locationSkipLevels);
	}
}
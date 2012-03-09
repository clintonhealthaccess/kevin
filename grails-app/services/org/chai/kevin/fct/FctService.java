package org.chai.kevin.fct;

import grails.plugin.springcache.annotations.Cacheable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LocationService;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.reports.ReportValue;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class FctService {
	private static final Log log = LogFactory.getLog(FctService.class);
	
	private LocationService locationService;
	private ReportService reportService;
	private ValueService valueService;
	private Set<String> skipLevels;
	
	@Cacheable("fctCache")
	@Transactional(readOnly = true)
	public FctTable getFctTable(LocationEntity location, ReportProgram program, Period period, LocationLevel level, Set<DataEntityType> types) {		
		if (log.isDebugEnabled()) 
			log.debug("getFctTable(period="+period+",location="+location+",level="+level+",program="+program+")");				
		
		List<FctTarget> targets = reportService.getReportTargets(FctTarget.class, program);
		
		Map<FctTarget, ReportValue> totalMap = new HashMap<FctTarget, ReportValue>();
		Map<LocationEntity, Map<FctTarget, ReportValue>> valueMap = new HashMap<LocationEntity, Map<FctTarget, ReportValue>>();
		
		if(targets.isEmpty())
			return new FctTable(totalMap, valueMap, targets);
		
		for(FctTarget target : targets){			
			totalMap.put(target, getFctValue(target, location, period, types));
		}
				
//		List<LocationEntity> children = locationService.getChildrenOfLevel(location, level);
		Set<LocationLevel> skips = reportService.getSkipLocationLevels(skipLevels);
		List<LocationEntity> children = location.getChildren(skips);
		
		for (LocationEntity child : children) {
			Map<FctTarget, ReportValue> targetMap = new HashMap<FctTarget, ReportValue>();
			for(FctTarget target : targets){
				if (log.isDebugEnabled()) log.debug("getting values for sum fct with calculation: "+target.getSum());
				targetMap.put(target, getFctValue(target, child, period, types));
			}
			valueMap.put(child, targetMap);
		}
		
		FctTable fctTable = new FctTable(totalMap, valueMap, targets);
		if (log.isDebugEnabled()) log.debug("getFctTable(...)="+fctTable);
		return fctTable;
	}

	private ReportValue getFctValue(FctTarget target, CalculationEntity entity, Period period, Set<DataEntityType> types) {
		String value = null;
		CalculationValue<?> calculationValue = valueService.getCalculationValue(target.getSum(), entity, period, types);
		if (calculationValue != null) value = calculationValue.getValue().getNumberValue().toString();
		return new ReportValue(value);
	}

	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public Set<LocationLevel> getSkipLocationLevels(){
		return reportService.getSkipLocationLevels(skipLevels);
	}
}
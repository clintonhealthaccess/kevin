package org.chai.kevin.dsr;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LanguageService;
import org.chai.kevin.Period;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class DsrService {
	private static final Log log = LogFactory.getLog(DsrService.class);
	
	private ReportService reportService;
	private ValueService valueService;
	private DataService dataService;
	private LanguageService languageService;
	private Set<String> skipLevels;
	
	@Cacheable("dsrCache")
	@Transactional(readOnly = true)

	public DsrTable getDsrTable(Location location, ReportProgram program, Period period, Set<DataLocationType> types, DsrTargetCategory category) {
		if (log.isDebugEnabled())  log.debug("getDsrTable(period="+period+",location="+location+",program="+program+",types="+types+",category="+category+")");
				
		List<DataLocation> dataLocations = location.collectDataLocations(null, types);
		
		List<DsrTarget> targets = new ArrayList<DsrTarget>();
		if(category != null) targets.addAll(category.getTargets());
		else targets.addAll(reportService.getReportTargets(DsrTarget.class, program));
		
		Map<DataLocation, Map<DsrTarget, Value>> valueMap = new HashMap<DataLocation, Map<DsrTarget, Value>>();		
		List<DsrTargetCategory> targetCategories = new ArrayList<DsrTargetCategory>();
		
		if(dataLocations.isEmpty() || targets.isEmpty())
			return new DsrTable(valueMap, targets, targetCategories);		
		
		for (DataLocation dataLocation : dataLocations) {
			Map<DsrTarget, Value> targetMap = new HashMap<DsrTarget, Value>();			
			for (DsrTarget target : targets) {
				targetMap.put(target, getDsrValue(target, dataLocation, period));
			}
			valueMap.put(dataLocation, targetMap);
		}				
			
		targetCategories = getTargetCategories(program);
		
		DsrTable dsrTable = new DsrTable(valueMap, targets, targetCategories);
		if (log.isDebugEnabled()) log.debug("getDsrTable(...)="+dsrTable);
		return dsrTable;
	}

	private Value getDsrValue(DsrTarget target, DataLocation dataLocation, Period period){
		Value value = null;
		
		Set<String> targetUuids = Utils.split(target.getTypeCodeString());
		if (targetUuids.contains(dataLocation.getType().getCode())) {
			DataValue dataValue = valueService.getDataElementValue(target.getDataElement(), dataLocation, period);
			if (dataValue != null) value = dataValue.getValue();
		}
		
		return value;
	}
	
	public List<DsrTargetCategory> getTargetCategories(ReportProgram program){
		Set<DsrTargetCategory> categories = new HashSet<DsrTargetCategory>();
		List<DsrTarget> targets = reportService.getReportTargets(DsrTarget.class, program);
		for(DsrTarget target : targets)
			if(target.getCategory() != null) categories.add(target.getCategory());
		List<DsrTargetCategory> sortedCategories = new ArrayList<DsrTargetCategory>(categories);
		Collections.sort(sortedCategories);
		return sortedCategories;	
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}
	
	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}
	
	public void setSkipLevels(Set<String> skipLevels) {
		this.skipLevels = skipLevels;
	}

	public Set<LocationLevel> getSkipLocationLevels(){
		return reportService.getSkipLocationLevels(skipLevels);
	}
}

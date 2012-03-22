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
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.reports.ReportValue;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ValueService;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class FctService {
	private static final Log log = LogFactory.getLog(FctService.class);
	
	private LanguageService languageService;
	private ReportService reportService;
	private ValueService valueService;
	private SessionFactory sessionFactory;
	private Set<String> skipLevels;
	
	@Cacheable("fctCache")
	@Transactional(readOnly = true)
	public FctTable getFctTable(LocationEntity locationEntity, ReportProgram program, FctTarget target, Period period, LocationLevel level, Set<DataEntityType> types) {		
		if (log.isDebugEnabled()) 
			log.debug("getFctTable(period="+period+",location="+locationEntity+",level="+level+",program="+program+",target="+target+")");				
		
		List<FctTargetOption> targetOptions = getTargetOptions(target);
			
		Map<LocationEntity, Map<FctTargetOption, ReportValue>> valueMap = new HashMap<LocationEntity, Map<FctTargetOption, ReportValue>>();
		
		if(targetOptions.isEmpty())
			return new FctTable(valueMap, targetOptions);		
		
		Set<LocationLevel> skips = reportService.getSkipLocationLevels(skipLevels);
		List<LocationEntity> locations = locationEntity.collectTreeWithDataEntities(skips, types);
		
		for (LocationEntity location : locations) {
			Map<FctTargetOption, ReportValue> targetMap = new HashMap<FctTargetOption, ReportValue>();
			for(FctTargetOption targetOption : targetOptions){
				if (log.isDebugEnabled()) log.debug("getting values for sum fct with calculation: "+target.getSum());
				targetMap.put(targetOption, getFctValue(targetOption, location, period, types));
			}
			valueMap.put(location, targetMap);
		}
		
		//sort location map keys
		List<LocationEntity> sortedLocations = new ArrayList<LocationEntity>(valueMap.keySet());
		Collections.sort(sortedLocations, LocationSorter.BY_LEVEL(languageService.getCurrentLanguage()));		
		Map<LocationEntity, Map<FctTargetOption, ReportValue>> sortedValueMap = new LinkedHashMap<LocationEntity, Map<FctTargetOption, ReportValue>>();		
		for (LocationEntity sortedLocation : sortedLocations){		
			sortedValueMap.put(sortedLocation, valueMap.get(sortedLocation));
		}
		
		FctTable fctTable = new FctTable(sortedValueMap, targetOptions);
		if (log.isDebugEnabled()) log.debug("getFctTable(...)="+fctTable);
		return fctTable;
	}

	private List<FctTargetOption> getTargetOptions(FctTarget target) {
		return (List<FctTargetOption>)sessionFactory.getCurrentSession()
				.createCriteria(FctTargetOption.class)
				.add(Restrictions.eq("target", target))
				.list();
	}

	private ReportValue getFctValue(FctTargetOption targetOption, CalculationEntity entity, Period period, Set<DataEntityType> types) {
		String value = null;
		CalculationValue<?> calculationValue = valueService.getCalculationValue(targetOption.getSum(), entity, period, types);
		if (calculationValue != null) value = calculationValue.getValue().getNumberValue().toString();
		return new ReportValue(value);
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
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setSkipLevels(Set<String> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public Set<LocationLevel> getSkipLocationLevels(){
		return reportService.getSkipLocationLevels(skipLevels);
	}
}
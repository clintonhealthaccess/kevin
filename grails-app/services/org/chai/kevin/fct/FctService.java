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
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class FctService {
	private static final Log log = LogFactory.getLog(FctService.class);
	
	private ReportService reportService;
	private ValueService valueService;
	private SessionFactory sessionFactory;
	private Set<String> skipLevels;
	
	@Cacheable("fctCache")
	@Transactional(readOnly = true)
	public FctTable getFctTable(LocationEntity location, ReportProgram program, FctTarget target, Period period, LocationLevel level, Set<DataEntityType> types) {		
		if (log.isDebugEnabled()) 
			log.debug("getFctTable(period="+period+",location="+location+",level="+level+",program="+program+",target="+target+")");				
		
		List<FctTargetOption> targetOptions = getTargetOptions(target);
			
		Map<FctTargetOption, ReportValue> totalMap = new HashMap<FctTargetOption, ReportValue>();
		Map<LocationEntity, Map<FctTargetOption, ReportValue>> valueMap = new HashMap<LocationEntity, Map<FctTargetOption, ReportValue>>();
		
		if(targetOptions.isEmpty())
			return new FctTable(totalMap, valueMap, targetOptions);
		
		for(FctTargetOption targetOption : targetOptions){			
			totalMap.put(targetOption, getFctValue(targetOption, location, period, types));
		}
				
//		List<LocationEntity> children = locationService.getChildrenOfLevel(location, level);
		Set<LocationLevel> skips = reportService.getSkipLocationLevels(skipLevels);		
		List<LocationEntity> children = location.getChildren(skips);
		//TODO
//		List<LocationEntity> locationTree = location.collectTreeWithDataEntities(skips, types);
		
		for (LocationEntity child : children) {
			Map<FctTargetOption, ReportValue> targetMap = new HashMap<FctTargetOption, ReportValue>();
			for(FctTargetOption targetOption : targetOptions){
				if (log.isDebugEnabled()) log.debug("getting values for sum fct with calculation: "+target.getSum());
				targetMap.put(targetOption, getFctValue(targetOption, child, period, types));
			}
			valueMap.put(child, targetMap);
		}
		
		FctTable fctTable = new FctTable(totalMap, valueMap, targetOptions);
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
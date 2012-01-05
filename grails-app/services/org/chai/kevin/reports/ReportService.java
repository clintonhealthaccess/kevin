package org.chai.kevin.reports;

import grails.plugin.springcache.annotations.Cacheable;

import java.text.DecimalFormat;
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
import org.chai.kevin.LocationService;
import org.chai.kevin.OrganisationSorter;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.dsr.DsrObjective;
import org.chai.kevin.dsr.DsrTable;
import org.chai.kevin.dsr.DsrTarget;
import org.chai.kevin.fct.FctObjective;
import org.chai.kevin.fct.FctTable;
import org.chai.kevin.fct.FctTarget;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class ReportService {
	
	private static final Log log = LogFactory.getLog(ReportService.class);
	
	private LocationService locationService;
	private ValueService valueService;
	private DataService dataService;
	private LanguageService languageService;
	private String groupLevel;
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
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
	
	public void setGroupLevel(String groupLevel) {
		this.groupLevel = groupLevel;
	}

	@Cacheable("dsrCache")
	@Transactional(readOnly = true)
	public DsrTable getDsrTable(LocationEntity entity, DsrObjective objective, Period period, Set<DataEntityType> groups) {
		if (log.isDebugEnabled())  log.debug("getDsrTable(period="+period+",entity="+entity+",objective="+objective+")");
						
		List<DataEntity> facilities = locationService.getDataEntities(entity, groups.toArray(new DataEntityType[groups.size()]));
		Map<LocationEntity, List<DataEntity>> organisationMap = getParents(facilities, locationService.findLocationLevelByCode(groupLevel));
		
		Map<DataEntity, Map<DsrTarget, ReportValue>> valueMap = new HashMap<DataEntity, Map<DsrTarget, ReportValue>>();
		List<DsrTarget> targets = objective.getTargets();
		for (DataEntity facility : facilities) {
			Map<DsrTarget, ReportValue> targetMap = new HashMap<DsrTarget, ReportValue>();			
			for (DsrTarget target : targets) {
				targetMap.put(target, getDsrValue(target, facility, period));
			}
			valueMap.put(facility, targetMap);
		}
		
		DsrTable dsrTable = new DsrTable(valueMap, targets, organisationMap);
		if (log.isDebugEnabled()) log.debug("getDsrTable(...)="+dsrTable);
		return dsrTable;
	}

	@Cacheable("fctCache")
	@Transactional(readOnly = true)
	public FctTable getFctTable(LocationEntity entity, FctObjective objective, Period period, LocationLevel level, Set<DataEntityType> groups) {		
		if (log.isDebugEnabled()) log.debug("getFctTable(period="+period+",entity="+entity+",objective="+objective+",level="+level+")");		
		
		List<LocationEntity> organisations = locationService.getChildrenOfLevel(entity, level);
		Map<LocationEntity, List<LocationEntity>> organisationMap = new HashMap<LocationEntity, List<LocationEntity>>();
		LocationLevel groupLevel = locationService.getLevelBefore(level);
		if (groupLevel != null) organisationMap.putAll(getParents(organisations, groupLevel));
		
		List<FctTarget> targets = objective.getTargets();
		Map<FctTarget, ReportValue> totalMap = new HashMap<FctTarget, ReportValue>();				
		for(FctTarget target : targets){			
			totalMap.put(target, getFctValue(target, entity, period, groups));
		}
		Map<LocationEntity, Map<FctTarget, ReportValue>> valueMap = new HashMap<LocationEntity, Map<FctTarget, ReportValue>>();
		for (LocationEntity child : organisations) {
			Map<FctTarget, ReportValue> targetMap = new HashMap<FctTarget, ReportValue>();
			for(FctTarget target : targets){
				if (log.isDebugEnabled()) log.debug("getting values for sum fct with calculation: "+target.getSum());
				targetMap.put(target, getFctValue(target, child, period, groups));
			}
			valueMap.put(child, targetMap);
		}
		
		FctTable fctTable = new FctTable(totalMap, valueMap, targets, organisationMap);
		if (log.isDebugEnabled()) log.debug("getFctTable(...)="+fctTable);
		return fctTable;
	}
	
	private ReportValue getDsrValue(DsrTarget target, DataEntity facility, Period period){
		String value = null;
		
		Set<String> targetUuids = Utils.split(target.getGroupUuidString());
		if (targetUuids.contains(facility.getType().getCode())) {
			DataValue dataValue = valueService.getDataElementValue(target.getDataElement(), facility, period);
			
			if (dataValue != null && !dataValue.getValue().isNull()) {
				// TODO put this in templates ?
				switch (target.getDataElement().getType().getType()) {
				case BOOL:
					if (dataValue.getValue().getBooleanValue()) value = "&#10003;";
					else value = "";
					break;
				case STRING:
					value = dataValue.getValue().getStringValue();
					break;
				case NUMBER:
					value = getFormat(target, dataValue.getValue().getNumberValue().doubleValue());
					break;
				case ENUM:
					String code = target.getDataElement().getType().getEnumCode();
					Enum enume = dataService.findEnumByCode(code);
					if (enume != null) {
						EnumOption option = enume.getOptionForValue(dataValue.getValue().getEnumValue());
						if (option != null) value = languageService.getText(option.getNames());
						else value = dataValue.getValue().getEnumValue();
					}
					else value = "";
					break;
				default:
					value = "";
					break;
				}
			}
		}
		return new ReportValue(value);
	}
	
	private ReportValue getFctValue(FctTarget target, CalculationEntity entity, Period period, Set<DataEntityType> groups) {
		String value = null;
		CalculationValue<?> calculationValue = valueService.getCalculationValue(target.getSum(), entity, period, groups);
		if (calculationValue != null) value = calculationValue.getValue().getNumberValue().toString();
		return new ReportValue(value);
	}
	
	private <T extends CalculationEntity> Map<LocationEntity, List<T>> getParents(List<T> entities, LocationLevel level) {									
		
		Map<LocationEntity, List<T>> organisationMap = new HashMap<LocationEntity, List<T>>();
		
		for (T entity : entities){			
			LocationEntity parentOrganisation = locationService.getParentOfLevel(entity, level);
			if(!organisationMap.containsKey(parentOrganisation)) organisationMap.put(parentOrganisation, new ArrayList<T>());
			organisationMap.get(parentOrganisation).add(entity);
		}
				
		//sort organisation map keys
		List<LocationEntity> sortedEntities = new ArrayList<LocationEntity>(organisationMap.keySet());
		Collections.sort(sortedEntities, OrganisationSorter.BY_NAME(languageService.getCurrentLanguage()));
		
		//sort organisation map values
		Map<LocationEntity, List<T>> sortedEntitiesMap = new LinkedHashMap<LocationEntity, List<T>>();		
		for (LocationEntity entity : sortedEntities){
			List<T> sortedList = organisationMap.get(entity);
			Collections.sort(sortedList, OrganisationSorter.BY_NAME(languageService.getCurrentLanguage()));
			sortedEntitiesMap.put(entity, sortedList);
		}
		
		return sortedEntitiesMap;
	}
	
	private static String getFormat(DsrTarget target, Double value) {
		String format = target.getFormat();
		if (format == null) format = "#";
		
		DecimalFormat frmt = new DecimalFormat(format);
		return frmt.format(value).toString();
	}
}

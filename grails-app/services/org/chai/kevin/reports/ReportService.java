package org.chai.kevin.reports;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.OrganisationSorter;
import org.chai.kevin.value.ValueService;
import org.chai.kevin.dsr.DsrObjective;
import org.chai.kevin.dsr.DsrTable;
import org.chai.kevin.dsr.DsrTarget;
import org.chai.kevin.fct.FctObjective;
import org.chai.kevin.fct.FctTable;
import org.chai.kevin.fct.FctTarget;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;

import org.chai.kevin.data.DataService;
import org.chai.kevin.LanguageService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.reports.ReportValue;
import org.chai.kevin.reports.ReportService;
import org.springframework.transaction.annotation.Transactional;
import grails.plugin.springcache.annotations.Cacheable;

public class ReportService {
	private static final Log log = LogFactory.getLog(ReportService.class);
	
	private OrganisationService organisationService;
	private ValueService valueService;
	private DataService dataService;
	private LanguageService languageService;
	private int groupLevel;
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
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
	
	public void setGroupLevel(int groupLevel) {
		this.groupLevel = groupLevel;
	}

	@Cacheable("dsrCache")
	@Transactional(readOnly = true)
	public DsrTable getDsrTable(Organisation organisation, DsrObjective objective, Period period, Set<String> groupUuids) {
		
		if (log.isDebugEnabled()) 
			log.debug("getDsrTable(period="+period+",organisation="+organisation+",objective="+objective+")");
						
		List<Organisation> facilities = new ArrayList<Organisation>();
		for (String groupUuid : groupUuids) {
			facilities.addAll(organisationService.getFacilitiesOfGroup(organisation, organisationService.getOrganisationUnitGroup(groupUuid)));
		}
		
		Map<Organisation, List<Organisation>> organisationMap = getParents(facilities, groupLevel);
		
		List<DsrTarget> targets = objective.getTargets();
		Map<Organisation, Map<DsrTarget, ReportValue>> valueMap = new HashMap<Organisation, Map<DsrTarget, ReportValue>>();
		
		for (Organisation facility : facilities) {
			organisationService.loadGroup(facility);
			Map<DsrTarget, ReportValue> targetMap = new HashMap<DsrTarget, ReportValue>();			
			for (DsrTarget target : targets) {
				Set<String> targetUuids = Utils.split(target.getGroupUuidString());
				String facilityUuid = facility.getOrganisationUnitGroup().getUuid();
				boolean belongsToTarget = targetUuids.contains(facilityUuid);								
				targetMap.put(target, getDsrValue(belongsToTarget, target, facility, period));
			}
			valueMap.put(facility, targetMap);
		}
		
		DsrTable dsrTable = new DsrTable(valueMap, targets, organisationMap);
		if (log.isDebugEnabled()) log.debug("getDsrTable(...)="+dsrTable);
		return dsrTable;
	}
	
	private ReportValue getDsrValue(boolean belongsToTarget, DsrTarget target, Organisation facility, Period period){
		
		String value = null;
		
		if(!belongsToTarget)
			return new ReportValue(value);
					
		DataValue dataValue = valueService.getDataElementValue(target.getDataElement(), facility.getOrganisationUnit(), period);
		
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
					value = languageService.getText(option.getNames());
				}
				else value = "";
				break;
			default:
				value = "";
				break;
			}
		}		
		return new ReportValue(value);
	}
	
	public FctTable getFctTable(Organisation organisation, FctObjective objective, Period period, OrganisationUnitLevel orgUnitLevel, Set<String> groupUuids) {		
		if (log.isDebugEnabled()) 
			log.debug("getFctTable(period="+period+",organisation="+organisation+",objective="+objective+",orgUnitlevel="+orgUnitLevel.getLevel()+")");		
		
		// TODO get organisations from group uuids only
		List<Organisation> children = organisationService.getChildrenOfLevel(organisation, orgUnitLevel.getLevel());
		
		Map<Organisation, List<Organisation>> organisationMap = getParents(children, orgUnitLevel.getLevel()-1);
		
		Map<FctTarget, ReportValue> totalMap = new HashMap<FctTarget, ReportValue>();				
		List<FctTarget> targets = objective.getTargets();
		for(FctTarget target : targets){			
			totalMap.put(target, getFctValue(target, organisation, period, groupUuids));
		}
		
		Map<Organisation, Map<FctTarget, ReportValue>> valueMap = new HashMap<Organisation, Map<FctTarget, ReportValue>>();
		for (Organisation child : children) {
			Map<FctTarget, ReportValue> targetMap = new HashMap<FctTarget, ReportValue>();
			for(FctTarget target : targets){
				if (log.isDebugEnabled()) log.debug("getting values for sum fct with calculation: "+target.getSum());
				targetMap.put(target, getFctValue(target, child, period, groupUuids));
			}
			valueMap.put(child, targetMap);
		}
		
		FctTable fctTable = new FctTable(totalMap, valueMap, targets, organisationMap);
		if (log.isDebugEnabled()) log.debug("getFctTable(...)="+fctTable);
		return fctTable;
	}
	
	private ReportValue getFctValue(FctTarget target, Organisation organisation, Period period, Set<String> groupUuids) {
		String value = null;
		CalculationValue<?> calculationValue = valueService.getCalculationValue(target.getSum(), organisation.getOrganisationUnit(), period, groupUuids);
		if (calculationValue != null) value = calculationValue.getValue().getNumberValue().toString();
		return new ReportValue(value);
	}
	
	private Map<Organisation, List<Organisation>> getParents(List<Organisation> organisations, Integer level) {									
		
		Map<Organisation, List<Organisation>> organisationMap = new HashMap<Organisation, List<Organisation>>();
		
		for (Organisation organisation : organisations){			
			Organisation parentOrganisation = organisationService.getParentOfLevel(organisation, level);			
			if(!organisationMap.containsKey(parentOrganisation))
				organisationMap.put(parentOrganisation, new ArrayList<Organisation>());
			organisationMap.get(parentOrganisation).add(organisation);
		}
				
		//sort organisation map keys
		List<Organisation> sortedOrganisations = new ArrayList<Organisation>(organisationMap.keySet());
		Collections.sort(sortedOrganisations, OrganisationSorter.BY_LEVEL);
		
		//sort organisation map values
		LinkedHashMap<Organisation, List<Organisation>> sortedOrganisationMap = new LinkedHashMap<Organisation, List<Organisation>>();		
		for (Organisation org : sortedOrganisations){
			List<Organisation> sortedList = organisationMap.get(org);
			Collections.sort(sortedList, OrganisationSorter.BY_LEVEL);
			sortedOrganisationMap.put(org, sortedList);
		}
		
		return sortedOrganisationMap;
	}
	
	private static String getFormat(DsrTarget target, Double value) {
		String format = target.getFormat();
		if (format == null) format = "#";
		
		DecimalFormat frmt = new DecimalFormat(format);
		return frmt.format(value).toString();
	}
}

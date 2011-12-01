package org.chai.kevin.reports;

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
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.OrganisationSorter;
import org.chai.kevin.ValueService;
import org.chai.kevin.dsr.DsrObjective;
import org.chai.kevin.dsr.DsrTable;
import org.chai.kevin.dsr.DsrTarget;
import org.chai.kevin.fct.FctObjective;
import org.chai.kevin.fct.FctTable;
import org.chai.kevin.fct.FctTarget;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.CalculationValue;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;

import org.chai.kevin.DataService;
import org.chai.kevin.LanguageService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.reports.Report;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.value.ExpressionValue;
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
	
	public void setGroupLevel(int froupLevel) {
		this.groupLevel = froupLevel;
	}

	@Cacheable("dsrCache")
	@Transactional(readOnly = true)
	public DsrTable getDsrTable(Organisation organisation, DsrObjective objective, Period period) {
		
		if (log.isDebugEnabled()) 
			log.debug("getDsr(period="+period+",organisation="+organisation+",objective="+objective+")");
		
		List<Organisation> facilities = organisationService.getChildrenOfLevel(organisation, organisationService.getFacilityLevel());		
		
		Map<Organisation, List<Organisation>> orgParentMap = getParents(facilities, groupLevel);
		List<DsrTarget> targets = objective.getTargets();		
		
		Map<Organisation, Map<DsrTarget, Report>> dsrMap = new HashMap<Organisation, Map<DsrTarget, Report>>();
		
		for (Organisation facility : facilities) {
			organisationService.loadGroup(facility);
			
			Map<DsrTarget, Report> orgDsr = new HashMap<DsrTarget, Report>();
			for (DsrTarget target : targets) {
				boolean applies = Utils.split(target.getGroupUuidString()).contains(facility.getOrganisationUnitGroup().getUuid());
				String value = null;
				
				if (applies) {
					ExpressionValue expressionValue = valueService.getValue(target.getExpression(), facility.getOrganisationUnit(), period);
					
					if (expressionValue != null && !expressionValue.getValue().isNull()) {
						// TODO put this in templates ?
						switch (expressionValue.getData().getType().getType()) {
						case BOOL:
							if (expressionValue.getValue().getBooleanValue()) value = "&#10003;";
							else value = "";
							break;
						case STRING:
							value = expressionValue.getValue().getStringValue();
							break;
						case NUMBER:
							value = getFormat(target, expressionValue.getValue().getNumberValue().doubleValue());
							break;
						case ENUM:
							String code = expressionValue.getData().getType().getEnumCode();
							Enum enume = dataService.findEnumByCode(code);
							if (enume != null) {
								EnumOption option = enume.getOptionForValue(expressionValue.getValue().getEnumValue());
								value = languageService.getText(option.getNames());
							}
							else value = "";
							break;
						default:
							value = "";
							break;
						}
					}
					
				}
				orgDsr.put(target, new Report(value));
			}
			dsrMap.put(facility, orgDsr);
		}

		return new DsrTable(targets, dsrMap, orgParentMap);
	}
	
	public FctTable getFctTable(Organisation organisation, FctObjective objective, Period period, OrganisationUnitLevel orgUnitLevel) {		
		if (log.isDebugEnabled()) 
			log.debug("getFct(period="+period+",organisation="+organisation+",objective="+objective+",orgUnitlevel="+orgUnitLevel.getLevel()+")");		
		
		List<Organisation> children = organisationService.getChildrenOfLevel(organisation, orgUnitLevel.getLevel());				
		
		Map<Organisation, List<Organisation>> orgParentMap = new LinkedHashMap<Organisation, List<Organisation>>();
		
		//"total" organisation	
		organisationService.loadParent(organisation);
		organisationService.loadLevel(organisation);		
		Organisation parentOrganisation = organisationService.getParentOfLevel(organisation, organisation.getLevel()-1);
		if(parentOrganisation == null){
			Organisation rootOrganisation = organisationService.getRootOrganisation();
			if(organisation.equals(rootOrganisation))
				parentOrganisation = rootOrganisation;			
		}
		orgParentMap.put(parentOrganisation, new ArrayList<Organisation>());
		orgParentMap.get(parentOrganisation).add(organisation);
		
		orgParentMap.putAll(getParents(children, orgUnitLevel.getLevel()-1));		
		children.add(0, organisation);
		
		List<FctTarget> targets = objective.getTargets();		
		Map<FctTarget, Report> orgFct = new HashMap<FctTarget, Report>();
		Map<Organisation, Map<FctTarget, Report>> fctMap = new HashMap<Organisation, Map<FctTarget, Report>>();		
		for (Organisation child : children) {
			
			organisationService.loadLevel(child);
			organisationService.loadGroup(child);
			OrganisationUnitGroup facilityType = child.getOrganisationUnitGroup();
			
			String value = null;
			
			for(FctTarget target: targets) {
							
				if (facilityType != null) {					
					Set<String> targetFacilityTypes = Utils.split(target.getGroupUuidString());
					String orgFacilityTypeUuid = facilityType.getUuid();										
					if(!targetFacilityTypes.contains(orgFacilityTypeUuid))
						continue;
				}					
				
				if (log.isDebugEnabled()) log.debug("getting values for sum fct with calculation: "+target.getSum());
				
				CalculationValue calculationValue = valueService.getValue(target.getSum(), child.getOrganisationUnit(), period);
				if (calculationValue != null) 
					value = calculationValue.getValue().getStringValue();
				
				orgFct.put(target, new Report(value));
			}											
			fctMap.put(child, orgFct);
		}

		return new FctTable(organisation, targets, fctMap, orgParentMap);
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

package org.chai.kevin.dsr;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LanguageService;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.reports.ReportObjective;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.reports.ReportTarget;
import org.chai.kevin.reports.ReportValue;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.period.Period;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class DsrService {
	private static final Log log = LogFactory.getLog(DsrService.class);
	
	private ReportService reportService;
	private OrganisationService organisationService;
	private ValueService valueService;
	private DataService dataService;
	private LanguageService languageService;
	private int groupLevel;
	
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
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
	public DsrTable getDsrTable(Organisation organisation, ReportObjective objective, Period period, Set<String> groupUuids) {
		
		if (log.isDebugEnabled()) 
			log.debug("getDsrTable(period="+period+",organisation="+organisation+",objective="+objective+")");
						
		List<Organisation> facilities = new ArrayList<Organisation>();
		for (String groupUuid : groupUuids) {
			facilities.addAll(organisationService.getFacilitiesOfGroup(organisation, organisationService.getOrganisationUnitGroup(groupUuid)));
		}
		
		Map<Organisation, List<Organisation>> organisationMap = reportService.getParents(facilities, groupLevel);
		
		List<DsrTarget> targets = reportService.getReportTargets(DsrTarget.class, objective);
		Map<Organisation, Map<DsrTarget, ReportValue>> valueMap = new HashMap<Organisation, Map<DsrTarget, ReportValue>>();
		
		for (Organisation facility : facilities) {
			organisationService.loadGroup(facility);
			Map<DsrTarget, ReportValue> targetMap = new HashMap<DsrTarget, ReportValue>();			
			for (ReportTarget target : targets) {
				DsrTarget dsrTarget = (DsrTarget) target;				
				Set<String> targetUuids = Utils.split(dsrTarget.getGroupUuidString());
				String facilityUuid = facility.getOrganisationUnitGroup().getUuid();
				boolean belongsToTarget = targetUuids.contains(facilityUuid);								
				targetMap.put(dsrTarget, getDsrValue(belongsToTarget, dsrTarget, facility, period));
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
	
	private static String getFormat(DsrTarget target, Double value) {
		String format = target.getFormat();
		if (format == null) format = "#";
		
		DecimalFormat frmt = new DecimalFormat(format);
		return frmt.format(value).toString();
	}
}

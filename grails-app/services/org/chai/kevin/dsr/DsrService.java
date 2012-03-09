package org.chai.kevin.dsr;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LanguageService;
import org.chai.kevin.LocationService;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.reports.ReportValue;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.period.Period;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class DsrService {
	private static final Log log = LogFactory.getLog(DsrService.class);
	
	private LocationService locationService;
	private ReportService reportService;
	private ValueService valueService;
	private DataService dataService;
	private LanguageService languageService;
	private Set<String> skipLevels;
	
	@Cacheable("dsrCache")
	@Transactional(readOnly = true)

	public DsrTable getDsrTable(LocationEntity location, ReportProgram program, Period period, Set<DataEntityType> types, DsrTargetCategory category) {
		if (log.isDebugEnabled())  log.debug("getDsrTable(period="+period+",entity="+location+",program="+program+",types="+types+",category="+category+")");
				
		List<DataLocationEntity> facilities = location.collectDataLocationEntities(null, types);		
		List<DsrTarget> targets = reportService.getReportTargets(DsrTarget.class, program);
		
		Map<DataLocationEntity, Map<DsrTarget, ReportValue>> valueMap = new HashMap<DataLocationEntity, Map<DsrTarget, ReportValue>>();				
		
		List<DsrTargetCategory> targetCategories = new ArrayList<DsrTargetCategory>();
		
		if(facilities.isEmpty() || targets.isEmpty())
			return new DsrTable(valueMap, targets, targetCategories);		
		
		List<DsrTarget> categoryTargets = new ArrayList<DsrTarget>();
		if(category != null){
			for(DsrTarget target : targets){
				if(category.equals(target.getCategory()))
					categoryTargets.add(target);					
			}
			if(!categoryTargets.isEmpty())
				targets = categoryTargets;
		}				
		
		for (DataLocationEntity facility : facilities) {
			Map<DsrTarget, ReportValue> targetMap = new HashMap<DsrTarget, ReportValue>();			
			for (DsrTarget target : targets) {
				targetMap.put(target, getDsrValue(target, facility, period));
			}
			valueMap.put(facility, targetMap);
		}				
			
		targetCategories = getTargetCategories(program);
		
		DsrTable dsrTable = new DsrTable(valueMap, targets, targetCategories);
		if (log.isDebugEnabled()) log.debug("getDsrTable(...)="+dsrTable);
		return dsrTable;
	}

	private ReportValue getDsrValue(DsrTarget target, DataLocationEntity facility, Period period){
		String value = null;
		
		Set<String> targetUuids = Utils.split(target.getTypeCodeString());
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
					else value = "N/A";
					break;
				default:
					value = "N/A";
					break;
				}
			}
			else
				value = "N/A";
		}
		else
			value="N/A";
		
		return new ReportValue(value);
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
	
	private static String getFormat(DsrTarget target, Double value) {
		String format = target.getFormat();
		if (format == null) format = "#";
		
		DecimalFormat frmt = new DecimalFormat(format);
		return frmt.format(value).toString();

	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
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
	
	public void setSkipLevels(Set<String> skipLevels) {
		this.skipLevels = skipLevels;
	}

	public Set<LocationLevel> getSkipLocationLevels(){
		return reportService.getSkipLocationLevels(skipLevels);
	}
}

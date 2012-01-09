package org.chai.kevin.fct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.reports.ReportObjective;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.reports.ReportValue;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;

public class FctService {
	private static final Log log = LogFactory.getLog(FctService.class);
	
	private ReportService reportService;
	private OrganisationService organisationService;
	private ValueService valueService;
	
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}

	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public FctTable getFctTable(Organisation organisation, ReportObjective objective, Period period, OrganisationUnitLevel orgUnitLevel, Set<String> groupUuids) {		
		if (log.isDebugEnabled()) 
			log.debug("getFctTable(period="+period+",organisation="+organisation+",objective="+objective+",orgUnitlevel="+orgUnitLevel.getLevel()+")");		
		
		// TODO get organisations from group uuids only
		List<Organisation> children = organisationService.getChildrenOfLevel(organisation, orgUnitLevel.getLevel());
		
		Map<Organisation, List<Organisation>> organisationMap = reportService.getParents(children, orgUnitLevel.getLevel()-1);
		
		Map<FctTarget, ReportValue> totalMap = new HashMap<FctTarget, ReportValue>();
		List<FctTarget> targets = reportService.getReportTargets(FctTarget.class, objective);
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
}

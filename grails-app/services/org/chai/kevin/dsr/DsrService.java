package org.chai.kevin.dsr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.DataElement;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.LocaleService;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.hisp.dhis.common.AbstractNameableObject;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class DsrService {
	
	private static final Log log = LogFactory.getLog(DsrService.class);
	
	private OrganisationService organisationService;
	private ExpressionService expressionService;
	private LocaleService localeService;

	@Transactional(readOnly = true)
	public DsrTable getDsr(Organisation organisation, DsrObjective objective,
			Period period) {
		log.debug("getDsr(organisation="+organisation+", objective="+objective+", period="+period+")");
		
		organisationService.loadChildren(organisation);
		for (Organisation child : organisation.getChildren()) {
			organisationService.loadChildren(child);
		}
		Organisation parent = organisation;
		while (organisationService.loadParent(parent)) {
			parent = parent.getParent();
		}
		List<Organisation> organisations = organisation.getChildren();
		List<DsrTarget> targets = objective.getTargets();
		
		Map<Organisation, Map<DsrTarget, Dsr>> dsrMap = new HashMap<Organisation, Map<DsrTarget, Dsr>>();
		for (Organisation orgChildren : organisations) {
			Map<DsrTarget, Dsr> orgDsr = new HashMap<DsrTarget, Dsr>();
			for (DsrTarget target : targets) {
				if(target.getCategories().get(localeService.getCurrentLanguage()) == null || target.getCategories().get(localeService.getCurrentLanguage()).equals("")){
					orgDsr.put(
							target,
							new Dsr(
									orgChildren,
									period,
									target,
									expressionService.getValue(
											target.getExpression(),
											period,
											orgChildren,
											new HashMap<DataElement, Object>())));		
				}else{
					orgDsr.put(
							target,
							new Dsr(
									orgChildren,
									period,
									target,
									expressionService.getValue(
											target.getExpression(),
											period,
											orgChildren,
											new HashMap<DataElement, Object>())));	
				}		
			}
			dsrMap.put(orgChildren, orgDsr);
		}
		return new DsrTable(organisations,period,objective,objective.getTargets(), dsrMap);
	}
	public OrganisationService getOrganisationService() {
		return organisationService;
	}

	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}

	public ExpressionService getExpressionService() {
		return expressionService;
	}

	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}

	public void setLocaleService(LocaleService localeService) {
		this.localeService = localeService;
	}
	
}

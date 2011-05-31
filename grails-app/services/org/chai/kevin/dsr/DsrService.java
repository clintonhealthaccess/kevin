package org.chai.kevin.dsr;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.chai.kevin.DataElement;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class DsrService {
   // private Log log = LogFactory.getLog(DsrService.class);
	private OrganisationService organisationService;
	private ExpressionService expressionService;
	private Integer organisationLevel;

	@Transactional(readOnly = true)
	public DsrTable getDsr(Organisation organisation, DsrObjective objective,
			Period period) {

		List<DsrTarget> targets = objective.getTargets();
		if (targets.size()!=0) 
		Collections.sort(targets, new DsrTargetSorter());
		List<Organisation> organisations = organisationService
				.getChildrenOfLevel(organisation,
						organisationLevel.intValue());
		Map<Organisation, Map<DsrTarget, Dsr>> dsrMap = new HashMap<Organisation, Map<DsrTarget, Dsr>>();
		for (Organisation orgChildren : organisations) {
			Map<DsrTarget, Dsr> orgDsr = new HashMap<DsrTarget, Dsr>();
			for (DsrTarget target : targets) {
				orgDsr.put(
						target,
						new Dsr(orgChildren, period, target, expressionService
								.getValue(target.getExpression(), period,
										orgChildren,
										new HashMap<DataElement, Object>())));
			}
			dsrMap.put(orgChildren, orgDsr);
		}
		return new DsrTable(organisation, organisations, period, objective,
				objective.getTargets(), dsrMap);
		
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
	
	public void setOrganisationLevel(Integer organisationLevel) {
		this.organisationLevel = organisationLevel;
	}

}

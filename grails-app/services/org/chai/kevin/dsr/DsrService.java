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
import org.chai.kevin.ValueService;
import org.chai.kevin.value.ExpressionValue;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class DsrService {
	// private Log log = LogFactory.getLog(DsrService.class);
	private OrganisationService organisationService;
	private ValueService valueService;
	
	@Transactional(readOnly = true)
	public DsrTable getDsr(Organisation organisation, DsrObjective objective,
			Period period) {
		List<Organisation> organisations = organisationService.getChildrenOfLevel(organisation,organisationService.getFacilityLevel());
		Map<Organisation, Map<DsrTarget, Dsr>> dsrMap = null;
		List<DsrTarget> targets = null;

		if (objective != null) {
			targets = objective.getTargets();
			Collections.sort(targets, new DsrTargetSorter());
			dsrMap = new HashMap<Organisation, Map<DsrTarget, Dsr>>();
			for (Organisation orgChildren : organisations) {
				Map<DsrTarget, Dsr> orgDsr = new HashMap<DsrTarget, Dsr>();
				for (DsrTarget target : targets) {
					ExpressionValue expressionValue = valueService.getExpressionValue(orgChildren.getOrganisationUnit(), target.getExpression(), period);
					String value = null;
					if (expressionValue != null) value = expressionValue.getValue();
					orgDsr.put(target,new Dsr(orgChildren, period, target, value));
				}
				dsrMap.put(orgChildren, orgDsr);
			}
		}
		return new DsrTable(organisation, organisations, period,
				objective, targets, dsrMap);
	}

	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}

	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
}

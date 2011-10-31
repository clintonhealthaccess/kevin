package org.chai.kevin.fct

import java.util.Collections;
import org.chai.kevin.AbstractController;
import org.chai.kevin.Organisation;
import org.hisp.dhis.period.Period;
import org.chai.kevin.dsr.DsrObjective;
import org.chai.kevin.fct.FctService;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class FctController extends AbstractController {

	FctService fctService;
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("fct.view, params:"+params)

		Period period = getPeriod();
		Organisation organisation = getOrganisation(false);
		FctObjective objective = FctObjective.get(params.int('objective'));
		OrganisationUnitLevel orgUnitLevel = getLevel();

		FctTable fctTable = null;
		if((period != null && objective != null && organisation != null && orgUnitLevel != null)) {
			fctTable = fctService.getFct(organisation, objective, period, orgUnitLevel);
		}
		
		if (log.isDebugEnabled()) log.debug('fct: '+fctTable+" root objective: "+objective)				
		
		Integer organisationLevel = ConfigurationHolder.config.facility.level;
		Set<String> defaultChecked = ConfigurationHolder.config.fct.facility.checked;
		
		[
			fctTable: fctTable,
			currentPeriod: period,
			currentObjective: objective,
			currentOrganisation: organisation,
			periods: Period.list(),
			objectives: FctObjective.list(),
			organisationTree: organisationService.getOrganisationTreeUntilLevel(organisationLevel.intValue()-1),
			checkedFacilities: defaultChecked
		]
	}
}

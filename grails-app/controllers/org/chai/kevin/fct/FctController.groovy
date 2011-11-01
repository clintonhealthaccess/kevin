package org.chai.kevin.fct

import java.util.Collections;
import org.chai.kevin.AbstractController;
import org.chai.kevin.Organisation;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel
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
		OrganisationUnitLevel level = getLevel();

		FctTable fctTable = null;
		if((period != null && objective != null && organisation != null && level != null)) {
			fctTable = fctService.getFct(organisation, objective, period, level);
		}
		
		if (log.isDebugEnabled()) log.debug('fct: '+fctTable+" root objective: "+objective)				
		
		Integer organisationLevel = ConfigurationHolder.config.facility.level;
		Set<String> defaultChecked = ConfigurationHolder.config.fct.facility.checked;
		
		[
			fctTable: fctTable,
			currentPeriod: period,
			currentObjective: objective,
			currentOrganisation: organisation,
			currentLevel: level,
			periods: Period.list(),
			objectives: FctObjective.list(),
			organisationTree: organisationService.getOrganisationTreeUntilLevel(organisationLevel.intValue()-1),
			levels: organisationService.getAllLevels(new Integer(organisationService.getRootOrganisation().getLevel()+1)),
			checkedFacilities: defaultChecked
		]
	}
}

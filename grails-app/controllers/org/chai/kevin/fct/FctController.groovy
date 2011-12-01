package org.chai.kevin.fct

import org.chai.kevin.AbstractController
import org.chai.kevin.Organisation
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.period.Period
import org.hisp.dhis.period.Period;
import org.chai.kevin.reports.ReportService;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class FctController extends AbstractController {

	ReportService reportService;
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("fct.view, params:"+params)

		Period period = getPeriod();
		Organisation organisation = getOrganisation(false);
		FctObjective objective = FctObjective.get(params.int('objective'));
		OrganisationUnitLevel level = getLevel();
		List<OrganisationUnitGroup> facilityTypes = getOrganisationUnitGroups(true);
		
		FctTable fctTable = null;

		if (period != null && objective != null && organisation != null && level != null) {
//			fctTable = fctService.getFct(organisation, objective, period, level, new HashSet(facilityTypes*.uuid));
		fctTable = reportService.getFctTable(organisation, objective, period, level);
		}
		
		if (log.isDebugEnabled()) log.debug('fct: '+fctTable+" root objective: "+objective)				
		
		Integer organisationLevel = ConfigurationHolder.config.facility.level;
		
		[
			fctTable: fctTable,
			currentPeriod: period,
			currentObjective: objective,
			currentOrganisation: organisation,
			currentLevel: level,
			currentFacilityTypes: facilityTypes,
			periods: Period.list(),
			facilityTypes: organisationService.getGroupsForExpression(),
			objectives: FctObjective.list(),
			organisationTree: organisationService.getOrganisationTreeUntilLevel(organisationLevel.intValue()-1),
			levels: organisationService.getAllLevels(new Integer(organisationService.getRootOrganisation().getLevel()+1))
		]
	}
}

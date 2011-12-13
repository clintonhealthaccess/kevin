package org.chai.kevin.fct

import org.chai.kevin.AbstractController
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.period.Period
import org.hisp.dhis.period.Period;
import org.chai.kevin.location.LocationEntity;
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
		LocationEntity entity = LocationEntity.get(params.int('entity'));
		FctObjective objective = FctObjective.get(params.int('objective'));
		OrganisationUnitLevel level = getLevel();
		List<OrganisationUnitGroup> facilityTypes = getOrganisationUnitGroups(true);
		
		FctTable fctTable = null;

		if (period != null && objective != null && entity != null && level != null) {
			fctTable = reportService.getFctTable(entity, objective, period, level, new HashSet(facilityTypes*.uuid));
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
			objectives: FctObjective.list(),
			levels: organisationService.getAllLevels(new Integer(organisationService.getRootOrganisation().getLevel()+1)),
			facilityTypes: locationService.listTypes(),
		    organisationTree: locationService.getRootLocation()
		]
	}
}

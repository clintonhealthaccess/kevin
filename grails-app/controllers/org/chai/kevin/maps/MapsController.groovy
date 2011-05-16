package org.chai.kevin.maps

import org.chai.kevin.AbstractReportController;
import org.chai.kevin.Organisation;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;

class MapsController extends AbstractReportController {

	def mapsService
	
    def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("maps.view, params:"+params)
		
		Period period = getPeriod()
		MapsTarget target = getMapsTarget()
		Organisation organisation = getOrganisation(true)
		
		[
			periods: Period.list(), 
			targets: MapsTarget.list(),
			organisationTree: organisationService.getOrganisationTreeUntilLevel(new Integer(mapsService.getOrganisationLevel()).intValue()-1),
			currentPeriod: period, 
			currentTarget: target,
			currentOrganisation: organisation 
		]
	}
	
	def map = {
		if (log.isDebugEnabled()) log.debug("maps.organisationCoordinates, params:"+params)
		
		Period period = getPeriod()
		Organisation organisation = getOrganisation(true)
		MapsTarget target = getMapsTarget()
		OrganisationUnitLevel level = getOrganisationUnitLevel()
		
		org.chai.kevin.maps.Maps map = mapsService.getMap(period, organisation, level, target);

		if (log.isDebugEnabled()) log.debug("displaying map: "+map)		
		render(contentType:"text/json", text:'{"result":"success","map":'+map.toJson()+"}")
	}
	
}

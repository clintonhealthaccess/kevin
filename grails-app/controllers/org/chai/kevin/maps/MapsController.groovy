package org.chai.kevin.maps

import org.chai.kevin.AbstractReportController;
import org.chai.kevin.Organisation;
import org.hisp.dhis.period.Period;

class MapsController extends AbstractReportController {

	def mapsService
	
    def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("maps.view, params:"+params)
		
		Period period = getPeriod()
		
		[  ]
	}
	
	def map = {
		if (log.isDebugEnabled()) log.debug("maps.organisationCoordinates, params:"+params)
		
		Period period = getPeriod()
		Organisation organisation = getOrganisation(true)
		MapsTarget target = getMapsTarget()
		
		org.chai.kevin.maps.Map map = mapsService.getMap(period, organisation, target);

		if (log.isDebugEnabled()) log.debug("displaying map: "+map)		
		render(contentType:"text/json", text:'{"result":"success","map":'+map.toJson()+"}")
	}
	
}

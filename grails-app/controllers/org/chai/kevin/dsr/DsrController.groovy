package org.chai.kevin.dsr

import java.util.Collections;

import org.chai.kevin.AbstractReportController;
import org.chai.kevin.Organisation;
import org.hisp.dhis.period.Period;
import org.chai.kevin.dsr.DsrObjective;
import org.chai.kevin.dsr.DsrService;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class DsrController extends AbstractReportController {
	
	DsrService dsrService;

	def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {

		def quartzScheduler;

		if (log.isDebugEnabled()) log.debug("dsr.view, params:"+params)
		Period period = getPeriod()
		DsrObjective objective = getStrategicObjectiveDsr()
		Organisation organisation = getOrganisation(true)
		
		def dsrTable = dsrService.getDsr(organisation, objective, period);
		if (log.isDebugEnabled()) log.debug('dsr: '+dsrTable)
		
		Integer organisationLevel = ConfigurationHolder.config.facility.level;
		
		[ 
			dsrTable: dsrTable, 
			periods: Period.list(),
			objectives: DsrObjective.list(),
		    organisationTree: organisationService.getOrganisationTreeUntilLevel(organisationLevel.intValue()-1)
		]
		
	}
	
}

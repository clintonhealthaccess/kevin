package org.chai.kevin.dsr

import org.chai.kevin.AbstractReportController;
import org.chai.kevin.Organisation;
import org.hisp.dhis.period.Period;
import org.chai.kevin.dsr.DsrObjective;
import org.chai.kevin.dsr.DsrService;

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

		//if (log.isInfoEnabled()) log.info("view dsr for period: "+period.id+", objective: "+objective.id+", organisation:"+ organisation.id);
		//redirectIfDifferent(period, objective, organisation)

		def dsrTable = dsrService.getDsr(organisation, objective, period);
		if (log.isDebugEnabled()) log.debug('dsr: '+dsrTable)
		[ dsrTable: dsrTable, periods: Period.list() ]
	}
	
}

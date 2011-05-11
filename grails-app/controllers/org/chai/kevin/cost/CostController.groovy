package org.chai.kevin.cost

import org.chai.kevin.AbstractReportController;
import org.chai.kevin.Organisation;
import org.chai.kevin.cost.CostTableService;
import org.hisp.dhis.period.Period;

class CostController extends AbstractReportController {

	CostTableService costTableService;
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def explain = {
		if (log.isDebugEnabled()) log.debug("cost.explain, params:"+params)
		
		Period period = getPeriod()
		CostTarget target = getCostTarget()
		Organisation organisation = getOrganisation(false)
		
		def explanation = costTableService.getExplanation(period, target, organisation);
		[ explanation: explanation ]
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("cost.view, params:"+params)
		
		Period period = getPeriod()
		Organisation organisation = getOrganisation(false)
		CostObjective objective = getCostObjective()
		
		if (log.isInfoEnabled()) log.info("view cost for period: "+period.id);
//		redirectIfDifferent(period, organisation, objective)
		
		def costTable = costTableService.getCostTable(period, objective, organisation);
		
		if (log.isDebugEnabled()) log.debug('costTable: '+costTable)
		[
			costTable: costTable,
			objectives: CostObjective.list(), 
			periods: Period.list(),
			organisationTree: organisationService.getOrganisationTreeUntilLevel(new Integer(costTableService.getOrganisationLevel()).intValue()-1),
			displayLinkUntil: 3
		]
	}
	
	
	protected def redirectIfDifferent(def period, def organisation, def objective) {
		if (period.id+'' != params['period'] || organisation.id+'' != params['organisation'] || objective.id+'' != params['objective']) {
			if (log.isInfoEnabled()) log.info ("redirecting to action: "+params['action']+", period: "+period.id)
			redirect (controller: 'cost', action: params['action'], params: [period: period.id, objective: objective.id, organisation: organisation.id]);
		}
	}
   
}

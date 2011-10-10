package org.chai.kevin.cost

/*
* Copyright (c) 2011, Clinton Health Access Initiative.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the <organization> nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import org.chai.kevin.AbstractReportController;
import org.chai.kevin.Organisation;
import org.chai.kevin.cost.CostTableService;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.hisp.dhis.period.Period;

class CostController extends AbstractReportController {

	CostTableService costTableService;
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def explain = {
		if (log.isDebugEnabled()) log.debug("cost.explain, params:"+params)
		
		Period period = Period.get(params.int('period'))
		Organisation organisation = organisationService.getOrganisation(params.int('organisation'))
		CostTarget target = CostTarget.get(params.int('objective'));
		
		def explanation = costTableService.getExplanation(period, target, organisation);
		[ explanation: explanation ]
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("cost.view, params:"+params)
		
		Period period = getPeriod()
		Organisation organisation = getOrganisation(false)
		CostObjective objective = CostObjective.get(params.int('objective'));
		
		if (log.isInfoEnabled()) log.info("view cost for period: "+period.id);
		
		def costTable = costTableService.getCostTable(period, objective, organisation);
		Integer organisationLevel = ConfigurationHolder.config.facility.level;
		
		if (log.isDebugEnabled()) log.debug('costTable: '+costTable)
		[
			costTable: costTable,
			objectives: CostObjective.list(), 
			periods: Period.list(),
			organisationTree: organisationService.getOrganisationTreeUntilLevel(organisationLevel.intValue()-1),
		]
	}
	
	
}
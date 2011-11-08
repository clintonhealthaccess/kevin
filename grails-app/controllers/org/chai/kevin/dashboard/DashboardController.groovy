package org.chai.kevin.dashboard

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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.AbstractController;
import org.chai.kevin.Organisation;
import org.chai.kevin.dashboard.Dashboard;
import org.chai.kevin.dashboard.DashboardService;
import org.chai.kevin.dashboard.DashboardPercentage;
import org.chai.kevin.dashboard.PercentageCalculator;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.reports.ReportEntity;
import org.hibernate.cache.ReadWriteCache.Item;
import org.hisp.dhis.aggregation.AggregationService;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;

class DashboardController extends AbstractController {

	AggregationService aggregationService;
	DashboardService dashboardService;
	DashboardObjectiveService dashboardObjectiveService;
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def explain = {
		Period period = Period.get(params.int('period'))
		DashboardEntry entry = getDashboardEntry()
		Organisation organisation = organisationService.getOrganisation(params.int('organisation'))

		def explanation = dashboardService.getExplanation(organisation, entry, period)
		def groups = organisationService.getGroupsForExpression()
		[explanation: explanation, groups: groups]
	}
	
	protected def redirectIfDifferent(def period, def objective, def organisation) {
		if (period.id+'' != params['period'] || objective.id+'' != params['objective'] || organisation.id+'' != params['organisation'] ) {
			if (log.isInfoEnabled()) log.info ("redirecting to action: "+params['action']+",	 period: "+period.id+", objective: "+objective.id+", organisation: "+organisation.id)
			redirect (controller: 'dashboard', action: params['action'], params: [period: period.id, objective: objective.id, organisation: organisation.id]);
		}
	}
	
	private def getDashboardEntry() {
		DashboardEntry entry = DashboardObjective.get(params.int('objective'));
		if (entry == null) {
			entry = DashboardTarget.get(params.int('objective'));
		}
		if (entry == null) {
			entry = dashboardObjectiveService.getRootObjective()
		}
		return entry
	}
	
    def view = {
		if (log.isDebugEnabled()) log.debug("dashboard.view, params:"+params)
		
		Period period = getPeriod()
		DashboardEntry entry = getDashboardEntry()
		Organisation organisation = getOrganisation(true)
		
		if (log.isInfoEnabled()) log.info("view dashboard for period: "+period.id+", objective: "+entry.id+", organisation:"+ organisation.id);
		redirectIfDifferent(period, entry, organisation)
		
		def dashboard = dashboardService.getDashboard(organisation, entry, period);
		if (log.isDebugEnabled()) log.debug('dashboard: '+dashboard)
		Set<String> defaultChecked = ConfigurationHolder.config.dashboard.facility.checked;
		
		if (log.isDebugEnabled()) log.debug("checked by default: "+defaultChecked)
		[ 
			dashboard: dashboard,
			currentPeriod: period,
			currentObjective: entry,
			currentOrganisation: organisation,
			periods: Period.list(), 
			checkedFacilities: defaultChecked 
		]
	}
	
	def getDescription = {
		def objective = null;
		if (NumberUtils.isNumber(params['id'])) {
			objective = DashboardObjective.get(params['id'])
			if (objective == null) DashboardObjective.get(params['id'])
		}
		
		if (objective == null) {
			render(contentType:"text/json") {
				result = 'error'
			}
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: 'description', model: [objective: objective])
			}
		}
	}
	
}

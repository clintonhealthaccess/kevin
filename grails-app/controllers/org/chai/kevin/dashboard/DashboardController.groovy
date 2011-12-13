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

import org.apache.commons.lang.math.NumberUtils
import org.chai.kevin.AbstractController
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.LocationEntity;
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.hisp.dhis.aggregation.AggregationService
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period

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
		CalculationEntity entity = locationService.getCalculationEntity(params.int('entity'), CalculationEntity.class)

		List<OrganisationUnitGroup> facilityTypes = getOrganisationUnitGroups(true);
		
		def info = dashboardService.getExplanation(entity, entry, period, new HashSet(facilityTypes*.uuid))
		def groups = organisationService.getGroupsForExpression()
		[info: info, groups: groups, entry: entry]
	}
	
	protected def redirectIfDifferent(def period, def objective, def location) {
		if (period.id+'' != params['period'] || objective.id+'' != params['objective'] || organisation.id+'' != params['entity'] ) {
			if (log.isInfoEnabled()) log.info ("redirecting to action: "+params['action']+",	 period: "+period.id+", objective: "+objective.id+", location: "+location.id)
			redirect (controller: 'dashboard', action: params['action'], params: [period: period.id, objective: objective.id, location: location.id]);
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
		LocationEntity location = LocationEntity.get(params.int('entity'))
		
		if (log.isInfoEnabled()) log.info("view dashboard for period: "+period.id+", objective: "+entry.id+", entity:"+ location.id);
		redirectIfDifferent(period, entry, location)
		
		List<OrganisationUnitGroup> facilityTypes = getOrganisationUnitGroups(true);
		
		def dashboard = dashboardService.getDashboard(location, entry, period, new HashSet(facilityTypes*.uuid));
		if (log.isDebugEnabled()) log.debug('dashboard: '+dashboard)
		
		[ 
			dashboard: dashboard,
			currentPeriod: period,
			currentObjective: entry,
			currentOrganisation: organisation,
			currentFacilityTypes: facilityTypes,
			periods: Period.list(),
			facilityTypes: organisationService.getGroupsForExpression()
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

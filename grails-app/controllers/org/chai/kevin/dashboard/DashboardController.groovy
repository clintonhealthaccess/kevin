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
import org.chai.kevin.AbstractReportController;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.Translatable;
import org.chai.kevin.Organisation;
import org.chai.kevin.ProgressListener;
import org.chai.kevin.dashboard.Dashboard;
import org.chai.kevin.dashboard.DashboardService;
import org.chai.kevin.dashboard.DashboardPercentage;
import org.chai.kevin.dashboard.PercentageCalculator;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.data.DataElement;
import org.hibernate.cache.ReadWriteCache.Item;
import org.hisp.dhis.aggregation.AggregationService;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;

class DashboardController extends AbstractReportController {

	AggregationService aggregationService;
	DashboardService dashboardService;

	def quartzScheduler;
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def explain = {
		Period period = getPeriod()
		Translatable objective = getObjective()
		Organisation organisation = getOrganisation(false)

		def explanation = dashboardService.getExplanation(organisation, objective, period)
		def groups = new GroupCollection(OrganisationUnitGroup.list())
		[explanation: explanation, groups: groups]
	}
	
	protected def redirectIfDifferent(def period, def objective, def organisation) {
		if (period.id+'' != params['period'] || objective.id+'' != params['objective'] || organisation.id+'' != params['organisation'] ) {
			if (log.isInfoEnabled()) log.info ("redirecting to action: "+params['action']+",	 period: "+period.id+", objective: "+objective.id+", organisation: "+organisation.id)
			redirect (controller: 'dashboard', action: params['action'], params: [period: period.id, objective: objective.id, organisation: organisation.id]);
		}
	}
	
    def view = {
		if (log.isDebugEnabled()) log.debug("dashboard.view, params:"+params)
		
		Period period = getPeriod()
		Translatable objective = getStrategicObjective()
		Organisation organisation = getOrganisation(true)
		
		if (log.isInfoEnabled()) log.info("view dashboard for period: "+period.id+", objective: "+objective.id+", organisation:"+ organisation.id);
		redirectIfDifferent(period, objective, organisation)
		
		def dashboard = dashboardService.getDashboard(organisation, objective, period);
		if (log.isDebugEnabled()) log.debug('dashboard: '+dashboard)
		Set<String> defaultChecked = ConfigurationHolder.config.dashboard.facility.checked;
		
		if (log.isDebugEnabled()) log.debug("checked by default: "+defaultChecked)
		[ dashboard: dashboard, periods: Period.list(), checkedFacilities: defaultChecked ]
	}
	
	def refresh = {
		if (log.isDebugEnabled()) log.debug("dashboard.refresh, params:"+params)
		
//		Period period = getPeriod()
//		Translatable objective = getStrategicObjective()
//		Organisation organisation = getOrganisation(false)
		
//		if (log.isInfoEnabled()) log.info("refresh dashboard for period: "+period.id+", objective: "+objective.id+", organisation:"+ organisation.id);
//		def runningJob = getRunningJob(period, organisation, objective)
//		if (runningJob == null) {
			ExpressionJob.triggerNow()
//		}
//		else {
//			if (log.isInfoEnabled()) log.info('already running')
//		}
		
		redirect (controller: 'dashboard', action: 'view', params: params);
	}
	
//	def progress = {
//		[ period: params['period'], objective: params['objective'], organisation: params['organisation'] ]
//	}
	
	def cancel = {
		if (log.isDebugEnabled()) log.debug("dashboard.progress, params:"+params)
		
		Period period = getPeriod()
		Translatable objective = getStrategicObjective()
		Organisation organisation = getOrganisation(false)
		
		def runningJob = getRunningJob(period, organisation, objective)
		if (runningJob != null) {
			runningJob.jobInstance.interrupt();
		}
		
		redirect (controller: 'dashboard', action: 'view', params: params);
	}
	
//	def progressInc = {
//		if (log.isDebugEnabled()) log.debug("dashboard.progress, params:"+params)
//		
//		Period period = getPeriod()
//		Translatable objective = getStrategicObjective()
//		Organisation organisation = getOrganisation(false)
//		
//		def runningJob = getRunningJob(period, organisation, objective)
//		if (runningJob != null) {
//			render(contentType:"text/json") {
//				job = 'found'
//				total = runningJob.mergedJobDataMap.progress.total
//				current = runningJob.mergedJobDataMap.progress.current
//			}
//		}
//		else {
//			render(contentType:"text/json") {
//				job = 'not_found'
//			}
//		}
//	}
	
//	private def getRunningJob(def period, def organisation, def objective) {
//		return quartzScheduler.currentlyExecutingJobs.find {
//			def jobPeriod = it.mergedJobDataMap.get('period')
//			def jobOrganisation = it.mergedJobDataMap.get('organisation')
//			def jobObjective = it.mergedJobDataMap.get('objective')
//			
//			jobPeriod == period.id && jobOrganisation == organisation.id && jobObjective == objective.id
//		}
//	}
	
//	def refreshAll = {
//		if (log.isDebugEnabled()) log.debug("dashboard.refreshAll, params:"+params)
//		
//		Period period = getPeriod()
//		if (log.isInfoEnabled()) log.info("refresh dashboard for period: "+period.id);
//		
//		dashboardService.refreshEntireDashboard(period);
//		redirect (controller: 'dashboard', action: 'view', params: params);
//	}
	
//	def flush = {
//		DashboardPercentage.list().each { it.delete() }
//		redirect (controller: 'dashboard', action: 'view', params: params);
//	}
	
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

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
import org.chai.kevin.LocationService
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.Translation;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.reports.ReportObjective
import org.chai.kevin.reports.ReportService
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.hisp.dhis.period.Period

class DashboardController extends AbstractController {
	
	DashboardService dashboardService;
	
	def index = {
		redirect (action: 'view', params: params)
	}
		
	protected def redirectIfDifferent(def period, def objective, def location) {
		if (period.id+'' != params['period'] || objective.id+'' != params['objective'] || location.id+'' != params['location'] ) {
			
			if (log.isInfoEnabled()) {
				log.info ("redirecting to action: "+params['action']+
					", period: "+period.id+
					", objective: "+objective.id+
					", location: "+location.id);
			}
			
			redirect (controller: 'dashboard', action: params['action'],
				params: [period: period.id, objective: objective.id, location: location.id]);
	
		}
	}
	
	private def getDashboardEntity(def reportObjective) {		
		DashboardEntity entity = dashboardService.getDashboardObjective(reportObjective)
		if (entity == null) {
			entity = dashboardService.getDashboardObjective(reportService.getRootObjective())
		}
		return entity
	}
	
    def view = {
		if (log.isDebugEnabled()) log.debug("dashboard.view, params:"+params)
		
		def programDashboard = null		
		def locationDashboard = null
		
		Period period = getPeriod()							
		ReportObjective objective = getObjective()
		DashboardEntity dashboardEntity = getDashboardEntity(objective)		
		LocationEntity location = getLocation()
		List<DataEntityType> locationTypes = getLocationTypes()
		
		if (period != null && objective != null && dashboardEntity != null && location != null && locationTypes != null) {			
			if (log.isInfoEnabled()){
				log.info("compare dashboard for period: "+period.id+
					", location: "+location.id+
					", objective:"+objective.id);
			}
			redirectIfDifferent(period, objective, location)
			
			programDashboard = dashboardService.getProgramDashboard(location, objective, period, new HashSet(locationTypes));
			locationDashboard = dashboardService.getLocationDashboard(location, objective, period, new HashSet(locationTypes), false);
		}
		if (log.isDebugEnabled()){
			 log.debug('program dashboard: '+programDashboard)
			 log.debug('location dashboard: '+locationDashboard)
		}		
		
		[
			programDashboard: programDashboard,
			locationDashboard: locationDashboard,			
			currentPeriod: period,
			dashboardEntity: dashboardEntity,
			currentObjective: objective,
			currentTarget: DashboardTarget.class,
			currentLocation: location,
			currentLocationTypes: locationTypes
		]
	}
	
	def compare = {
		if (log.isDebugEnabled()) log.debug("dashboard.compare, params:"+params)								
		
		Period period = getPeriod()	
		ReportObjective objective = getObjective()
		DashboardEntity dashboardEntity = getDashboardEntity(objective)		
		LocationEntity location = getLocation()
		List<DataEntityType> locationTypes = getLocationTypes()
		
		def dashboard = null
		if (period != null && objective != null && dashboardEntity != null && location != null && locationTypes != null) {			
			
			if (log.isInfoEnabled()){
				log.info("compare dashboard for period: "+period.id+
					", location: "+location.id+
					", objective:"+objective.id+
					", dashboardEntity: " + dashboardEntity.id);
			}
			redirectIfDifferent(period, objective, location)
			
			def table = (String) params.get("table")			
			if(table == 'program')
				dashboard = dashboardService.getProgramDashboard(location, objective, period, new HashSet(locationTypes));
			if(table == 'location')
				dashboard = dashboardService.getLocationDashboard(location, objective, period, new HashSet(locationTypes), true);
						
			if (log.isDebugEnabled()) log.debug('compare dashboard: '+dashboard)

			render(contentType:"text/json") {
				status = 'success'	
				compareValues = array {
					dashboard.dashboardEntities.each{ entity ->
						obj (
							id: entity.id,
							value: dashboard.getPercentage(location, entity)
							)
					}
				}			
			}
		}		
		else {
			render(contentType:"text/json") {
				status = 'error'
			}
		}
	}
	
}

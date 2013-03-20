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

import org.chai.kevin.AbstractController
import org.chai.kevin.Period
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.reports.ReportService
import org.chai.kevin.reports.ReportService.ReportType
import org.chai.kevin.util.Utils
import org.chai.location.CalculationLocation
import org.chai.location.DataLocation
import org.chai.location.DataLocationType
import org.chai.location.Location
import org.chai.location.LocationService

class DashboardController extends AbstractController {
	
	DashboardService dashboardService;	
	
	def index = {
		redirect (action: 'view', params: params)
	}		
	
	private def getDashboardEntity(def program) {		
		DashboardEntity entity = DashboardTarget.get(params.int('dashboardEntity'))
		if(entity != null){
			// reset the entity if it doesn't belong to the right program
			if(!entity.program.equals(program)) 
				entity = null
		}
		
		// set the entity to the program if null
		if(entity == null) 
			entity = dashboardService.getDashboardProgram(program)

		return entity
	}
	
    def view = {
		if (log.isDebugEnabled()) log.debug("dashboard.view, params:"+params)		
		
		// entities from params
		Period period = getPeriod()									
		ReportProgram program = getProgram(DashboardTarget.class)
		CalculationLocation location = getCalculationLocation()
		// if (log.isDebugEnabled()) log.debug("dashboard.view, location:"+location)
		Set<DataLocationType> dataLocationTypes = getLocationTypes()
		ReportType reportType = getReportType()
		
		// other information we need in the view
		DashboardEntity dashboardEntity = getDashboardEntity(program)
		if (log.isDebugEnabled()) log.debug("dashboard.view, dashboardEntity:"+dashboardEntity)
		def locationSkipLevels = dashboardService.getSkipLocationLevels();
		
		def redirected = false
		// we check if we need to redirect, but only when some of the high level filters are null
		if (period != null && program != null && location != null && dashboardEntity != null) {
			// building params for redirection checks
			def reportParams = ['period':period.id, 'program':program.id, 'location':location.id, 
								'dataLocationTypes':dataLocationTypes.collect{ it.id }.sort(),
								'dashboardEntity': dashboardEntity.id,
								'reportType':reportType.toString().toLowerCase()]
			
			// we check if we should redirect
			def newParams = redirectIfDifferent(reportParams)
			
			if(newParams != null && !newParams.empty) {
				redirected = true
				if (log.isDebugEnabled()) log.debug('dashboard.view, redirecting, parems: '+newParams);
				redirect(controller: 'dashboard', action: 'view', params: newParams)
			}
		}
		
		if (!redirected) {
			def dashboard
			if (dashboardEntity != null) {
				dashboard = dashboardService.getDashboard(location, program, dashboardEntity, period, dataLocationTypes, false);
			}
						
			if (log.isDebugEnabled()) log.debug('dashboard: '+dashboard+", root program: "+program+", root location: "+location)
			
			[
				currentPeriod: period,
				currentProgram: program,
				currentLocation: location,
				currentLocationTypes: dataLocationTypes,
				currentView: reportType,
				
				dashboardTable: dashboard,
				selectedTargetClass: DashboardTarget.class,
				dashboardEntity: dashboardEntity,
				locationSkipLevels: locationSkipLevels			
			]
    	}
	}
	
	def compare = {
		if (log.isDebugEnabled()) log.debug("dashboard.compare, params:"+params)
		
		// entities from params
		Period period = getPeriod()	
		ReportProgram program = getProgram(DashboardTarget.class)
		CalculationLocation location = getCalculationLocation()
		if (log.isDebugEnabled()) log.debug("dashboard.view, location:"+location)
		Set<DataLocationType> dataLocationTypes = getLocationTypes()
		
		// other information we need in the view
		DashboardEntity dashboardEntity = getDashboardEntity(program)
		
		if (period != null && program != null && location != null && dataLocationTypes != null && dashboardEntity != null) {			
			if (log.isDebugEnabled()) log.debug("compare dashboard for dashboardEntity: "+dashboardEntity+", root program: "+program+", root location: "+location)
			
			def dashboard = dashboardService.getDashboard(location, program, dashboardEntity, period, dataLocationTypes, true);
			if (log.isDebugEnabled()) log.debug('compare dashboard: '+dashboard)
			
			def table = (String) params.get("table")
			render(contentType:"text/json") {
				status = 'success'	
				compareValues = array {
					if(table == 'program') {
						dashboard.getIndicators(dashboardEntity).each{ entity ->
							obj (
								id: entity.id,
								value: dashboard.getPercentage(location, entity).numberValue
							)
						}
					}
					if(table == 'location') {
						[
							obj (
								id: dashboardEntity.id,
								value: dashboard.getPercentage(location, dashboardEntity).numberValue
							)
						]
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

	def map = {
		if (log.isDebugEnabled()) log.debug("dashboard.map, params:"+params)
		return getFosaLocations();
	}
	
}

package org.chai.kevin;

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
import org.apache.shiro.SecurityUtils
import org.chai.location.DataLocationType
import org.chai.location.Location
import org.chai.location.LocationLevel
import org.chai.location.LocationService;
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.reports.ReportService
import org.chai.kevin.security.User;
import org.chai.kevin.survey.Survey
import org.chai.kevin.survey.SurveyProgram
import org.chai.kevin.survey.SurveyPageService
import org.chai.kevin.survey.SurveySection
import org.chai.kevin.survey.summary.SurveySummaryPage;
import org.chai.kevin.util.Utils;
import org.chai.kevin.util.Utils.ReportType
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

public abstract class AbstractController {

	LanguageService languageService;
	ReportService reportService;
	LocationService locationService;
	
	def getTargetURI() {
		return params.targetURI?: "/"
	}
	
	def getCurrentUser() {
		if (log.isDebugEnabled()) log.debug('getCurrentUser()')
		return User.findByUuid(SecurityUtils.subject.principal, [cache: true])
	}

	def getPeriod() {
		Period period = Period.get(params.int('period'))
		if (period == null)  period = Period.findAll()[ConfigurationHolder.config.site.period]
		return period
	}
	
	def getProgram(def clazz){		
		ReportProgram program = ReportProgram.get(params.int('program'))		
		if(program == null) program = reportService.getRootProgram()			
		if(clazz != null){
			def targets = reportService.collectReportTargets(clazz, program);
			if (targets.isEmpty()) program = reportService.getRootProgram()
		}
		return program
	}
	
	def getLocation(){
		Location location = Location.get(params.int('location'))
		//TODO add skips and types to method
		//TODO if location != null, get location tree, and if the location tree doesn't contain the location, return root location
		if (location == null)
			location = locationService.getRootLocation()
		return location
	}
	
	public Set<DataLocationType> getLocationTypes() {
		Set<DataLocationType> dataLocationTypes = new HashSet<DataLocationType>()
		if (params.list('dataLocationTypes') != null && !params.list('dataLocationTypes').empty) {
			def types = params.list('dataLocationTypes')
			dataLocationTypes.addAll(types.collect{ NumberUtils.isNumber(it as String) ? DataLocationType.get(it) : null } - null)
		}		
		
		if(dataLocationTypes == null || dataLocationTypes.empty){
			dataLocationTypes.addAll(ConfigurationHolder.config.site.datalocationtype.checked.collect{ DataLocationType.findByCode(it) } - null)
		}
		
		return dataLocationTypes
	}
	
	public Set<Period> getPeriods() {
		Set<Period> periods =null
		if (params.list('currentPeriods') != null && !params.list('currentPeriods').empty) {
			def selectedPeriods = params.list('currentPeriods')
			periods = new HashSet<Period>(selectedPeriods.collect{ NumberUtils.isNumber(it as String) ? Periods.get(it) : null } - null)
		}
		else {
			periods = new HashSet<Period>().add(Period.list()[Period.list().size()-1]);
		}
		return periods
		
	}
	
	public ReportType getReportType(){
		def reportType = null
		if(params['reportType'] != null && !params['reportType'].empty){
			reportType = params['reportType']
			try{
				reportType = ReportType.valueOf(ReportType.class, reportType.toUpperCase())
			}
			catch(IllegalArgumentException ex){
				reportType = ReportType.TABLE
			}			
		}
		else{
			//TODO config.site.blah
			reportType = ReportType.TABLE
		}
		return reportType;
	}
	
	def adaptParamsForList() {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		params.offset = params.offset ? params.int('offset'): 0
	}	
	
	protected def redirectIfDifferent(def redirectParams) {
		if (log.isDebugEnabled()) {
			log.debug("redirectIfDifferent(redirectParams="+redirectParams+")")
			log.debug("request params: "+params)
		}
				
		boolean redirect = false
		def newParams = [:]
		for(def param : redirectParams){
			def key = param.key
			def redirectValue = redirectParams[key].toString()
			
			// url value to compare with
			def urlValue = null
			if(key == 'dataLocationTypes' || key == 'indicators') 
				urlValue = params.list(key).toString()
			else 
				urlValue = params[key].toString()
			
			if(redirectValue != null && urlValue != redirectValue)
				redirect = true
			if(redirectParams[key] != null) newParams.put(key, redirectParams[key])
		}
		
		if(!redirect) return null				
		if(redirect){
			if (log.isInfoEnabled()) {
				log.info ("redirecting to controller: "+ params['controller']+
					", action: "+params['action']+
					", period: "+newParams['period']+
					", program: "+newParams['program']+
					", location: "+newParams['location']+
					", dataLocationTypes: "+newParams['dataLocationTypes']+
					", reportType: "+newParams['reportType']+
					", indicators: "+newParams['indicators']);
			}
			return newParams;
		}
	}
}

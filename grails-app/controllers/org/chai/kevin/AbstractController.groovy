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

import org.apache.shiro.SecurityUtils;
import org.chai.kevin.dsr.DsrTargetCategory
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location
import org.chai.kevin.location.LocationLevel
import org.chai.kevin.LocationService

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.reports.ReportService
import org.chai.kevin.security.User;
import org.chai.kevin.survey.Survey
import org.chai.kevin.survey.SurveyProgram
import org.chai.kevin.survey.SurveyPageService
import org.chai.kevin.survey.SurveySection
import org.chai.kevin.survey.summary.SurveySummaryPage;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

public abstract class AbstractController {

	ReportService reportService;
	LocationService locationService;
	
	def getTargetURI() {
		return params.targetURI?: "/"
	}
	
	def getUser() {
		return User.findByUuid(SecurityUtils.subject.principal)
	}

	def getPeriod() {
		Period period = Period.get(params.int('period'))
		if (period == null)  period = Period.findAll()[ConfigurationHolder.config.site.period]
		return period
	}
	
	def getProgram(def clazz){		
		ReportProgram program = ReportProgram.get(params.int('program'))		
		if(program == null)
			program = reportService.getRootProgram()			
		if(clazz != null){
			def programTree = reportService.getProgramTree(clazz).asList()
			if(!programTree.contains(program))
				program = reportService.getRootProgram()
		}
			
		return program
	}
	
	def getLocation(){
		Location location = Location.get(params.int('location'))
		if (location == null) location = locationService.getRootLocation()
		return location
	}		
	
	public Set<DataLocationType> getLocationTypes() {
		Set<DataLocationType> types = null
		if (params.list('dataLocationTypes') != null && !params.list('dataLocationTypes').empty) {
			def dataLocationTypes = params.list('dataLocationTypes')
			types = new HashSet<DataLocationType>(dataLocationTypes.collect{ DataLocationType.get(it) })
		}
		else {
			types = new HashSet<DataLocationType>(ConfigurationHolder.config.site.datalocationtype.checked.collect {DataLocationType.findByCode(it)})
		}
		return types
	}
	
	public Set<Period> getPeriods() {
		Set<Period> periods =null
		if (params.list('currentPeriods') != null && !params.list('currentPeriods').empty) {
			def selectedPeriods = params.list('currentPeriods')
			periods = new HashSet<Period>(selectedPeriods.collect{ Periods.get(it) })
		}
		else {
			periods = new HashSet<Period>().add(Period.list()[Period.list().size()-1]);
		}
		return periods
		
	}
	def getLevel(){
		LocationLevel level = null
		level = LocationLevel.get(params.int('level'));
		return level
	}
	
	def adaptParamsForList() {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		params.offset = params.offset ? params.int('offset'): 0
	}

}

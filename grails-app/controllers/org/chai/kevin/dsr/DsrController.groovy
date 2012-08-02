package org.chai.kevin.dsr

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
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import java.util.Collections;

import org.apache.jasper.compiler.Node.ParamsAction;
import org.chai.kevin.AbstractController;
import org.chai.kevin.LanguageService
import org.chai.kevin.LocationService
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel
import org.chai.kevin.Period;
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.util.Utils.ReportType;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class DsrController extends AbstractController {

	LanguageService languageService;
	DsrService dsrService;
	
	public DsrTargetCategory getDsrTargetCategory(def program){
		def dsrTargetCategory = null
			
		if(params.int('dsrCategory') != null){
			dsrTargetCategory = DsrTargetCategory.get(params.int('dsrCategory'))
		}
		
		if(dsrTargetCategory == null){
			def categories = dsrService.getTargetCategories(program)
			if(categories != null && !categories.empty){
				Collections.sort(categories);
				dsrTargetCategory = categories.first()
			}
		}
		
		return dsrTargetCategory
	}	
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("dsr.view, params:"+params)				
		
		Period period = getPeriod()
		ReportProgram program = getProgram(DsrTarget.class)
		Location location = getLocation()
		Set<DataLocationType> dataLocationTypes = getLocationTypes()
		DsrTargetCategory dsrCategory = getDsrTargetCategory(program)
		
		ReportType reportView = getReportType()	
		def viewSkipLevels = dsrService.getSkipViewLevels(reportView)
		
		def locationSkipLevels = dsrService.getSkipLocationLevels()
		def locationTree = location.collectLocationTreeWithData(locationSkipLevels, dataLocationTypes, false).asList()
		
		def reportParams = ['period':period.id, 'program':program.id, 'location':location.id, 
							'dataLocationTypes':dataLocationTypes.collect{ it.id }.sort(), 							
							'dsrCategory':dsrCategory?.id,
							'reportType':reportView.toString().toLowerCase()]
		def newParams = redirectIfDifferent(reportParams)
		
		if(newParams != null && !newParams.empty) { 
			redirect(controller: 'dsr', action: 'view', params: newParams)
		}
		else {
			def dsrTable = null
			if (dsrCategory != null)
				dsrTable = dsrService.getDsrTable(location, program, period, dataLocationTypes, dsrCategory, reportView);			
			
			if (log.isDebugEnabled()) log.debug('dsr: '+dsrTable+" root program: "+program+", root location: "+location)
			
			[
				dsrTable: dsrTable,
				currentCategory: dsrCategory,
				currentPeriod: period,
				currentProgram: program,
				selectedTargetClass: DsrTarget.class,
				currentLocation: location,
				locationTree: locationTree,
				currentLocationTypes: dataLocationTypes,
				locationSkipLevels: locationSkipLevels,
				currentView: reportView,
				viewSkipLevels: viewSkipLevels
			]
		}
	}
}
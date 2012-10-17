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

import org.apache.commons.lang.math.NumberUtils;
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
			
			// reset the category if it doesn't belong to the right program
			if(dsrTargetCategory != null){
				def categories = dsrService.getTargetCategories(program)
				if(categories != null && !categories.empty){
					if(!categories.contains(dsrTargetCategory))
						dsrTargetCategory = null
				}								
			}
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
	
	public Set<DsrTarget> getDsrIndicators(def reportType, def category, def program){
		Set<DsrTarget> dsrIndicators = null
		
		if(reportType == ReportType.TABLE) return dsrIndicators
		
		dsrIndicators = new HashSet<DsrTarget>()			
		if(params.list('indicators') != null && !params.list('indicators').empty){
			def indicators = params.list('indicators')
			dsrIndicators.addAll(indicators.collect{ NumberUtils.isNumber(it as String) ? DsrTarget.get(it) : null } - null)
			
			// reset the indicators if any of them don't belong to the right category
			if(dsrIndicators != null){
				for(DsrTarget dsrIndicator in dsrIndicator){
					if(!dsrIndicator.category.equals(category)){
						dsrIndicators = null
						break;
					}
				}
			}
		}
		
		if(dsrIndicators == null || dsrIndicators.empty){
			if(category == null) category = getDsrTargetCategory(program);
			if(category != null){
				def targets = category.getTargetsForProgram(program)
				if(targets != null && !targets.empty){
					dsrIndicators.addAll(targets.sort().first())
				}
			}			
		}
		
		return dsrIndicators
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
		
		ReportType reportType = getReportType()
		def mapSkipLevels = dsrService.getSkipViewLevels(reportType)
		
		Set<DsrTarget> dsrIndicators = getDsrIndicators(reportType, dsrCategory, program)
		
		def locationSkipLevels = dsrService.getSkipLocationLevels()
		def locationTree = location.collectLocationTreeWithData(locationSkipLevels, dataLocationTypes, false).asList()
		
		def reportParams = ['period':period.id, 'program':program.id, 'location':location.id, 
							'dataLocationTypes':dataLocationTypes.collect{ it.id }.sort(), 							
							'dsrCategory':dsrCategory?.id,							
							'reportType':reportType.toString().toLowerCase()]
		if(dsrIndicators != null) reportParams['indicators'] = dsrIndicators.collect{ it.id }
		
		def newParams = redirectIfDifferent(reportParams)
		
		if(newParams != null && !newParams.empty) {
			redirect(controller: 'dsr', action: 'view', params: newParams)
		}
		else {
			def dsrTable = null
			if (dsrCategory != null)
				dsrTable = dsrService.getDsrTable(location, program, period, dataLocationTypes, dsrCategory, reportType);			
			
			if (log.isDebugEnabled()) log.debug('dsr: '+dsrTable+" root program: "+program+", root location: "+location)
			
			[
				dsrTable: dsrTable,
				currentCategory: dsrCategory,
				currentIndicators: dsrIndicators,
				currentPeriod: period,
				currentProgram: program,
				selectedTargetClass: DsrTarget.class,
				currentLocation: location,
				locationTree: locationTree,
				currentLocationTypes: dataLocationTypes,
				locationSkipLevels: locationSkipLevels,
				currentView: reportType,
				mapSkipLevels: mapSkipLevels
			]
		}
	}
}
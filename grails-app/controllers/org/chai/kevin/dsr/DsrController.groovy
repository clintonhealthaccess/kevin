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
import org.apache.commons.lang.math.NumberUtils
import org.chai.kevin.AbstractController
import org.chai.kevin.LanguageService
import org.chai.kevin.Period
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.reports.ReportService
import org.chai.kevin.reports.ReportService.ReportType
import org.chai.kevin.util.Utils
import org.chai.location.DataLocationType
import org.chai.location.Location
import org.chai.location.LocationService

class DsrController extends AbstractController {

	def languageService;
	def dsrService;
	def reportExportService;
	
	/**
	 * This returns the dsr target category passed as a parameter if it belongs to the
	 * given program. Otherwise, it returns the first dsr target category that has targets for the program.
	 * 
	 * @param program
	 * @return
	 */
	public DsrTargetCategory getDsrTargetCategory(def program){
		DsrTargetCategory dsrTargetCategory = null
		
		if (program == null) return null	
		
		if(params.int('dsrCategory') != null) {
			try {
				dsrTargetCategory = DsrTargetCategory.get(params.int('dsrCategory'))
			} catch (Exception e) {
				dsrTargetCategory = null
			}
			if (log.debugEnabled) log.debug('found DsrTargetCategory in params: '+dsrTargetCategory)
			
			// reset the category if it doesn't belong to the right program
			if(dsrTargetCategory != null){
				if(!dsrTargetCategory.program.equals(program))
					dsrTargetCategory = null
			}	
		}
		
		// set the target to the first of the program if null
		// TODO this crashes if there are no categoires for the program
		if(dsrTargetCategory == null){
			def categories = dsrService.getDsrCategoriesWithTargets(program)
			if(categories != null && !categories.empty){
				categories.sort({it.order})
				dsrTargetCategory = categories.first()
			}
			else{
				if (log.isDebugEnabled()) {
					log.debug("dsr.view, program:"+program+", dsrTargetCategory:"+dsrTargetCategory+" must have at least 1 dsrTarget")
				}
			}
		}
		
		if (log.isDebugEnabled()) log.debug("dsr.view, program:"+program+", getDsrTargetCategory:"+dsrTargetCategory)	

		return dsrTargetCategory
	}
	
	/**
	 * Returns the dsr targets passed as a parameter, if they belong to the given category. If
	 * they don't, or if no targets are passed as a parameter, it returns the first target
	 * for the given category.
	 * 
	 * @param reportType
	 * @param category
	 * @return
	 */
	public Set<DsrTarget> getDsrIndicators(def reportType, def category){
		Set<DsrTarget> dsrIndicators = null
		
		if(reportType == ReportType.TABLE || category == null) return dsrIndicators

		if(params.list('indicators') != null && !params.list('indicators').empty){
			dsrIndicators = new HashSet<DsrTarget>()
			
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
		
		// if it is null (doesn't belong to the right category or not present in params), we take 
		// the first one of the given category
		if (dsrIndicators == null) {
			dsrIndicators = new HashSet<DsrTarget>()
			

			def targets = category.getAllTargets()
			if(!targets.empty){
				dsrIndicators.addAll(targets.sort({it.order}).first())
			}			
		}
		
		if (log.isDebugEnabled()) log.debug("dsr.view, reportType:"+reportType+", category:"+category+", getDsrIndicators:"+dsrIndicators)

		return dsrIndicators
	}
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("dsr.view, params:"+params)				
		
		// entities from params
		Period period = getPeriod()
		ReportProgram program = getProgram(DsrTargetCategory.class)
		Location location = getLocation()
		Set<DataLocationType> dataLocationTypes = getLocationTypes()
		DsrTargetCategory dsrCategory = getDsrTargetCategory(program)
		ReportType reportType = getReportType()
		Set<DsrTarget> dsrIndicators = getDsrIndicators(reportType, dsrCategory)
		
		// we get the skip levels
		def locationSkipLevels = dsrService.getSkipLocationLevels()
		
		def redirected = false
		// we check if we need to redirect, but only when some of the high level filters are null
		if (period != null && program != null && location != null && dsrCategory != null) {

			// building params for redirection checks
			def reportParams = ['period':period.id, 'program':program.id, 'location':location.id, 
								'dataLocationTypes':dataLocationTypes.collect{ it.id }.sort(), 							
								'dsrCategory':dsrCategory?.id,							
								'reportType':reportType.toString().toLowerCase()]
			
			// this is for maps, we add the selected indicators 
			if(dsrIndicators != null) reportParams['indicators'] = dsrIndicators.collect{ it.id }
			
			// we check if we should redirect
			def newParams = redirectIfDifferent(reportParams)
			
			if(newParams != null && !newParams.empty) {
				redirected = true
				if (log.isDebugEnabled()) log.debug('dsr.view, redirecting, params: '+newParams);
				redirect(action: 'view', params: newParams)
			}
		}
		
		if (!redirected) {
			def dsrTable = null
			if (dsrCategory != null) {
				dsrTable = dsrService.getDsrTable(location, period, dataLocationTypes, dsrCategory, reportType);
			}			
		
			def locationTree = null
			if (location != null) {	
				// entire location tree to filter stuff that has no data for tree table
				locationTree = location.collectTreeWithDataLocations(locationSkipLevels, dataLocationTypes).asList()
			}
		
			if (log.isDebugEnabled()) log.debug('dsr: '+dsrTable+" root program: "+program+", root location: "+location)
			
			[
				currentCategory: dsrCategory,
				currentIndicators: dsrIndicators,
				currentPeriod: period,
				currentProgram: program,
				currentLocation: location,
				currentLocationTypes: dataLocationTypes,
				currentView: reportType,
				
				dsrTable: dsrTable,
				selectedTargetClass: DsrTargetCategory.class,
				locationTree: locationTree,
				locationSkipLevels: locationSkipLevels
			]
		}
	}
	
	def export = {
		if (log.isDebugEnabled()) log.debug("dsr.export, params:"+params)
		
		Period period = getPeriod()
		ReportProgram program = getProgram(DsrTarget.class)
		Location location = getLocation()
		Set<DataLocationType> dataLocationTypes = getLocationTypes()
		DsrTargetCategory dsrCategory = getDsrTargetCategory(program)
		ReportType reportType = getReportType()
		
		def reportParams = ['period':period.id, 'program':program.id, 'location':location.id,
					'dataLocationTypes':dataLocationTypes.collect{ it.id }.sort(),
					'dsrCategory':dsrCategory?.id,
					'reportType':reportType.toString().toLowerCase()]
		
		def newParams = redirectIfDifferent(reportParams)
		
		if(newParams != null && !newParams.empty) {
			redirect(action: 'export', params: newParams)
		}
		else{
			def dsrTable = null
			if (dsrCategory != null)
				dsrTable = dsrService.getDsrTable(location, period, dataLocationTypes, dsrCategory, reportType);
			
			if (log.isDebugEnabled()) log.debug('dsr: '+dsrTable+" program: "+program+", location: "+location)
			
			String report = message(code:'dsr.title');
			String filename = reportExportService.getReportExportFilename(report, location, program, period);
			File csvFile = reportExportService.getReportExportFile(filename, dsrTable, location);
			def zipFile = Utils.getZipFile(csvFile, filename)
				
			if(zipFile.exists()){
				response.setHeader("Content-disposition", "attachment; filename=" + zipFile.getName());
				response.setContentType("application/zip");
				response.setHeader("Content-length", zipFile.length().toString());
				response.outputStream << zipFile.newInputStream()
			}
		}
	}
}
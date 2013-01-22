package org.chai.kevin

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

import org.chai.location.LocationService;
import org.chai.location.DataLocationType;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dsr.DsrTarget;
import org.chai.kevin.fct.FctTarget;
import org.chai.location.DataLocation
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.reports.AbstractReportTarget;
import org.chai.location.DataLocationType;

class FilterTagLib {

	def languageService;
	def locationService;
	def reportService;
	
	def dsrService;
	def fctService;
	
	def topLevelReportTabs = {attrs, body ->
		def model = excludeLinkParams(attrs)
		out << render(template:'/templates/topLevelReportTabs', model:model)
	}
	
	def reportView = {attrs, body ->
		def model = excludeLinkParams(attrs)
		out << render(template:'/templates/reportView', model:model)
	}

	def reportProgramParent = {attrs, body ->
		def model = excludeLinkParams(attrs)
		out << render(template:'/templates/reportProgramParent', model:model)
	}

	def reportLocationParent = {attrs, body ->
		def model = excludeLinkParams(attrs)
		out << render(template:'/templates/reportLocationParent', model:model)
	}
	
	def reportExport = {attrs, body ->
		def model = excludeLinkParams(attrs)
		out << render(template:'/templates/reportExport', model:model)
	}
	
	def reportValueFilter = {attrs, body ->
		def model = excludeLinkParams(attrs)
		out << render(template:'/fct/reportValueFilter', model:model)
	}
	
	def reportCategoryFilter = {attrs, body ->
		def model = excludeLinkParams(attrs)
		model <<
		[
			currentCategory: attrs['selected'],
			targetCategories: dsrService.getDsrCategoriesWithTargets(attrs['program']).sort({it.order}),
		]
		out << render(template:'/dsr/reportCategoryFilter', model:model)
	}
	
	def reportTargetFilter = {attrs, body ->
		def model = excludeLinkParams(attrs)
		model <<
		[
			currentTarget: attrs['selected'],
			targets: fctService.getFctTargetsWithOptions(attrs['program']).sort({it.order}),
		]
		out << render(template:'/fct/reportTargetFilter', model:model)
	}
	
	def periodFilter = {attrs, body ->
		Period.withTransaction {
			def model = excludeLinkParams(attrs)
			def period = attrs['selected']

			if (log.isDebugEnabled()) 
				log.debug('periodFilter:'+period)

			model << 
				[
					currentPeriod: period,
					periods: Period.list([cache: true])
				]
			out << render(template:'/tags/filter/periodFilter', model:model)
		}
	}

	def programFilter = {attrs, body ->
		ReportProgram.withTransaction {
			def model = excludeLinkParams(attrs)
			def selectedProgram = attrs['selected']
			def selectedClass = attrs['selectedTargetClass']
			def programRoot = reportService.getRootProgram()
			def programTree = reportService.collectReportProgramTree(selectedClass, programRoot)
			
			if (log.isDebugEnabled()) 
				log.debug('programFilter:'+programTree+', selectedProgram:'+selectedProgram+', selectedClass:'+selectedClass)
				
			model << 
				[
					currentProgram: selectedProgram,
					programRoot: programRoot,
					programTree: programTree			
				]				
			out << render(template:'/tags/filter/programFilter', model:model)
		}
	}
		
	def locationFilter = {attrs, body ->
		Location.withTransaction {
			def model = excludeLinkParams(attrs)
			def location = attrs['selected']
			def skipLevels = attrs['skipLevels']
			def selectedTypes = attrs['selectedTypes']
			if (selectedTypes == null) selectedTypes = DataLocationType.list([cache: true])
			def locationFilterRoot = locationService.getRootLocation()	
			def locationFilterTree = []
			if (locationFilterRoot != null && !selectedTypes.empty) locationFilterTree.addAll locationFilterRoot.collectTreeWithDataLocations(skipLevels, selectedTypes)
			
			if (log.debugEnabled) log.debug("locationFilter: displaying filter with location: ${location}, skip levels: ${skipLevels}, selected types: ${selectedTypes}, tree: ${locationFilterTree}")
			model << 
				[
					currentLocation: location,
					locationFilterRoot: locationFilterRoot, 
					locationFilterTree: locationFilterTree,
					skipLevels: skipLevels,
					selectedTypes: selectedTypes
				]
			out << render(template:'/tags/filter/locationFilter', model:model)
		}
	}
	
	def dataLocationTypeFilter = {attrs, body ->
		DataLocationType.withTransaction {
			def model = excludeLinkParams(attrs)
			def currentLocationTypes = null
			if (attrs['selected'] == null) currentLocationTypes = []
			else currentLocationTypes = attrs['selected'].asList().sort{it.order}
			def dataLocationTypes = DataLocationType.list([cache: true]).sort{it.order}

			if (log.isDebugEnabled()) 
				log.debug('dataLocationTypeFilter:'+currentLocationTypes)

			model << 
				[
					currentLocationTypes: currentLocationTypes,
					dataLocationTypes: dataLocationTypes					
				]
			out << render(template:'/tags/filter/dataLocationTypeFilter', model:model)
		}
	}	
	
	public excludeLinkParams(def attrs){
		def model = new HashMap(attrs)
		if (model.linkParams == null)
			model << [linkParams: [:]]
		else{
			def includeParams = new HashMap(model.linkParams)
			def excludeParams = model.exclude
			if (excludeParams != null){
				for(def excludeParam : excludeParams){
					includeParams.remove(excludeParam)
				}
			}
			model << [linkParams: includeParams]
		}
		return model
	}
	
}
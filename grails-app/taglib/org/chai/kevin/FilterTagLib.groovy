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

import org.chai.kevin.LocationService;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.dsr.DsrTarget;
import org.chai.kevin.location.DataLocationEntity
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.reports.ReportTarget;
import org.hisp.dhis.period.Period;
import org.chai.kevin.location.DataEntityType;

class FilterTagLib {

	def locationService;
	def reportService;

	def iterationFilter = {attrs, body ->
		Period.withTransaction {
			def model = new HashMap(attrs)
			model << 
				[
					currentPeriod: attrs['selected'],
					periods: Period.list()
				]
			if (model.linkParams == null) model << [linkParams: [:]]
			out << render(template:'/tags/filter/iterationFilter', model:model)
		}
	}

	def programFilter = {attrs, body ->
		ReportProgram.withTransaction {
			def model = new HashMap(attrs)
			def program = attrs['selected']
			def target = attrs['selectedTarget']
			model << 
				[
					currentProgram: program,
					programRoot: reportService.getRootProgram(), 
					programTree: reportService.getProgramTree(target).asList()			
				]
			if (model.linkParams == null) model << [linkParams: [:]]
			out << render(template:'/tags/filter/programFilter', model:model)
		}
	}
		
	def locationFilter = {attrs, body ->
		LocationEntity.withTransaction {
			def model = new HashMap(attrs)					
			def locationFilterRoot = locationService.getRootLocation()	
			def locationFilterTree = locationFilterRoot.collectTreeWithDataEntities(attrs['skipLevels'], null)
			model << 
				[
					currentLocation: attrs['selected'],
					locationFilterRoot: locationFilterRoot, 
					locationFilterTree: locationFilterTree
				]
			if (model.linkParams == null) model << [linkParams: [:]]
			out << render(template:'/tags/filter/locationFilter', model:model)
		}
	}
	
	def locationTypeFilter = {attrs, body ->
		DataEntityType.withTransaction {
			def model = new HashMap(attrs)
			model << 
				[
					currentLocationTypes: attrs['selected'],
					locationTypes: DataEntityType.list()					
				]
			if (model.linkParams == null) model << [linkParams: [:]]
			out << render(template:'/tags/filter/locationTypeFilter', model:model)
		}
	}
	
	def levelFilter = {attrs, body ->
		LocationLevel.withTransaction {
			def model = new HashMap(attrs)
			def currentLevel = attrs['selected']
			def levels = locationService.listLevels(attrs['skipLevels'])
			model << 
				[
					currentLevel: currentLevel,
					levels: levels
				]
			if (model.linkParams == null) model << [linkParams: [:]]
			out << render(template:'/tags/filter/levelFilter', model:model)
		}
	}
	
	def createLinkByFilter = {attrs, body ->
		if (attrs['params'] == null) attrs['params'] = [:]
		else{
			Map params = new HashMap(attrs['params'])
			attrs['params'] = updateParamsByFilter(params);
		}
		out << createLink(attrs, body)
	}	
	
	public Map updateParamsByFilter(Map params) {
		if (!params.containsKey("filter")) return params;
		String filter = (String) params.get("filter");

		LocationEntity location = null;
		if (params.get("location") != null) {
			location = LocationEntity.get(Integer.parseInt(params.get("location")))
		}

		LocationLevel level = null;
		if (params.get("level") != null) {
			level = LocationLevel.get(Integer.parseInt(params.get('level')))
		}

		if (location != null) {
			if (level != null) {
				// TODO use isAfter()
				if (location.getLevel().getOrder() >= level.getOrder()) {
					// conflict
					if (filter == "level") {
						// adjust location to level
						LocationLevel levelBefore = locationService.getLevelBefore(location.getLevel())
						if (levelBefore == null) location = locationService.getRootLocation();
						else location = locationService.getParentOfLevel(location, levelBefore);
					}
					// conflict
					else {
						// adjust level to location
						level = locationService.getLevelAfter(location.getLevel())
					}
				}
			}
			// conflict
			else {
				// adjust level to location
				level = locationService.getLevelAfter(location.getLevel())
			}
		}
		if (location != null) params.put("location", location.id);
		if (level != null) params.put("level", level.id);
		return params;
	}

}
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

import org.chai.kevin.location.LocationEntity
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.hisp.dhis.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.period.Period

abstract class AbstractController {

	def locationService;

	def getOrganisationUnitGroups(def defaultIfNull) {
		List<OrganisationUnitGroup> groups = []
		def groupUuids = null
		if (params['groupUuids'] != null) {
			groupUuids = params.list('groupUuids')
		}
		else {
			groupUuids = ConfigurationHolder.config.dashboard.facility.checked
			ConfigurationHolder.config.dashboard.facility.checked;
		}
		groupUuids.each {groups.add(locationService.findDataEntityTypeByCode(it))}
		return groups;
	}
	
//	def getLocation(def defaultIfNull) {
//		LocationEntity location = LocationEntity.get(params.int('location'));		
//		//if true, return the root organisation
//		//if false, don't return the root organisation
//		if (location == null && defaultIfNull) {
//			location = locationService.getRootLocation();
//		}		
//		return location
//	}
	
//	def getDataEntity() {
//		DataEntity dataEntity = DataEntity.get(params.int('entity'))
//		if (dataEntity == null) {
//			 organisation = organisationService.getOrganisation(params.int('organisation'));
//		}
//	}

	def getPeriod() {
		Period period = Period.get(params.int('period'))
		if (period == null) {
			period = Period.findAll()[ConfigurationHolder.config.site.period]
		}
		return period
	}
	
//	def getLevel(){
//		OrganisationUnitLevel level = null;
//		if(params.int('level')){
//			level = OrganisationUnitLevel.findByLevel(params.int('level'));
//		}
//		return level;
//	}
	
	def adaptParamsForList() {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		params.offset = params.offset ? params.int('offset'): 0
	}

}

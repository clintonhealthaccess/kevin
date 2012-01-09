package org.chai.kevin.maps

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
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.hisp.dhis.period.Period

class MapsController extends AbstractController {

	def mapsService
	def languageService
	
    def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("maps.view, params:"+params)
		
		Period period = getPeriod()
		MapsTarget target = MapsTarget.get(params.int('target'));
		LocationEntity entity = LocationEntity.get(params.int('organisation'));
		
		[
			periods: Period.list(), 
			targets: MapsTarget.list(),
			organisationTree: locationService.getRootLocation(),
			currentPeriod: period, 
			currentTarget: target,
			currentOrganisation: entity
		]
	}
	
	def explain = {
		if (log.isDebugEnabled()) log.debug("maps.infos, params:"+params)
		
		Period period = Period.get(params.int('period'))
		CalculationEntity entity = locationService.getCalculationEntity(params.int('organisation'), CalculationEntity.class);
		MapsTarget target =  MapsTarget.get(params.int('target'));
		
		def info = mapsService.getExplanation(period, organisation, target);
		
		[info: info, target: target, groups: DataEntityType.list()]
	}
	
	def map = {
		if (log.isDebugEnabled()) log.debug("maps.map, params:"+params)
		
		Period period = getPeriod()
		LocationEntity entity = LocationEntity.get(params.int('organisation'));
		if (entity == null) entity = locationService.getRootLocation();
		LocationLevel level = LocationLevel.get(params.int('level'))
		if (level == null) level = entity.getLevel()
		
		MapsTarget target =  MapsTarget.get(params.int('target'));
		
		def map = mapsService.getMap(period, entity, level, target);
		
		if (log.isDebugEnabled()) log.debug("displaying map: "+map)		
		render(contentType:"text/json", text:'{"result":"success","map":'+map.toJson(languageService)+'}');
	}

}

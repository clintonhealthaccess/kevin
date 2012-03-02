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
import org.hisp.dhis.period.Period
import java.util.Collections;

import org.apache.jasper.compiler.Node.ParamsAction;
import org.chai.kevin.AbstractController;
import org.chai.kevin.LanguageService
import org.chai.kevin.LocationService
import org.hisp.dhis.period.Period;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.reports.ReportObjective
import org.chai.kevin.reports.ReportService;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class DsrController extends AbstractController {

	LanguageService languageService;
	DsrService dsrService;
	
	public DsrTargetCategory getDsrTargetCategory(def objective){
		def dsrTargetCategory = null
		if(params.int('dsrCategory') != null)
			dsrTargetCategory = DsrTargetCategory.get(params.int('dsrCategory'))
		else{
			def categories = dsrService.getTargetCategories(objective)
			if(categories != null && !categories.empty)
				dsrTargetCategory = categories.first()
		}
		return dsrTargetCategory
	}
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("dsr.view, params:"+params)				
		
		Period period = getPeriod()		
		ReportObjective objective = getObjective()		
		LocationEntity location = getLocation()
		Set<DataEntityType> locationTypes = getLocationTypes()
		DsrTargetCategory category = getDsrTargetCategory(objective)
		
		def skipLevels = dsrService.getSkipLocationLevels()		
		
		def dsrTable = null		
		if (period != null && objective != null && location != null && locationTypes != null) {
			 dsrTable = dsrService.getDsrTable(location, objective, period, locationTypes, category);				 					 		 			 
		}
		
		if (log.isDebugEnabled()) log.debug('dsr: '+dsrTable+"root objective: "+objective)
		
		[
			dsrTable: dsrTable,
			currentCategory: category,
			currentPeriod: period,
			currentObjective: objective,			
			currentTarget: DsrTarget.class,
			currentLocation: location,
			locationRoot: dsrTable.getLocationRoot(),
			locationTree: dsrTable.getLocationTree().asList(),
			dataLocationTree: dsrTable.getDataLocationTree().asList(),
			currentLocationTypes: locationTypes,
			skipLevels: skipLevels
		]
	}	
}
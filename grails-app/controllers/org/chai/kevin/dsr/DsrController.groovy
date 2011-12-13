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
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period
import java.util.Collections;
import org.chai.kevin.AbstractController;
import org.hisp.dhis.period.Period;
import org.chai.kevin.dsr.DsrObjective;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.reports.ReportService;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class DsrController extends AbstractController {

	ReportService reportService;
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("dsr.view, params:"+params)
		Period period = getPeriod()
		DsrObjective objective = DsrObjective.get(params.int('objective'));
		LocationEntity entity = LocationEntity.get(params.int('entity'));
		List<OrganisationUnitGroup> facilityTypes = getOrganisationUnitGroups(true);
		
		def dsrTable = null
		if (period != null && objective != null && entity != null) {
//			 dsrTable = dsrService.getDsr(organisation, objective, period, new HashSet(facilityTypes*.uuid));
			 dsrTable = reportService.getDsrTable(entity, objective, period, new HashSet(facilityTypes*.uuid));
		}
		
		if (log.isDebugEnabled()) log.debug('dsr: '+dsrTable+"root objective: "+objective)
		
		Integer organisationLevel = ConfigurationHolder.config.facility.level;
		
		[
			dsrTable: dsrTable, 
			currentPeriod: period,
			currentObjective: objective,
			currentOrganisation: entity,
			currentFacilityTypes: facilityTypes,
			periods: Period.list(),
			objectives: DsrObjective.list(),
			facilityTypes: locationService.listTypes(),
		    organisationTree: locationService.getRootLocation()
		]
	}
	
}

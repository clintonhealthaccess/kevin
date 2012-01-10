package org.chai.kevin.dashboard

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

import java.util.List

import org.chai.kevin.data.RawDataElement
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity
import org.chai.kevin.value.RawDataElementValue
import org.hisp.dhis.period.Period
import org.chai.kevin.reports.ReportObjective

class DashboardServiceSpec extends DashboardIntegrationTests {

	def dashboardService
	
	def "dashboard service works"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		def root = newReportObjective(CODE(1))
		def calculation = newAverage("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, root, 1)
		def dashboard = null
		refresh()
		
		when:
		dashboard = dashboardService.getDashboard(LocationEntity.findByCode(RWANDA), root, period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]))
		
		then:
		dashboard.organisations.equals([LocationEntity.findByCode(NORTH)])
		
		when:
		dashboard = dashboardService.getDashboard(LocationEntity.findByCode(BURERA), root, period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]))
		
		then:
		s(dashboard.organisations).equals(s([DataEntity.findByCode(KIVUYE), DataEntity.findByCode(BUTARO)]))
		
		when:
		dashboard = dashboardService.getDashboard(LocationEntity.findByCode(BURERA), root, period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		
		then:
		dashboard.organisations.equals([DataEntity.findByCode(BUTARO)])
		
	}
	
	def "test dashboard with correct values"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupDashboard()
		refresh()

		when:
		def dashboard = dashboardService.getDashboard(LocationEntity.findByCode(currentOrganisationName), ReportObjective.findByCode(currentObjectiveName), period, new HashSet(groups.collect {DataEntityType.findByCode(it)}));
		def percentage = dashboard.getPercentage(getCalculationEntity(organisationName), getDashboardEntity(objectiveName))

		then:
		if (percentage.value == null) value == null
		else percentage.value == value

		where:
		currentOrganisationName	| currentObjectiveName	| organisationName	| objectiveName | groups																							  | value
		BURERA					| OBJECTIVE				| BUTARO			| TARGET1		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|40.0d
		BURERA					| OBJECTIVE				| BUTARO			| TARGET2		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|20.0d
		BURERA					| OBJECTIVE				| KIVUYE			| TARGET1		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|40.0d
		BURERA					| OBJECTIVE				| KIVUYE			| TARGET2		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|null
		RWANDA					| ROOT					| NORTH				| OBJECTIVE		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|30.0d
		BURERA					| OBJECTIVE				| BUTARO			| TARGET1		| [DISTRICT_HOSPITAL_GROUP]						|20.0d
		RWANDA					| ROOT					| NORTH				| OBJECTIVE		| [DISTRICT_HOSPITAL_GROUP]						|20.0d
	}

	def "dashboard test objective path"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupDashboard()
		refresh()

		when:
		def dashboard = dashboardService.getDashboard(LocationEntity.findByCode(organisationName), ReportObjective.findByCode(objectiveCode), period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]));

		then:
		dashboard.objectiveEntities.containsAll expectedEntities.collect {getDashboardEntity(it)}
		dashboard.objectivePath.containsAll expectedObjectivePath.collect {DashboardObjective.findByCode(it)}
		// TODO order organisations
		dashboard.organisations.containsAll expectedOrganisations.collect {DataEntity.findByCode(it)}
		dashboard.organisationPath.containsAll expectedOrganisationPath.collect {LocationEntity.findByCode(it)}

		where:
		organisationName| objectiveCode	| expectedOrganisations	| expectedEntities  | expectedOrganisationPath	| expectedObjectivePath
		BURERA			| OBJECTIVE		| [BUTARO, KIVUYE]		| [TARGET1, TARGET2]| [RWANDA, NORTH]			| [ROOT]
		BURERA			| ROOT			| [BUTARO, KIVUYE]		| [OBJECTIVE]		| [RWANDA, NORTH]			| []
	}

	
	def getDashboardEntity(String code) {
		def entity = DashboardObjective.findByCode(code);
		if(entity == null) entity = DashboardTarget.findByCode(code);
		return entity
	}

	def getReportObjectives(List<String> codes) {
		def reportObjectives = []
		for (String code : codes) {
			def entity = DashboardObjective.findByCode(code);
			if(entity == null) DashboardTarget.findByCode(code);
			if(entity != null && entity.getObjective() != null) 
				reportObjectives.add(entity.getObjective());
		}
		return reportObjectives;
	}
}
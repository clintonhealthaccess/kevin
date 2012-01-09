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

import org.hisp.dhis.organisationunit.OrganisationUnit;

import java.util.List

import org.chai.kevin.Organisation
import org.chai.kevin.data.RawDataElement
import org.chai.kevin.value.RawDataElementValue
import org.hisp.dhis.organisationunit.OrganisationUnit
import org.hisp.dhis.period.Period
import org.chai.kevin.reports.ReportObjective

class DashboardServiceSpec extends DashboardIntegrationTests {

	def dashboardService
	def reportService
	
	def "dashboard service works"() {
		setup:
		def period = newPeriod()
		setupOrganisationUnitTree()
		def root = newReportObjective(CODE(1))
		def calculation = newAverage("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, root, 1)
		def dashboard = null
		refresh()
		
		when:
		dashboard = dashboardService.getDashboard(getOrganisation(RWANDA), target.getObjective(), period, new HashSet([DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]))
		
		then:
		dashboard.organisations.equals([getOrganisation(NORTH)])
		
		when:
		dashboard = dashboardService.getDashboard(getOrganisation(BURERA), target.getObjective(), period, new HashSet([DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]))
		
		then:
		dashboard.organisations.equals([getOrganisation(KIVUYE), getOrganisation(BUTARO)])
		
		when:
		dashboard = dashboardService.getDashboard(getOrganisation(BURERA), target.getObjective(), period, new HashSet([DISTRICT_HOSPITAL_GROUP]))
		
		then:
		dashboard.organisations.equals([getOrganisation(BUTARO)])
		
	}
	
	def "test dashboard with correct values"() {
		setup:
		def period = newPeriod()
		setupOrganisationUnitTree()
		setupDashboard()
		refresh()

		when:
		def currentOrganisation = new Organisation(OrganisationUnit.findByName(currentOrganisationName));
		def currentObjective = DashboardObjective.findByCode(currentObjectiveName);

		def dashboard = dashboardService.getDashboard(currentOrganisation, currentObjective.getObjective(), period, new HashSet(groups));
		def percentage = dashboard.getPercentage(getOrganisation(organisationName), currentObjective)

		then:
		if (percentage.value == null) value == null
		else percentage.value == value

		where:
		currentOrganisationName	| currentObjectiveName	| organisationName	| objectiveName | groups										| value
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
		setupOrganisationUnitTree()
		setupDashboard()
		refresh()

		when:
		def objective = DashboardObjective.findByCode(objectiveCode);
		def dashboard = dashboardService.getDashboard(getOrganisation(organisationName), objective.getObjective(), period, new HashSet([DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]));
		def dashboardEntities = [];
		for(def dashboardEntity : reportService.getDashboardEntities(objective.getObjective()))
			 dashboardEntities.add(dashboardEntity.code);
		def dashboardObjectivePath = [];
		for(def objectivePathItem : dashboard.objectivePath)
			dashboardObjectivePath.add(objectivePathItem.code);
		
		then:
		dashboardEntities == expectedEntities
		dashboardObjectivePath == expectedObjectivePath
		// TODO order organisations
		dashboard.organisations.containsAll getOrganisations(expectedOrganisations)
		dashboard.organisationPath == getOrganisations(expectedOrganisationPath)

		where:
		organisationName| objectiveCode	| expectedOrganisations	| expectedEntities  | expectedOrganisationPath	| expectedObjectivePath
		BURERA			| OBJECTIVE		| [BUTARO, KIVUYE]		| [TARGET1, TARGET2]| [RWANDA, NORTH]			| [ROOT]
		BURERA			| ROOT			| [BUTARO, KIVUYE]		| [OBJECTIVE]		| [RWANDA, NORTH]			| []
	}

	def getDashboardEntities(List<String> codes) {
		def entities = []
		for (String code : codes) {
			def entity = DashboardObjective.findByCode(code);
			if(entity == null) DashboardTarget.findByCode(code);
			if(entity != null) 
				entities.add(entity)
		}
		return entities;
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
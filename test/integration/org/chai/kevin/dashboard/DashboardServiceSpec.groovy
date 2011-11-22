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

class DashboardServiceSpec extends DashboardIntegrationTests {

	def dashboardService

	def "dashboard service works"() {
		setup:
		def period = newPeriod()
		setupOrganisationUnitTree()
		def root = newDashboardObjective(CODE(1))
		def calculation = newAverage("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, root, 1)
		def organisation = getOrganisation(RWANDA)
		refresh()
		
		when:
		def dashboard = dashboardService.getDashboard(organisation, root, period)
		
		then:
		dashboard.getPercentage(getOrganisation(NORTH), target) != null
		
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

		def dashboard = dashboardService.getDashboard(currentOrganisation, currentObjective, period);
		def percentage = dashboard.getPercentage(getOrganisation(organisationName), getObjective(objectiveName))

		then:
		//		percentage.status == status;
		if (percentage.value == null) value == null
		else percentage.value == value

		where:
		currentOrganisationName	| currentObjectiveName	| organisationName	| objectiveName | value
		BURERA					| OBJECTIVE				| BUTARO			| TARGET1		| 40.0d
		BURERA					| OBJECTIVE				| BUTARO			| TARGET2		| 20.0d
		BURERA					| OBJECTIVE				| KIVUYE			| TARGET1		| 40.0d
		BURERA					| OBJECTIVE				| KIVUYE			| TARGET2		| null
		RWANDA					| ROOT					| NORTH				| OBJECTIVE		| 30.0d
	}

	def "dashboard test objective path"() {
		setup:
		def period = newPeriod()
		setupOrganisationUnitTree()
		setupDashboard()
		refresh()

		when:
		def objective = DashboardObjective.findByCode(objectiveCode);
		def dashboard = dashboardService.getDashboard(getOrganisation(organisationName), objective, period);

		then:
		dashboard.objectiveEntries == getWeightedObjectives(expectedObjectives)
		// TODO order organisations
		dashboard.organisations.containsAll getOrganisations(expectedOrganisations)
		dashboard.organisationPath == getOrganisations(expectedOrganisationPath)
		dashboard.objectivePath == getObjectives(expectedObjectivePath)

		where:
		organisationName| objectiveCode	| expectedOrganisations	| expectedObjectives| expectedOrganisationPath	| expectedObjectivePath
		BURERA			| OBJECTIVE		| [BUTARO, KIVUYE]		| [TARGET1, TARGET2]| [RWANDA, NORTH]			| [ROOT]
		BURERA			| ROOT			| [BUTARO, KIVUYE]		| [OBJECTIVE]		| [RWANDA, NORTH]			| []

	}

	def getObjectives(List<String> codes) {
		def objectives = []
		for (String code : codes) {
			objectives.add(getObjective(code))
		}
		return objectives;
	}

	def getObjective(String code) {
		def objective = DashboardTarget.findByCode(code);
		if (objective == null) objective = DashboardObjective.findByCode(code);
		return objective
	}

	def getWeightedObjectives(List<String> codes) {
		def objectives = []
		for (String code : codes) {
			def objective = DashboardTarget.findByCode(code);
			if (objective == null) objective = DashboardObjective.findByCode(code);
			objectives.add(objective.getParent());
		}
		return objectives;
	}
}
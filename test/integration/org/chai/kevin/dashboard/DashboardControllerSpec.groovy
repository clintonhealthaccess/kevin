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

import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Expression;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;

import grails.plugin.spock.IntegrationSpec;
import grails.plugin.spock.UnitSpec;

class DashboardControllerSpec extends IntegrationTests {

	def dashboardObjectiveController
	def dashboardTargetController
	
	def setup() {
		Initializer.createDummyStructure();
		IntegrationTestInitializer.createExpressions();
		IntegrationTestInitializer.createDashboard();
	}
	
	def "test tests"() {
		expect:
		DashboardObjectiveEntry.get(DashboardObjective.findByCode("HRH").objectiveEntries[0].id) != null
		DashboardObjectiveEntry.get(DashboardObjective.findByCode("STAFFING").objectiveEntries[0].id) != null
		
	}
	
	def "delete objective with children"() {
		setup:
		def entries = DashboardObjectiveEntry.count()
		def targets = DashboardTarget.count()
		def objectives = DashboardObjective.count()
		dashboardObjectiveController = new DashboardObjectiveController()
		
		when:
		dashboardObjectiveController.params.id = DashboardObjective.findByCode("HRH").objectiveEntries[0].id
		dashboardObjectiveController.delete()
		
		then:
		entries == DashboardObjectiveEntry.count()
		targets == DashboardTarget.count()
		objectives == DashboardObjective.count()
//		dashboardObjectiveController.response.contentAsString.contains "error";
	}

	
	def "delete target deletes entry and target"() {
		setup:
		def entries = DashboardObjectiveEntry.count()
		def targets = DashboardTarget.count()
		def objectives = DashboardObjective.count()
		dashboardTargetController = new DashboardTargetController();
		
		when:
		dashboardTargetController.params.id = DashboardObjective.findByCode("STAFFING").objectiveEntries[0].id
		dashboardTargetController.delete()
		
		then:
		DashboardTarget.count() == targets-1
		DashboardObjectiveEntry.count() == entries-1
		DashboardObjective.count() == objectives
		dashboardTargetController.response.contentAsString.contains "success";
	}
	
	def "save new objective"() {
		setup:
		def entries = DashboardObjectiveEntry.count()
		def targets = DashboardTarget.count()
		def objectives = DashboardObjective.count()
		dashboardObjectiveController = new DashboardObjectiveController()
		
		when:
		dashboardObjectiveController.params['currentObjective'] = DashboardObjective.findByCode("STAFFING").id
		dashboardObjectiveController.params['weight'] = 1
		dashboardObjectiveController.params['entry.code'] = "NEW"
		dashboardObjectiveController.saveWithoutTokenCheck()
		def newObjective = DashboardObjective.findByCode("NEW")
		
		then:
		dashboardObjectiveController.response.contentAsString.contains "success";
		newObjective != null
		entries + 1 == DashboardObjectiveEntry.count()
		targets == DashboardTarget.count()
		objectives + 1 == DashboardObjective.count()
		newObjective.parent.weight == 1
	}
	
	def "save new target"() {
		setup:
		def entries = DashboardObjectiveEntry.count()
		def targets = DashboardTarget.count()
		def objectives = DashboardObjective.count()
		dashboardTargetController = new DashboardTargetController()
		
		when:
		dashboardTargetController.params['currentObjective'] = DashboardObjective.findByCode("STAFFING").id
		dashboardTargetController.params['weight'] = 1
		dashboardTargetController.params['entry.code'] = "NEW"
		dashboardTargetController.params['entry.calculation.expressions['+OrganisationUnitGroup.findByName('Health Center').uuid+'].id'] = "null"
		dashboardTargetController.saveWithoutTokenCheck()
		def newTarget = DashboardTarget.findByCode("NEW")
		
		then:
		dashboardTargetController.response.contentAsString.contains "success";
		newTarget != null
		entries + 1 == DashboardObjectiveEntry.count()
		targets + 1 == DashboardTarget.count() 
		objectives == DashboardObjective.count() 
		newTarget.parent.weight == 1
	}
	
	def "save target with calculations"() {
		setup:
		def entries = DashboardObjectiveEntry.count()
		def targets = DashboardTarget.count()
		def objectives = DashboardObjective.count()
		def calculations = Calculation.count()
		dashboardTargetController = new DashboardTargetController()
		
		when:
		dashboardTargetController.params['currentObjective'] = DashboardObjective.findByCode("STAFFING").id
		dashboardTargetController.params['weight'] = 1
		dashboardTargetController.params['entry.code'] = "NEW"
		dashboardTargetController.params['entry.calculation.expressions['+OrganisationUnitGroup.findByName('Health Center').uuid+'].id'] = Expression.findByCode("CONST10").id+""
		dashboardTargetController.params['entry.calculation.epxressions['+OrganisationUnitGroup.findByName('District Hospital').uuid+'].id'] = "null"
		dashboardTargetController.saveWithoutTokenCheck()
		def newTarget = DashboardTarget.findByCode("NEW")
		
		then:
		dashboardTargetController.response.contentAsString.contains "success";
		newTarget != null
		entries + 1 == DashboardObjectiveEntry.count()
		targets + 1 == DashboardTarget.count()
		objectives == DashboardObjective.count()
		calculations + 1 == Calculation.count()
		newTarget.calculation.expressions['Health Center'] == Expression.findByCode("CONST10")
		newTarget.calculation.expressions['District Hospital'] == null
		newTarget.parent.weight == 1
	}
	
	def "edit target with calculations"() {
		setup:
		def entries = DashboardObjectiveEntry.count()
		def targets = DashboardTarget.count()
		def objectives = DashboardObjective.count()
		def calculations = Calculation.count()
		dashboardTargetController = new DashboardTargetController()
		
		when:
		dashboardTargetController.params['id'] = DashboardTarget.findByCode('A2').parent.id
		dashboardTargetController.params['weight'] = 1
		dashboardTargetController.params['entry.code'] = "NEW"
		dashboardTargetController.params['entry.calculation.expressions['+OrganisationUnitGroup.findByName('Health Center').uuid+'].id'] = Expression.findByCode("CONST10").id+""
		dashboardTargetController.params['entry.calculation.expressions['+OrganisationUnitGroup.findByName('District Hospital').uuid+'].id'] = "null"
		dashboardTargetController.saveWithoutTokenCheck()
		def newTarget = DashboardTarget.findByCode("NEW")
		
		then:
		dashboardTargetController.response.contentAsString.contains "success";
		newTarget != null
		entries == DashboardObjectiveEntry.count()
		targets == DashboardTarget.count()
		objectives == DashboardObjective.count()
		calculations == Calculation.count()
		newTarget.calculation.expressions['Health Center'] == Expression.findByCode("CONST10")
		newTarget.calculation.expressions['District Hospital'] == null
		newTarget.parent.weight == 1
	}
	
}

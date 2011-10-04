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

import org.chai.kevin.data.Average;
import org.chai.kevin.data.Calculation
import org.chai.kevin.data.Type

class DashboardTargetControllerSpec extends DashboardIntegrationTests {

	def dashboardTargetController
	def dashboardService
	
	def "delete target flushes cache"() {
		setup:
		dashboardTargetController = new DashboardTargetController()
		def period = newPeriod()
		setupOrganisationUnitTree()
		def root = newDashboardObjective(CODE(1))
		def calculation = newAverage([:], CODE(2), Type.TYPE_NUMBER())
		def target = newDashboardTarget(TARGET1, calculation, root, "1")
		def organisation = getOrganisation(RWANDA)
		refresh()
		
		when:
		def dashboard = dashboardService.getDashboard(organisation, root, period)
		
		then:
		dashboard.getPercentage(getOrganisation(NORTH), target) != null
		
		// TODO can't work because controller class is not instrumented
//		when:
//		dashboardTargetController.params.id = target.parent.id
//		dashboardTargetController.delete()
//		
//		then:
//		dashboard.getPercentage(getOrganisation(NORTH), target) == null
		
	}
	
	def "delete target deletes entry and target"() {
		setup:
		def root = newDashboardObjective(CODE(1))
		def calculation = newAverage([:], CODE(2), Type.TYPE_NUMBER())
		def target = newDashboardTarget(TARGET1, calculation, root, 1)
	
		dashboardTargetController = new DashboardTargetController();
		
		when:
		dashboardTargetController.params.id = target.parent.id
		dashboardTargetController.delete()
		
		then:
		dashboardTargetController.response.contentAsString.contains "success";

		DashboardTarget.count() == 0
		DashboardObjectiveEntry.count() == 0
		DashboardObjective.count() == 1
	}
	
	def "save new target"() {
		setup:
		def root = newDashboardObjective(CODE(1))
		dashboardTargetController = new DashboardTargetController()
		
		when:
		dashboardTargetController.params['currentObjective'] = root.id
		dashboardTargetController.params['weight'] = 1
		dashboardTargetController.params['entry.code'] = "NEW"
		dashboardTargetController.params['entry.calculation.expressions['+HEALTH_CENTER_GROUP+'].id'] = "null"
		dashboardTargetController.saveWithoutTokenCheck()
		def newTarget = DashboardTarget.findByCode("NEW")
		
		then:
		dashboardTargetController.response.contentAsString.contains "success";
		newTarget != null
		newTarget.parent.weight == 1
		DashboardObjectiveEntry.count() == 1
		DashboardTarget.count() == 1 
		DashboardObjective.count() == 1 
	}
	
	def "save target with calculations"() {
		setup:
		def expression = newExpression(CODE(2), Type.TYPE_NUMBER(), "1")
		def root = newDashboardObjective(CODE(1))
		dashboardTargetController = new DashboardTargetController()
		
		when:
		dashboardTargetController.params['currentObjective'] = root.id
		dashboardTargetController.params['weight'] = 1
		dashboardTargetController.params['entry.code'] = "NEW"
		dashboardTargetController.params['entry.calculation.expressions['+HEALTH_CENTER_GROUP+'].id'] = expression.id+""
		dashboardTargetController.params['entry.calculation.epxressions['+DISTRICT_HOSPITAL_GROUP+'].id'] = "null"
		dashboardTargetController.saveWithoutTokenCheck()
		def newTarget = DashboardTarget.findByCode("NEW")
		
		then:
		dashboardTargetController.response.contentAsString.contains "success";
		newTarget != null
		newTarget.calculation.expressions['Health Center'] == expression
		newTarget.calculation.expressions['District Hospital'] == null
		newTarget.parent.weight == 1
		
		DashboardObjectiveEntry.count() == 1
		DashboardTarget.count() == 1
		DashboardObjective.count() == 1
		Average.count() == 1
	}
	
	def "edit target with calculations"() {
		setup:
		def root = newDashboardObjective(CODE(1))
		def expression = newExpression(CODE(2), Type.TYPE_NUMBER(), "1")
		def caculation = newAverage([:], CODE(3), Type.TYPE_NUMBER())
		def target = newDashboardTarget(TARGET1, caculation, root, 1)
		dashboardTargetController = new DashboardTargetController()
		
		when:
		dashboardTargetController.params['id'] = target.parent.id
		dashboardTargetController.params['weight'] = 1
		dashboardTargetController.params['entry.code'] = "NEW"
		dashboardTargetController.params['entry.calculation.expressions['+HEALTH_CENTER_GROUP+'].id'] = expression.id+""
		dashboardTargetController.params['entry.calculation.expressions['+DISTRICT_HOSPITAL_GROUP+'].id'] = "null"
		dashboardTargetController.saveWithoutTokenCheck()
		def newTarget = DashboardTarget.findByCode("NEW")
		
		then:
		dashboardTargetController.response.contentAsString.contains "success";
		newTarget != null
		newTarget.calculation.expressions['Health Center'] == expression
		newTarget.calculation.expressions['District Hospital'] == null
		newTarget.parent.weight == 1

		DashboardObjectiveEntry.count() == 1
		DashboardTarget.count() == 1
		DashboardObjective.count() == 1
		Average.count() == 1
	}
	
}

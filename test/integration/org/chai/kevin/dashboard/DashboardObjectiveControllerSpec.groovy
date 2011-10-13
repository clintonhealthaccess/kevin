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


class DashboardObjectiveControllerSpec extends DashboardIntegrationTests {

	def dashboardObjectiveController
	
	def "delete objective with children"() {
		setup:
		def root = newDashboardObjective(CODE(1))
		def objective = newDashboardObjective(CODE(2), root, 1)
		dashboardObjectiveController = new DashboardObjectiveController()
		
		when:
		dashboardObjectiveController.params.id = root.id
		dashboardObjectiveController.delete()
		
		then:
		DashboardObjective.count() == 2
		//		dashboardObjectiveController.response.contentAsString.contains "error";
	}

	def "save new objective"() {
		setup:
		def root = newDashboardObjective(CODE(1))
		dashboardObjectiveController = new DashboardObjectiveController()
		
		when:
		dashboardObjectiveController.params['currentObjective'] = root.id
		dashboardObjectiveController.params['weight'] = 1
		dashboardObjectiveController.params['entry.code'] = "NEW"
		dashboardObjectiveController.saveWithoutTokenCheck()
		def newObjective = DashboardObjective.findByCode("NEW")
		
		then:
		dashboardObjectiveController.response.redirectedUrl.equals(dashboardObjectiveController.getTargetURI())
		newObjective != null
		newObjective.parent.weight == 1
		DashboardObjectiveEntry.count() == 1
		DashboardObjective.count() == 2
	}
	
}

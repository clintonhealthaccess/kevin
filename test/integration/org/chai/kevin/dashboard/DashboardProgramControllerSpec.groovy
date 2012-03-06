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

import org.chai.kevin.reports.ReportProgram

class DashboardProgramControllerSpec extends DashboardIntegrationTests {

	def dashboardProgramController
	
	def "delete program "() {
		setup:
		def root = newReportProgram(CODE(1))
		def program = newDashboardProgram(CODE(2), root, 1)
		dashboardProgramController = new DashboardProgramController()
		
		when:
		dashboardProgramController.params.id = program.id
		dashboardProgramController.delete()
		
		then:
		ReportProgram.count() == 1
		DashboardProgram.count() == 0
	}
	
	def "delete program with children does not delete"() {
		setup:
		def root = newReportProgram(CODE(1))
		def program = newDashboardProgram(CODE(2), root, 1)
		def child = newReportProgram(CODE(3), root)
		def childProgram = newDashboardProgram(CODE(4), child, 1)
		dashboardProgramController = new DashboardProgramController()
		
		when:
		dashboardProgramController.params.id = program.id
		dashboardProgramController.delete()
		
		then:
		ReportProgram.count() == 2
		DashboardProgram.count() == 2
	}

	def "save new program"() {
		setup:
		def root = newReportProgram(CODE(1))
		def program = newDashboardProgram(CODE(2), root, 1)
		dashboardProgramController = new DashboardProgramController()
		
		when:
		dashboardProgramController.params['id'] = program.id
		dashboardProgramController.params['weight'] = 1
		dashboardProgramController.params['code'] = "NEW"
		dashboardProgramController.saveWithoutTokenCheck()
		
		then:
		dashboardProgramController.response.redirectedUrl.equals(dashboardProgramController.getTargetURI())
		DashboardProgram.count() == 1
	}
	
}

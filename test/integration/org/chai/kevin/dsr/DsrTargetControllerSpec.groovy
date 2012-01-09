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

import org.chai.kevin.data.Average;
import org.chai.kevin.data.Calculation
import org.chai.kevin.data.Type

class DsrTargetControllerSpec extends DsrIntegrationTests {

	def dsrTargetController
	def dataService
	def dsrService
	
	def "delete target refreshes cache"() {
		setup:
		dsrTargetController = new DsrTargetController()
		setupOrganisationUnitTree()
		def period = newPeriod()
		def objective = newReportObjective(CODE(1))
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(3), dataElement, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], objective)
		def organisation = getOrganisation(BURERA)
		refresh()
		
		when:
		def dsrTable = dsrService.getDsrTable(organisation, objective, period, new HashSet([DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]))
		
		then:
		dsrTable.getDsrValue(getOrganisation(BUTARO), target) != null
	}
		
		// TODO can't work because controller class is not instrumented 
//		when:
//		def dsrTable = reportService.getDsrTable(organisation, objective, period)
//		
//		then:
//		dsrTable.getDsrValue(getOrganisation(BUTARO), target) != null
//		
//		// TODO can't work because controller class is not instrumented 
////		when:
////		dsrTargetController.params.id = target.id
////		dsrTargetController.delete()
////		dsrTable = dsrService.getDsr(organisation, objective, period)
////		
////		then:
////		dsrTable.getDsr(getOrganisation(BUTARO), target) == null
//	}
	
	def "save target saves target"() {
		setup:
		setupOrganisationUnitTree()
		def objective = newReportObjective(CODE(1))
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		dsrTargetController = new DsrTargetController()
		dsrTargetController.dataService = dataService
		
		when:
		dsrTargetController.params.code = CODE(2)
		dsrTargetController.params['dataElement.id'] = dataElement.id+""
		dsrTargetController.params['objective.id'] = objective.id+""
		dsrTargetController.params.groupUuids = [DISTRICT_HOSPITAL_GROUP]
		dsrTargetController.saveWithoutTokenCheck()
		
		then:
		DsrTarget.count() == 1
		DsrTarget.list()[0].dataElement.equals(dataElement)
	}
	
}
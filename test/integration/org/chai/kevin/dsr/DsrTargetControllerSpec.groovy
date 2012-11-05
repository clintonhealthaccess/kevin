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

import org.chai.kevin.data.Calculation
import org.chai.kevin.data.Type

class DsrTargetControllerSpec extends DsrIntegrationTests {

	def dsrTargetController
	def dataService
	def dsrService	
	
	def "create target with ratio calculation element"(){
		setup:
		setupLocationTree()
		def program = newReportProgram(CODE(1))
		def ratio = newSum("1", CODE(2))
		def category = newDsrTargetCategory(CODE(2), program, 1)
		dsrTargetController = new DsrTargetController()
		dsrTargetController.dataService = dataService
		
		when:
		dsrTargetController.params.code = CODE(4)
		dsrTargetController.params['data.id'] = ratio.id+""
		dsrTargetController.params['category.id'] = category.id+""
		dsrTargetController.saveWithoutTokenCheck()
		
		then:
		DsrTarget.count() == 1
		DsrTarget.list()[0].data.equals(ratio)				
	}
	
	def "create target with sum calculation element"(){
		setup:
		setupLocationTree()
		def program = newReportProgram(CODE(1))
		def category = newDsrTargetCategory(CODE(2), program, 1)
		def sum = newSum("1", CODE(2))
		dsrTargetController = new DsrTargetController()
		dsrTargetController.dataService = dataService
		
		when:
		dsrTargetController.params.code = CODE(5)
		dsrTargetController.params['data.id'] = sum.id+""
		dsrTargetController.params['category.id'] = category.id+""
		dsrTargetController.saveWithoutTokenCheck()
		
		then:
		DsrTarget.count() == 1
		DsrTarget.list()[0].data.equals(sum)
	}
	
	def "create target with raw data element calculation element"() {
		setup:
		setupLocationTree()
		def program = newReportProgram(CODE(1))
		def category = newDsrTargetCategory(CODE(2), program, 1)
		def rawDataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		dsrTargetController = new DsrTargetController()
		dsrTargetController.dataService = dataService
		
		when:
		dsrTargetController.params.code = CODE(3)
		dsrTargetController.params['data.id'] = rawDataElement.id+""
		dsrTargetController.params['category.id'] = category.id+""
		dsrTargetController.saveWithoutTokenCheck()
		
		then:
		DsrTarget.count() == 1
		DsrTarget.list()[0].data.equals(rawDataElement)				
	}
	
	def "create target with normalized data element calculation element"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def category = newDsrTargetCategory(CODE(2), program, 1)
		def normalizedDataElement = newNormalizedDataElement(CODE(4), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]])
		dsrTargetController = new DsrTargetController()
		dsrTargetController.dataService = dataService
		
		when:
		dsrTargetController.params.code = CODE(5)
		dsrTargetController.params['data.id'] = normalizedDataElement.id+""
		dsrTargetController.params['category.id'] = category.id+""
		dsrTargetController.saveWithoutTokenCheck()
		
		then:
		DsrTarget.count() == 1
		DsrTarget.list()[0].data.equals(normalizedDataElement)
	}
	
	def "delete target" () {
		setup:
		setupLocationTree()
		def program = newReportProgram(CODE(1))
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CODE(2), program, 1)
		def target = newDsrTarget(CODE(3), dataElement, category)
		dsrTargetController = new DsrTargetController()
		dsrTargetController.dataService = dataService
		
		when:
		dsrTargetController.params.id = target.id
		dsrTargetController.delete()
		
		then:
		DsrTarget.count() == 0
	}	
	
	def "search target"() {
		setup:
		def program = newReportProgram(CODE(1))
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CODE(2), program, 1)
		def target = newDsrTarget(CODE(3), dataElement, category)
		dsrTargetController = new DsrTargetController()
		dsrTargetController.dataService = dataService
		
		when:
		dsrTargetController.params.q = CODE(3)
		dsrTargetController.search()
		
		then:
		dsrTargetController.modelAndView.model.entities.size() == 1
		dsrTargetController.modelAndView.model.entities[0].equals(target)
		dsrTargetController.modelAndView.model.entityCount == 1
	}
}

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

class DsrTargetCategoryControllerSpec extends DsrIntegrationTests {

	def dsrTargetCategoryController
	
	def "list target with offset works"() {
		setup:
		def program = newReportProgram(CODE(1))
		def category1 = newDsrTargetCategory(CODE(2), program, 1)
		newDsrTarget(CODE(3), 1, newRawDataElement(CODE(2), Type.TYPE_NUMBER()), category1);
		newDsrTarget(CODE(4), 2, newRawDataElement(CODE(3), Type.TYPE_NUMBER()), category1);
		newDsrTarget(CODE(5), 3, newRawDataElement(CODE(4), Type.TYPE_NUMBER()), category1);
		
		def category2 = newDsrTargetCategory(CODE(6), program, 2)
		def category3 = newDsrTargetCategory(CODE(7), program, 3)
		dsrTargetCategoryController = new DsrTargetCategoryController()
		
		when:
		dsrTargetCategoryController.params.max = 1
		dsrTargetCategoryController.params.offset = 0
		dsrTargetCategoryController.list()
		
		then:
		dsrTargetCategoryController.modelAndView.model.entities.equals([category1])
		
		when:
		dsrTargetCategoryController.params.max = 1
		dsrTargetCategoryController.params.offset = 1
		dsrTargetCategoryController.list()
		
		then:
		dsrTargetCategoryController.modelAndView.model.entities.equals([category2])
		
		when:
		dsrTargetCategoryController.params.max = 1
		dsrTargetCategoryController.params.offset = 2
		dsrTargetCategoryController.list()
		
		then:
		dsrTargetCategoryController.modelAndView.model.entities.equals([category3])
	}
	
	def "search category"() {
		setup:
		def program = newReportProgram(CODE(1))
		def category1 = newDsrTargetCategory(CODE(2), program, 1)
		newDsrTarget(CODE(3), 1, newRawDataElement(CODE(2), Type.TYPE_NUMBER()), category1);
		newDsrTarget(CODE(4), 2, newRawDataElement(CODE(3), Type.TYPE_NUMBER()), category1);
		newDsrTarget(CODE(5), 3, newRawDataElement(CODE(4), Type.TYPE_NUMBER()), category1);
		
		def category2 = newDsrTargetCategory(CODE(6), program, 2)
		def category3 = newDsrTargetCategory(CODE(7), program, 3)
		dsrTargetCategoryController = new DsrTargetCategoryController()
		
		when:
		dsrTargetCategoryController.params.q = CODE(7)
		dsrTargetCategoryController.search()
		
		then:
		dsrTargetCategoryController.modelAndView.model.entities.size() == 1
		dsrTargetCategoryController.modelAndView.model.entities[0].equals(category3)
		dsrTargetCategoryController.modelAndView.model.entityCount == 1
	}
}

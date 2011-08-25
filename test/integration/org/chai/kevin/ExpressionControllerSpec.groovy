package org.chai.kevin

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

import org.chai.kevin.data.Constant;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.ExpressionController;

import grails.plugin.spock.UnitSpec;

class ExpressionControllerSpec extends IntegrationTests {

	def expressionController

	def "get constants"() {
		
		setup:
		Initializer.createDummyStructure()
		IntegrationTestInitializer.createConstants()
		expressionController = new ExpressionController()
		
		when:
		expressionController.params.constant = 'con'
		expressionController.params.type = 'constant'
		expressionController.params.controller = 'expression'
		def model = expressionController.getData()
		
		then:
		expressionController.response.contentAsString.contains("success")
		expressionController.response.contentAsString.contains("Constant 1000")
		
	}
	
	def "get constant description"() {
		
		setup:
		Initializer.createDummyStructure()
		IntegrationTestInitializer.createConstants()
		expressionController = new ExpressionController()
		
		when:
		expressionController.params.constant = Constant.findByCode("CONST1").id+''
		def model = expressionController.getConstantDescription()
		
		then:
		expressionController.response.contentAsString.contains("success")
		expressionController.response.contentAsString.contains("Description")
		
	}
	
	def "get data elements"() {
		
		setup:
		Initializer.createDummyStructure()
		IntegrationTestInitializer.createDataElements()
		expressionController = new ExpressionController()
		
		when:
		expressionController.params.constant = 'ele'
		expressionController.params.type = 'data-element'
		expressionController.params.controller = 'dataElement'
		def model = expressionController.getData()
		
		then:
		expressionController.response.contentAsString.contains("success")
		expressionController.response.contentAsString.contains("Element 1")
		
	}
		
	def "get data element description"() {
		
		setup:
		Initializer.createDummyStructure()
		IntegrationTestInitializer.createDataElements()
		expressionController = new ExpressionController()
		
		when:
		expressionController.params.dataElement = DataElement.findByCode("CODE").id+''
		def model = expressionController.getDataElementDescription()
		
		then:
		expressionController.response.contentAsString.contains("success")
		expressionController.response.contentAsString.contains("Description")
		
	}
	
}

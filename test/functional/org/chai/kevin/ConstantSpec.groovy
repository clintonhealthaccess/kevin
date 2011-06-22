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

class ConstantSpec extends GebTests {

	def setup() {
		Initializer.createDummyStructure();
		Initializer.createDataElementsAndExpressions();
	}
	

	def "edit constant works"() {
		when:
			browser.to(ConstantPage)
			editConstant("Constant")
		
		then:
			browser.at(ConstantPage)
			createConstant.entityFormContainer.displayed
		
	}

	def "add constant works"() {
		when:
			browser.to(ConstantPage)
			addConstant()
			
		then:
			browser.at(ConstantPage)
			createConstant.entityFormContainer.displayed
	}
	
	def "cancel new constant"() {
		when:
			browser.to(ConstantPage)
			addConstant()
			createConstant.cancel()
		
		then:
			browser.at(ConstantPage)
			!createConstant.entityFormContainer.displayed
	}
	
	def "save new empty constant displays error"() {
		when:
			browser.to(ConstantPage)
			addConstant()
			createConstant.save()
		
		then:
			browser.at(ConstantPage)
			createConstant.entityFormContainer.displayed
			createConstant.hasError(createConstant.codeField)
			createConstant.hasError(createConstant.valueField)
	}
	
	def "save new constant displays it on page"() {
		when:
			browser.to(ConstantPage)
			addConstant()
			createConstant.nameField.value("Test Constant")
			createConstant.codeField.value("TESTCONST")
			createConstant.valueField.value("100")
			createConstant.save()
		
		then:
			browser.at(ConstantPage)
			constants.displayed
			hasConstant("Test Constant")
	}
	
	
}

package org.chai.kevin.cost

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

import org.chai.kevin.GebTests;
import org.chai.kevin.Initializer;

import grails.plugin.geb.GebSpec;

class CostSpec extends GebTests {

	def transactional = true
	
	def setupSpec() {
		Initializer.createDummyStructure();
		Initializer.createDataElementsAndExpressions();
		Initializer.createCost();
	}
	
	def "costing page works"() {
		when:
			browser.to(CostPage)
			
		then:
			browser.at(CostPage)
			
		when:
			pickObjective("Geographical Access")
			
		then:
			browser.at(CostPage)
			
		when:
			pickOrganisation("Burera")
			
		then:
			browser.at(CostPage)
	}
	
	def "cancel new expression and save target works"() {
		when:
			browser.to(CostPage)
			pickOrganisation("Burera")
			pickObjective("Geographical Access")
			addTarget()
			createTarget.addExpression()
			createTarget.createExpression.cancel()
			createTarget.codeField.value("TARGET")
			createTarget.nameField.value("Test Target")
			createTarget.orderField.value("10")
			createTarget.expressionFields.first().value("1")
			createTarget.save()
			
		then:
			browser.at(CostPage)
			costTable.displayed
			getTarget("Test Target").unique().text().contains "Test Target"
	}
	
	def "add target gets displayed"() {
		when:
			browser.to(CostPage)
			pickOrganisation("Burera")
			pickObjective("Geographical Access")
			addTarget()
			
		then:
			browser.at(CostPage)
			createTarget.saveButton.present
			!createTarget.hasError(createTarget.codeField)
			createTarget.hasExpression("Constant 10")
	}
	
	def "edit target gets displayed"() {
		when:
			browser.to(CostPage)
			pickOrganisation("Burera")
			pickObjective("Geographical Access")
			editTarget()
			
		then:
			browser.at(CostPage)
			createTarget.saveButton.present
			!createTarget.hasError(createTarget.codeField)
	}
	
	def "add empty target displays error"() {
		when:
			browser.to(CostPage)
			pickOrganisation("Burera")
			pickObjective("Geographical Access")
			addTarget()
			createTarget.save()
			
		then:
			browser.at(CostPage)
			!costTable.displayed
			createTarget.hasError(createTarget.codeField)
	}
	
	def "add targets displays it on page"() {
		when:
			browser.to(CostPage)
			pickOrganisation("Burera")
			pickObjective("Geographical Access")
			addTarget()
			createTarget.nameField.value("Test Target 2")
			createTarget.codeField.value("TARGET2")
			createTarget.orderField.value("11")
			createTarget.expressionFields.first().value("1")
			createTarget.save()
			
		then:
			browser.at(CostPage)
			costTable.displayed
			getTarget("Test Target 2").unique().text().contains "Test Target 2"
	}
		
	def "add empty expression displays error"() {
		when:
			browser.to(CostPage)
			pickOrganisation("Burera")
			pickObjective("Geographical Access")
			addTarget()
			createTarget.addExpression()
			createTarget.createExpression.save()
			
		then:
			browser.at(CostPage)
			createTarget.createExpression.hasError(createTarget.createExpression.codeField)
			createTarget.createExpression.hasError(createTarget.createExpression.expressionField)
	}
	
	def "cancel new expression and save empty target displays error"() {
		when:
			browser.to(CostPage)
			pickOrganisation("Burera")
			pickObjective("Geographical Access")
			addTarget()
			createTarget.addExpression()
			createTarget.createExpression.cancel()
			createTarget.save()
			
		then:
			browser.at(CostPage)
			createTarget.hasError(createTarget.codeField)
	}

}

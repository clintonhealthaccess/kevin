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

import grails.validation.ValidationException

import org.chai.kevin.cost.CostTarget.CostType
import org.chai.kevin.data.Type


abstract class CostDomainSpec extends CostIntegrationTests {

	def "program constraint: code cannot be null"() {
		when:
		newReportProgram(CODE(1))
		
		then:
		ReportProgram.count() == 1
		
		when:
		newReportProgram(null)
		
		then:
		thrown ValidationException
	}
	
	def "target constraint: code cannot be null"() {
		when:
		newCostTarget(CODE(1), newExpression(CODE(2), Type.TYPE_NUMBER(), "1"), CONSTANT_RAMP_UP(), [], CostType.INVESTMENT, null)
		
		then:
		CostTarget.count() == 1
		
		when:
		newCostTarget(null, newExpression(CODE(2), Type.TYPE_NUMBER(), "1"), CONSTANT_RAMP_UP(), [], CostType.INVESTMENT, null)
		
		then:
		thrown ValidationException
	}
	
	def "target constraint: expression cannot be null"() {
		when:
		newCostTarget(CODE(1), newExpression(CODE(2), Type.TYPE_NUMBER(), "1"), CONSTANT_RAMP_UP(), [], CostType.INVESTMENT, null)
		
		then:
		CostTarget.count() == 1
		
		when:
		newCostTarget(CODE(2), null, CONSTANT_RAMP_UP(), [], CostType.INVESTMENT, null)
		
		then:
		thrown ValidationException
	}
	
	
	def "ramp up constraint: code cannot be null"() {
		when:
		newCostRampUp(CODE(1), [:])
		
		then:
		CostRampUp.count() == 1
		
		when:
		new CostRampUp(code: null, years: [:]).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "ramp up year constraint: year cannot be null"() {
		when:
		newCostRampUpYear(null, 1.0d)
		
		then:
		thrown ValidationException
	}
	
	def "ramp up year constraint: value cannot be null"() {
		when:
		newCostRampUpYear(1, null)
		
		then:
		thrown ValidationException
	}
	
	def "delete program cascade deletes target"() {
		when:
		def costProgram = newReportProgram(CODE(1))
		newCostTarget(CODE(1), newExpression(CODE(2), Type.TYPE_NUMBER(), "1"), CONSTANT_RAMP_UP(), [], CostType.INVESTMENT, costProgram)
		costProgram.delete();
		
		then:
		ReportProgram.count() == 0
		CostTarget.count() == 0
	}
	
	def "save program saves target"() {
		when:
		def expression = newExpression(CODE(2), Type.TYPE_NUMBER(), "1")
		def costProgram = newReportProgram(CODE(1))
		costProgram.addTarget new CostTarget(names:j(["en":"Test Target"]), code:CODE(1), expression: expression, costRampUp: CONSTANT_RAMP_UP(), costType: CostType.INVESTMENT)
		costProgram.save();
		
		then:
		ReportProgram.count() == 1
		CostTarget.count() == 1
	}
	
	
	def "save target preserves order"() {
		when:
		def costProgram = newReportProgram(CODE(1))
		def expression = newExpression(CODE(2), Type.TYPE_NUMBER(), "1")
		costProgram.addTarget new CostTarget(names:j(["en":"Test 4"]), code:CODE(1), expression: expression, costRampUp: CONSTANT_RAMP_UP(), costType: CostType.INVESTMENT, order: 4)
		costProgram.addTarget new CostTarget(names:j(["en":"Test 3"]), code:CODE(2), expression: expression, costRampUp: CONSTANT_RAMP_UP(), costType: CostType.INVESTMENT, order: 3)
		costProgram.save();
		
		then:
		def expectedProgram = ReportProgram.findByCode("TEST");
		expectedProgram.targets[0].order == 3
		expectedProgram.targets[1].order == 4

	}
	
	
}

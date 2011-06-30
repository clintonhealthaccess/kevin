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

import grails.validation.ValidationException;

import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.cost.CostTarget.CostType;
import org.chai.kevin.data.Expression;
import org.springframework.dao.DataIntegrityViolationException;

class DomainSpec extends IntegrationTests {

	def setup() {
		Initializer.createDummyStructure()
		IntegrationTestInitializer.createExpressions()
		
		new CostRampUp(names:j(["en":"Constant"]), code:"CONST", years: [
			1: new CostRampUpYear(year: 1, value: 0.2),
			2: new CostRampUpYear(year: 2, value: 0.2),
			3: new CostRampUpYear(year: 3, value: 0.2),
			4: new CostRampUpYear(year: 4, value: 0.2),
			5: new CostRampUpYear(year: 5, value: 0.2)
		]).save(failOnError: true);
	
		def costObjective = new CostObjective(names:j(["en":"Human Resources for Health"]), code:"HRH")
		costObjective.addTarget new CostTarget(names:j(["en":"Training"]), code:"TRAINING", expression: Expression.findByCode("CONST10"), costRampUp: CostRampUp.findByCode("CONST"), costType: CostType.INVESTMENT)
		costObjective.save(failOnError: true)
	}
	
	def "objective constraint: code cannot be null"() {
		when:
		new CostObjective(names:j(["en":"Test Obejctive"]), code:"CODE").save(failOnError:true)
		
		then:
		CostObjective.count() == 2
		
		when:
		new CostObjective(names:j(["en":"Test Obejctive"])).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
	def "target constrant: code cannot be null"() {
		when:
		new CostTarget(names:j(["en":"Test Target"]), code:"CODE", expression: Expression.findByCode("CONST10"), costRampUp: CostRampUp.findByCode("CONST"), costType: CostType.INVESTMENT).save(failOnError:true)
		
		then:
		CostTarget.count() == 2
		
		when:
		new CostTarget(names:j(["en":"Test Target"]), expression: Expression.findByCode("CONST10"), costRampUp: CostRampUp.findByCode("CONST"), costType: CostType.INVESTMENT).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
	def "target constraint: expression cannot be null"() {
		when:
		new CostTarget(names:j(["en":"Test Target"]), code:"CODE", expression: Expression.findByCode("CONST10"), costRampUp: CostRampUp.findByCode("CONST"), costType: CostType.INVESTMENT).save(failOnError:true)
		
		then:
		CostTarget.count() == 2
		
		when:
		new CostTarget(names:j(["en":"Test Target"]), code:"CODE", costRampUp: CostRampUp.findByCode("CONST"), costType: CostType.INVESTMENT).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
	
	def "ramp up constraint: code cannot be null"() {
		when:
		new CostRampUp(code:"CODE").save(failOnError: true)
		
		then:
		CostRampUp.count() == 2
		
		when:
		new CostRampUp().save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "ramp up year constraint: year cannot be null"() {
		when:
		new CostRampUp(names:j(["en":"Test"]), years: [new CostRampUpYear(value: 1.0d)]).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "ramp up year constraint: value cannot be null"() {
		when:
		new CostRampUp(names:j(["en":"Test"]), years: [new CostRampUpYear(year: 1)]).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "ramp up year constraint: value must be integer"() {
		when:
		new CostRampUp(names:j(["en":"Test"]), years: [new CostRampUpYear(year: "test")]).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "delete objective cascade deletes target"() {
		when:
		def costObjective = CostObjective.findByCode("HRH");
		costObjective.delete();
		
		then:
		CostObjective.count() == 0
		CostTarget.count() == 0
	}
	
	def "save objective saves target"() {
		when:
		def costObjective = new CostObjective(names:j(["en":"Test Objective"]), code:"TEST")
		costObjective.addTarget new CostTarget(names:j(["en":"Test Target"]), code:"TEST", expression: Expression.findByCode("CONST10"), costRampUp: CostRampUp.findByCode("CONST"), costType: CostType.INVESTMENT)
		costObjective.save();
		
		then:
		CostObjective.count() == 2
		CostTarget.count() == 2
	}
	
	
	def "save target preserves order"() {
		when:
		def costObjective = new CostObjective(names:j(["en":"Test Objective"]), code:"TEST")
		costObjective.addTarget new CostTarget(names:j(["en":"Test 4"]), code:"TEST1", expression: Expression.findByCode("CONST10"), costRampUp: CostRampUp.findByCode("CONST"), costType: CostType.INVESTMENT, order: 4)
		costObjective.addTarget new CostTarget(names:j(["en":"Test 3"]), code:"TEST2", expression: Expression.findByCode("CONST10"), costRampUp: CostRampUp.findByCode("CONST"), costType: CostType.INVESTMENT, order: 3)
		costObjective.save();
		
		then:
		def expectedObjective = CostObjective.findByCode("TEST");
		expectedObjective.targets[0].order == 3
		expectedObjective.targets[1].order == 4

	}
	
	def "save target preserves groups"() {
		when:
		new CostTarget(names:j(["en":"Test Target"]), code:"TEST", groupUuidString: "group1", expression: Expression.findByCode("CONST10"), costRampUp: CostRampUp.findByCode("CONST"), costType: CostType.INVESTMENT, order: 4).save(failOnError: true)
		
		then:
		CostService.getGroupUuids(CostTarget.findByCode("TEST").groupUuidString).size() == 1
	}
	
	def "save target erases old groups"() {
		when:
		new CostTarget(names:j(["en":"Test Target"]), code:"TEST", groupUuidString: "group1", expression: Expression.findByCode("CONST10"), costRampUp: CostRampUp.findByCode("CONST"), costType: CostType.INVESTMENT, order: 4).save(failOnError: true)
		CostTarget.findByCode("TEST").groupUuidString = "group2"
		
		then:
		def target = CostTarget.findByCode("TEST")
		CostService.getGroupUuids(target.groupUuidString).size() == 1
		(new HashSet(["group2"])).equals(CostService.getGroupUuids(target.groupUuidString))
	}
	
	
	
}

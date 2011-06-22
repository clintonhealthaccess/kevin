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

import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ExpressionValue;
import org.chai.kevin.value.ExpressionValue.Status;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class ValueServiceSpec extends IntegrationTests {

	def valueService;
	
	def expressionService;
	
	def setup() {
		IntegrationTestInitializer.createDummyStructure()
	}
	
	def "test get non calculated expressions"() {
		setup:
		new Expression(
			names:j(["en":"Constant 10"]), 
			descriptions:j([:]), 
			code:"Constant 10", expression: "10", 
			type: ValueType.VALUE, 
			timestamp:new Date()
		).save(failOnError: true)
		
		expect:
		List<ExpressionValue> values = valueService.getNonCalculatedExpressions();
		values.size() == OrganisationUnit.count() * Period.count();
	}
	
	def "test get non calculated expressions when already one expression value"() {
		setup:
		new Expression(
			names:j(["en":"Constant 10"]),
			descriptions:j([:]),
			code:"Constant 10", expression: "10",
			type: ValueType.VALUE,
			timestamp:new Date()
		).save(failOnError: true)
		
		new ExpressionValue(
			period: Period.list()[0],
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			timestamp: new Date(),
			status: Status.VALID,
			value: "10",
			expression: Expression.findByCode("Constant 10")
		).save(failOnError: true)
		
		expect:
		List<ExpressionValue> values = valueService.getNonCalculatedExpressions();
		values.size() == OrganisationUnit.count() * Period.count() - 1;
	}
	
	
	def "test outdated expressions"() {
		setup:
		new Expression(
			names:j(["en":"Constant 10"]),
			descriptions:j([:]),
			code:"Constant 10", expression: "10",
			type: ValueType.VALUE,
		).save(failOnError: true)
		new ExpressionValue(
			period: Period.list()[0],
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			status: Status.VALID,
			value: "10",
			expression: Expression.findByCode("Constant 10")
		).save(failOnError: true)
		
		when:
		List<ExpressionValue> values = valueService.getOutdatedExpressions();
		
		then:
		values.size() == 0
		
		when:
		def expression = Expression.findByCode("Constant 10")
		expression.setTimestamp(new Date());
		expression.save(failOnError: true);
		values = valueService.getOutdatedExpressions();
		
		then:
		values.size() == 1
		values.get(0).expression.equals(Expression.findByCode("Constant 10"));
	}
	
	def "test get non calculated calculations"() {
		setup:
		new Expression(
			names:j(["en":"Constant 10"]),
			descriptions:j([:]),
			code:"Constant 10", expression: "10",
			type: ValueType.VALUE,
			timestamp:new Date()
		).save(failOnError: true)
		new Calculation(expressions: [
			"District Hospital": Expression.findByCode("Constant 10"),
			"Health Center": Expression.findByCode("Constant 10")
		], timestamp:new Date()).save(failOnError: true)
		
		expect:
		List<CalculationValue> values = valueService.getNonCalculatedCalculations();
		values.size() == OrganisationUnit.count() * Period.count();
	}
	
	def "test outdated calculations"() {
		setup:
		new Expression(
			names:j(["en":"Constant 10"]),
			descriptions:j([:]),
			code:"Constant 10", expression: "10",
			type: ValueType.VALUE,
			timestamp:new Date()
		).save(failOnError: true)
		new Calculation(expressions: [
			"District Hospital": Expression.findByCode("Constant 10"),
			"Health Center": Expression.findByCode("Constant 10")
		], timestamp:new Date()).save(failOnError: true)
		new CalculationValue(
			period: Period.list()[0],
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			timestamp: new Date(),
			average: "10",
			calculation: Calculation.list()[0],
			hasMissingExpression: false,
			hasMissingValues: false
		).save(failOnError: true)
		
		when:
		List<CalculationValue> values = valueService.getOutdatedCalculations();
		
		then:
		values.size() == 0
		
		when:
		def calculation = Calculation.list()[0]
		calculation.setTimestamp(new Date());
		calculation.save(failOnError: true, flush: true);
		values = valueService.getOutdatedCalculations();
		
		then:
		values.size() == 1
		values.get(0).calculation.equals(Calculation.list()[0]);
	}
	
	def "test outdated calculations when expression is saved"() {
		setup:
		new Expression(
			names:j(["en":"Constant 10"]),
			descriptions:j([:]),
			code:"Constant 10", expression: "10",
			type: ValueType.VALUE,
			timestamp:new Date()
		).save(failOnError: true)
		new Calculation(expressions: [
			"District Hospital": Expression.findByCode("Constant 10"),
			"Health Center": Expression.findByCode("Constant 10")
		], timestamp:new Date()).save(failOnError: true)
		new CalculationValue(
			period: Period.list()[0],
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			timestamp: new Date(),
			average: "10",
			calculation: Calculation.list()[0],
			hasMissingExpression: false,
			hasMissingValues: false
		).save(failOnError: true)
		
		when:
		List<CalculationValue> values = valueService.getOutdatedCalculations();
		
		then:
		values.size() == 0
		
		when:
		def expression = Expression.findByCode("Constant 10")
		expression.setTimestamp(new Date());
		expression.save(failOnError: true, flush: true)
		values = valueService.getOutdatedCalculations();
		
		then:
		values.size() == 1
		values.get(0).calculation.equals(Calculation.list()[0]);
	}
	
}

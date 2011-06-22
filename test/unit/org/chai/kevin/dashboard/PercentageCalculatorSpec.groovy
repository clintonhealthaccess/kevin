package org.chai.kevin.dashboard;

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

import java.util.Date;


import grails.plugin.spock.UnitSpec
import groovy.mock.interceptor.MockFor
import groovy.mock.interceptor.StubFor;

import org.chai.kevin.Calculation;
import org.chai.kevin.Expression;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.Organisation;
import org.chai.kevin.UnitTests;
import org.chai.kevin.ValueType;
import org.gmock.WithGMock;
import org.hisp.dhis.aggregation.AggregationService
import org.chai.kevin.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit
import org.hisp.dhis.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType

@WithGMock
class PercentageCalculatorSpec extends UnitTests {

	PercentageCalculator percentageCalculator;

	def setup() {
		addBasicData()
		
		// indicators
//		def one = new IndicatorType(names:j(["en":"one"]), factor: 1)
//		mockDomain(IndicatorType, [one])
//		def const10 = new Indicator(names:j(["en":"Constant 10"]), shortName: "Constant 10", code: "CONST10", numerator: "10", denominator: "1", indicatorType: IndicatorType.findByName("one"))
//		def const20 = new Indicator(names:j(["en":"Constant 20"]), shortName: "Constant 20", code: "CONST20", numerator: "20", denominator: "1", indicatorType: IndicatorType.findByName("one"))
		def const10 = new Expression(names:j(["en":"Constant 10"]), code:"CONST10", expression: "10", type: ValueType.VALUE)
		def const20 = new Expression(names:j(["en":"Constant 20"]), code:"CONST20", expression: "20", type: ValueType.VALUE)
		mockDomain(Expression, [const10, const20])
		
		// objectives and targets for dashboard
		
		def calculation1 = new Calculation(expressions: [
			"District Hospital": Expression.findByCode("CONST10"),
		], timestamp:new Date())
		def calculation2 = new Calculation(expressions: [
			"District Hospital": Expression.findByCode("CONST20"),
		], timestamp:new Date())
		
		mockDomain(Calculation, [calculation1, calculation2])
		def nursea1 = new DashboardTarget(
			names:j(["en":"Nurse A1"]), code:"A1", descriptions:j(["en":"Nurse A1"]),
			calculation: calculation1
		)
		def nursea2 = new DashboardTarget(
			names:j(["en":"Nurse A2"]), code:"A2", descriptions:j(["en":"Nurse A2"]),
			calculation: calculation2
		)
		mockDomain(DashboardTarget, [nursea1, nursea2])
		def staffing = new DashboardObjective(root: false, names:j(["en":"Staffing"]), code:"STAFFING", descriptions:j(["en":"Staffing"]), objectiveEntries: [])
		staffing.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardTarget.findByCode("A1"), weight: 1, order: 1)
		staffing.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardTarget.findByCode("A2"), weight: 1, order: 2)
		mockDomain(DashboardObjective, [staffing])
		
		
		
		def monthly = new MonthlyPeriodType();
		mockDomain(MonthlyPeriodType, [monthly])
		def period = new Period(periodType: monthly, startDate: mar01, endDate: mar31)
		mockDomain(Period, [period])
		
	}
	
	def "test strategic target"() {
		setup:
		def expressionService = mock(ExpressionService)
		expressionService.getValue(match {true}, match {true}, match {true}, match {true}).returns(10.0d);
		def percentageCalculator = new PercentageCalculator();
		percentageCalculator.expressionService = expressionService;
		
		when:
		DashboardPercentage percentage
		play {
			percentage = percentageCalculator.getPercentage(DashboardTarget.findByCode('A1'), new Organisation(OrganisationUnit.findByName('Butaro DH')), Period.list()[0]);
		}
		
		then:
		percentage.status == Status.VALID
		percentage.value == 10

	}
	
	def "test strategic objective"() {
		
		setup:		
		def percentageService = mock(PercentageService);
		percentageService.getPercentage(match {true}, match {true}, match {true}).returns(new DashboardPercentage(10.0d, null, null, null));
		percentageService.getPercentage(match {true}, match {true}, match {true}).returns(new DashboardPercentage(20.0d, null, null, null));
		def percentageCalculator = new PercentageCalculator();
		percentageCalculator.percentageService = percentageService;
		
		when:
		DashboardPercentage percentage = null;
		play {
			percentage = percentageCalculator.getPercentage(DashboardObjective.findByCode('STAFFING'), new Organisation(OrganisationUnit.findByName('Butaro DH')), Period.list()[0]);
		}
		
		then:
		percentage.valid == true
		percentage.status == Status.VALID
		percentage.value == 15
	}
	
	def "test absent value on target"() {
		setup:
		def dataElement = new DataElement(names:j(["en":"Element 1"]), code: "CODE", type: ValueType.VALUE)
		mockDomain(DataElement, [dataElement])
		def expression = new Expression(names:j(["en":"Expression Element 1"]), code:"ELEM1", expression: "["+dataElement.id+"]", denominator: "1", type: ValueType.VALUE)
		mockDomain(Expression, [expression])
		
		def target = new DashboardTarget(
			names:j(["en":"Target 1"]), code:"TARGET1", descriptions:j(["en":"Target 1"]),
			calculations: ["District Hospital":
				new Calculation(groupUuid: "District Hospital", expression: Expression.findByCode("CODE")),
			]
		)
		mockDomain(DashboardTarget, [target])
		
		def expressionService = mock(ExpressionService);
		expressionService.getValue(match {true}, match {true}, match {true}, match {it[dataElement] = null; true}).returns(null);
		def percentageCalculator = new PercentageCalculator();
		percentageCalculator.expressionService = expressionService
		def organisation = new Organisation(OrganisationUnit.findByName('Butaro DH'))
		organisation.children = []

		when:
		DashboardPercentage percentage
		play {
			percentage = percentageCalculator.getPercentage(DashboardTarget.findByCode('TARGET1'), organisation, Period.list()[0]);
		}
		
		then:
		percentage.status == Status.MISSING_VALUE
		percentage.hasMissingValue == true
	}
	
	def "test absent orgunit group on target"() {
		setup:
		def percentageCalculator = new PercentageCalculator();
		def organisation = new Organisation(OrganisationUnit.findByName('Kivuye HC'));
		organisation.children = new ArrayList();
		
		when:
		DashboardPercentage percentage = percentageCalculator.getPercentage(DashboardTarget.findByCode('A1'), organisation, Period.list()[0]);
		
		then:
		percentage.status == Status.MISSING_EXPRESSION
	}
	
	def "test absent orgunit group on objective"() {
		setup:
		def percentageCalculator = new PercentageCalculator();
		def percentageService = mock(PercentageService);
		percentageService.getPercentage(match {true}, match {true}, match {true}).returns(new DashboardPercentage(Status.MISSING_EXPRESSION, null, null, null));
		percentageService.getPercentage(match {true}, match {true}, match {true}).returns(new DashboardPercentage(Status.MISSING_EXPRESSION, null, null, null));
		percentageCalculator.percentageService = percentageService;
		
		when:
		DashboardPercentage percentage;
		play {
			percentage = percentageCalculator.getPercentage(DashboardObjective.findByCode('STAFFING'), new Organisation(OrganisationUnit.findByName('Kivuye HC')), Period.list()[0]);
		}
		
		then:
		percentage.valid == false
		percentage.isHasMissingExpression() == true
	}
	
	def "test absent with non absent orgunit group on objective"() {
		setup:
		def percentageService = mock(PercentageService);
		percentageService.getPercentage(match {true}, match {true}, match {true}).returns(new DashboardPercentage(10.0d, null, null, null));
		percentageService.getPercentage(match {true}, match {true}, match {true}).returns(new DashboardPercentage(20.0d, null, null, null));
		def percentageCalculator = new PercentageCalculator();
		percentageCalculator.percentageService = percentageService;
		
		when:
		DashboardPercentage percentage;
		play {
			percentage = percentageCalculator.getPercentage(DashboardObjective.findByCode('STAFFING'), new Organisation(OrganisationUnit.findByName('Burera')), Period.list()[0]);
		}
		
		then:
		percentage.valid == true
		percentage.value == 15
	}

	
	public static Date getDate( int year, int month, int day )
	{
		final Calendar calendar = Calendar.getInstance();

		calendar.clear();
		calendar.set( Calendar.YEAR, year );
		calendar.set( Calendar.MONTH, month - 1 );
		calendar.set( Calendar.DAY_OF_MONTH, day );

		return calendar.getTime();
	}
}

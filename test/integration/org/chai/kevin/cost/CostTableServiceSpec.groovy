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

import java.util.List;

import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.Organisation;
import org.chai.kevin.cost.CostTarget.CostType;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Expression;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.JSONUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import grails.plugin.spock.IntegrationSpec;

class CostTableServiceSpec extends CostIntegrationTests {

	def costTableService;
	
	
	def "cost service returns expected values with no end"() {
		setup:
		setupOrganisationUnitTree()
		
		when:
		def period = newPeriod()
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER, "20")
		def costObjective = newCostObjective(CODE(2))
		def training = newCostTarget(CODE(3), expression, CONSTANT_RAMP_UP(), CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], costObjective)
		refreshExpression()
		
		def costTable = costTableService.getCostTable(period, costObjective, getOrganisation(RWANDA))

		then:
		costTable.getCost(expectedTarget, year).value == value
		
		where:
		year	| value
		1		| 4.0d
		2		| 4.0d
		3		| 4.0d
		4		| 4.0d
		5		| 4.0d
	}
	
	def "cost service returns expected values with end expression"() {
		setup:
		setupOrganisationUnitTree()
		
		when:
		def period = newPeriod()
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER, "20")
		def expressionEnd = newExpression(CODE(4), Type.TYPE_NUMBER, "40")
		def costObjective = newCostObjective(CODE(2))
		def training = newCostTarget(CODE(3), expression, expressionEnd, CONSTANT_RAMP_UP(), CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], costObjective)
		refreshExpression()
		
		def costTable = costTableService.getCostTable(period, costObjective, getOrganisation(RWANDA))

		then:
		costTable.getCost(expectedTarget, year).value == value
		
		where:
		year	| value
		1		| 4.0d
		2		| 5.0d
		3		| 6.0d
		4		| 7.0d
		5		| 8.0d
	}
	
	def "cost service returns expected years and targets"() {
		setup:
		setupOrganisationUnitTree()
		def TRAINING = "Training"
		def AVERAGE = "Average"
		
		when:
		def period = newPeriod()
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER, "20")
		def costObjective = newCostObjective(CODE(2))
		def training = newCostTarget(TRAINING, expression, CONSTANT_RAMP_UP(), CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], costObjective)
		def average = newCostTarget(AVERAGE, expression, CONSTANT_RAMP_UP(), CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], costObjective)
		refreshExpression()
		
		def costTable = costTableService.getCostTable(period, costObjective, getOrganisation(RWANDA))
		
		then:
		costTable.targets.containsAll getTargets(expectedTargets)
		costTable.years == expectedYears
		
		where:
		expectedTargets		| expectedYears
		[TRAINING, AVERAGE]	| [1, 2, 3, 4, 5]
	}
	
	
	def "missing values displayed correctly"() {
		setup:
		setupOrganisationUnitTree()
		
		when:
		def period = newPeriod()
		def dataElement = newDataElement(CODE(3), Type.TYPE_NUMBER)
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER, "\$"+dataElement.id)
		def costObjective = newCostObjective(CODE(2))
		def target = newCostTarget(CODE(4), expression, CONSTANT_RAMP_UP(), CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], costObjective)
		refreshExpression()
		
		def costTable = costTableService.getCostTable(period, costObjective, getOrganisation(RWANDA))
		
		then:
		costTable.getCost(expectedTarget, year).value == value
		
		where:
		year	| value
		1		| 0.0d
		2		| 0.0d
		3		| 0.0d
		4		| 0.0d
		5		| 0.0d
	}
	
	def "cost service takes into account only selected groups"() {
		setup:
		setupOrganisationUnitTree()
		
		when:
		def period = newPeriod()
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER, "20")
		def costObjective = newCostObjective(CODE(2))
		def training = newCostTarget(CODE(3), expression, CONSTANT_RAMP_UP(), CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP], costObjective)
		refreshExpression()
		
		def costTable = costTableService.getCostTable(period, costObjective, getOrganisation(RWANDA))

		then:
		costTable.getCost(expectedTarget, year).value == value
		
		where:
		year	| value
		1		| 2.0d
		2		| 2.0d
		3		| 2.0d
		4		| 2.0d
		5		| 2.0d
	}
	

	def "cost service returns correct explanation"() {
		setup:
		setupOrganisationUnitTree()
		
		when:
		def period = newPeriod()
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER, "20")
		def costObjective = newCostObjective(CODE(2))
		def costTarget = newCostTarget(CODE(3), expression, CONSTANT_RAMP_UP(), CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP], costObjective)
		refreshExpression()
		
		def explanation = costTableService.getExplanation(period, costTarget, getOrganisation(organisationName))
		def cost = explanation.getCost(getOrganisation(expectedOrganisationName), year)
		
		then:
		cost.value == expectedValue
		
		where:
		organisationName| expectedOrganisationName	| year	| expectedValue
		RWANDA			| NORTH						| 1		| 4.0d
		RWANDA			| NORTH						| 2		| 4.0d
		RWANDA			| NORTH						| 3		| 4.0d
		RWANDA			| NORTH						| 4		| 4.0d
		RWANDA			| NORTH						| 5		| 4.0d
		NORTH			| BURERA					| 1		| 4.0d
		NORTH			| BURERA					| 2		| 4.0d
		NORTH			| BURERA					| 3		| 4.0d
		NORTH			| BURERA					| 4		| 4.0d
		NORTH			| BURERA					| 5		| 4.0d
		BURERA			| KIVUYE					| 1		| 2.0d
		BURERA			| KIVUYE					| 2		| 2.0d
		BURERA			| KIVUYE					| 3		| 2.0d
		BURERA			| KIVUYE					| 4		| 2.0d
		BURERA			| KIVUYE					| 5		| 2.0d
		BURERA			| BUTARO					| 1		| 2.0d
		BURERA			| BUTARO					| 2		| 2.0d
		BURERA			| BUTARO					| 3		| 2.0d
		BURERA			| BUTARO					| 4		| 2.0d
		BURERA			| BUTARO					| 5		| 2.0d
		
	}
	
	def "explanation applies to correct organisation"() {
		setup:
		setupOrganisationUnitTree()
		
		when:
		def period = newPeriod()
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER, "20")
		def costObjective = newCostObjective(CODE(2))
		def costTarget = newCostTarget(CODE(3), expression, CONSTANT_RAMP_UP(), CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP], costObjective)
		refreshExpression()
		
		def explanation = costTableService.getExplanation(period, costTarget, getOrganisation(BURERA))
		
		then:
		explanation.organisations.containsAll getOrganisations([BUTARO])
		explanation.organisations.size() == 1
		
	}
	
	def getTargets(def targets) {
		def result = []
		targets.each {
			result.add CostTarget.findByCode(it)
		}
		return result
	}
	

}

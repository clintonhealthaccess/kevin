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

import org.chai.kevin.cost.CostTarget.CostType
import org.chai.kevin.data.Type
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;

class CostTableServiceSpec extends CostIntegrationTests {

	def costTableService;
	
	static def TRAINING = "Training"
	static def AVERAGE = "Average"
	
	def "cost service returns expected values with no end"() {
		setup:
		setupLocationTree()
		
		when:
		def period = newPeriod()
		def dataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"20",(HEALTH_CENTER_GROUP):"20"]]))
		def costObjective = newCostObjective(CODE(2))
		def training = newCostTarget(CODE(3), dataElement, CONSTANT_RAMP_UP(), CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], costObjective)
		refreshNormalizedDataElement()
		
		def costTable = costTableService.getCostTable(period, costObjective, LocationEntity.findByCode(RWANDA))

		then:
		costTable.getCost(training, year).value == value
		
		where:
		year	| value
		1		| 8.0d
		2		| 8.0d
		3		| 8.0d
		4		| 8.0d
		5		| 8.0d
	}
	
	def "cost service returns expected values with end expression"() {
		setup:
		setupLocationTree()
		
		when:
		def period = newPeriod()
		def dataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"20",(HEALTH_CENTER_GROUP):"20"]]))
		def dataElementEnd = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"40",(HEALTH_CENTER_GROUP):"40"]]))
		def costObjective = newCostObjective(CODE(2))
		def training = newCostTarget(CODE(3), dataElement, dataElementEnd, CONSTANT_RAMP_UP(), CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], costObjective)
		refreshNormalizedDataElement()
		
		def costTable = costTableService.getCostTable(period, costObjective, LocationEntity.findByCode(RWANDA))

		then:
		costTable.getCost(training, year).value == value
		
		where:
		year	| value
		1		| 8.0d
		2		| 10.0d
		3		| 12.0d
		4		| 14.0d
		5		| 16.0d
	}
	
	def "cost service returns expected years and targets"() {
		setup:
		setupLocationTree()
		
		when:
		def period = newPeriod()
		def dataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"20",(HEALTH_CENTER_GROUP):"20"]]))
		def costObjective = newCostObjective(CODE(2))
		def rampUp = CONSTANT_RAMP_UP()
		def training = newCostTarget(TRAINING, dataElement, rampUp, CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], costObjective)
		def average = newCostTarget(AVERAGE, dataElement, rampUp, CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], costObjective)
		refreshNormalizedDataElement()
		
		def costTable = costTableService.getCostTable(period, costObjective, LocationEntity.findByCode(RWANDA))
		
		then:
		costTable.targets.containsAll getTargets(expectedTargets)
		costTable.years == expectedYears
		
		where:
		expectedTargets		| expectedYears
		[TRAINING, AVERAGE]	| [1, 2, 3, 4, 5]
	}
	
	
	def "missing values displayed correctly"() {
		setup:
		setupLocationTree()
		
		when:
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def costObjective = newCostObjective(CODE(2))
		def target = newCostTarget(CODE(4), dataElement, CONSTANT_RAMP_UP(), CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], costObjective)
		refreshNormalizedDataElement()
		
		def costTable = costTableService.getCostTable(period, costObjective, LocationEntity.findByCode(RWANDA))
		
		then:
		costTable.getCost(target, year).value == value
		
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
		setupLocationTree()
		
		when:
		def period = newPeriod()
		def dataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"20",(HEALTH_CENTER_GROUP):"20"]]))
		def costObjective = newCostObjective(CODE(2))
		def training = newCostTarget(CODE(3), dataElement, CONSTANT_RAMP_UP(), CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP], costObjective)
		refreshNormalizedDataElement()
		
		def costTable = costTableService.getCostTable(period, costObjective, LocationEntity.findByCode(RWANDA))

		then:
		costTable.getCost(training, year).value == value
		
		where:
		year	| value
		1		| 4.0d
		2		| 4.0d
		3		| 4.0d
		4		| 4.0d
		5		| 4.0d
	}
	

	def "cost service returns correct explanation"() {
		setup:
		setupLocationTree()
		
		when:
		def period = newPeriod()
		def dataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"20",(HEALTH_CENTER_GROUP):"20"]]))
		def costObjective = newCostObjective(CODE(2))
		def costTarget = newCostTarget(CODE(3), dataElement, CONSTANT_RAMP_UP(), CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], costObjective)
		refreshNormalizedDataElement()
		
		def explanation = costTableService.getExplanation(period, costTarget, LocationEntity.findByCode(entity))
		def cost = explanation.getCost(getCalculationEntity(expectedEntity), year)
		
		then:
		cost.value == expectedValue
		
		where:
		entity		| expectedEntity	| year	| expectedValue
		RWANDA		| NORTH				| 1		| 8.0d
		RWANDA		| NORTH				| 2		| 8.0d
		RWANDA		| NORTH				| 3		| 8.0d
		RWANDA		| NORTH				| 4		| 8.0d
		RWANDA		| NORTH				| 5		| 8.0d
		NORTH		| BURERA			| 1		| 8.0d
		NORTH		| BURERA			| 2		| 8.0d
		NORTH		| BURERA			| 3		| 8.0d
		NORTH		| BURERA			| 4		| 8.0d
		NORTH		| BURERA			| 5		| 8.0d
		BURERA		| KIVUYE			| 1		| 4.0d
		BURERA		| KIVUYE			| 2		| 4.0d
		BURERA		| KIVUYE			| 3		| 4.0d
		BURERA		| KIVUYE			| 4		| 4.0d
		BURERA		| KIVUYE			| 5		| 4.0d
		BURERA		| BUTARO			| 1		| 4.0d
		BURERA		| BUTARO			| 2		| 4.0d
		BURERA		| BUTARO			| 3		| 4.0d
		BURERA		| BUTARO			| 4		| 4.0d
		BURERA		| BUTARO			| 5		| 4.0d
		
	}
	
	def "explanation applies to correct organisation"() {
		setup:
		setupLocationTree()
		
		when:
		def period = newPeriod()
		def dataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"20",(HEALTH_CENTER_GROUP):"20"]]))
		def costObjective = newCostObjective(CODE(2))
		def costTarget = newCostTarget(CODE(3), dataElement, CONSTANT_RAMP_UP(), CostType.INVESTMENT, [DISTRICT_HOSPITAL_GROUP], costObjective)
		refreshNormalizedDataElement()
		
		def explanation = costTableService.getExplanation(period, costTarget, LocationEntity.findByCode(BURERA))
		
		then:
		explanation.organisations.containsAll([DataEntity.findByCode(BUTARO)])
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

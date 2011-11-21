package org.chai.kevin.cost

import grails.validation.ValidationException;

import org.chai.kevin.cost.CostTarget.CostType;
import org.chai.kevin.data.Type;

class CostTargetSpec extends CostIntegrationTests {

	def "cannot save target with null data element"() {
		when:
		def rampUp = newCostRampUp(CODE(1), [:])
		new CostTarget(costRampUp: rampUp, costType: CostType.INVESTMENT, code: CODE(2)).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		def dataElement = newDataElement(CODE(3), Type.TYPE_NUMBER())
		new CostTarget(costRampUp: rampUp, costType: CostType.INVESTMENT, code: CODE(4), dataElement: dataElement).save(failOnError: true)
		
		then:
		CostTarget.count() == 1
	}
	
	def "cannot save target with null type"() {
		when:
		def rampUp = newCostRampUp(CODE(1), [:])
		def dataElement = newDataElement(CODE(3), Type.TYPE_NUMBER())
		new CostTarget(costRampUp: rampUp, code: CODE(2), dataElement: dataElement).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new CostTarget(costRampUp: rampUp, costType: CostType.INVESTMENT, code: CODE(4), dataElement: dataElement).save(failOnError: true)
		
		then:
		CostTarget.count() == 1
	}
	
	def "cannot save target with null ramp up"() {
		when:
		def dataElement = newDataElement(CODE(3), Type.TYPE_NUMBER())
		new CostTarget(code: CODE(2), costType: CostType.INVESTMENT, dataElement: dataElement).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		def rampUp = newCostRampUp(CODE(1), [:])
		new CostTarget(costRampUp: rampUp, costType: CostType.INVESTMENT, code: CODE(4), dataElement: dataElement).save(failOnError: true)
		
		then:
		CostTarget.count() == 1
	}
	
	
}

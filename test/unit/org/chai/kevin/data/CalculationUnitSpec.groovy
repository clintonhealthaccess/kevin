package org.chai.kevin.data

import org.chai.kevin.data.Aggregation;
import org.chai.kevin.data.Summ;
import org.chai.kevin.value.AggregationPartialValue;

import grails.plugin.spock.UnitSpec;

class CalculationUnitSpec extends UnitSpec {

	def "equals"() {
		
		when:
		def calculation1 = new Summ(code: '1', expression: "1")
		
		then:
		calculation1.equals(calculation1)
		
		when:
		def calculation2 = new Summ(code: '2', expression: "1")
		
		then:
		!calculation2.equals(calculation1)
		
		when:
		def calculation3 = new Aggregation(code: '1', expression: '1')
		
		then:
		!calculation3.equals(calculation1)
	}
	
	def "partial expressions in calculation"() {
		setup:
		def calculation = null
		
		when:
		calculation = new Summ(expression: "1")
		
		then:
		calculation.getPartialExpressions().equals(["1"])
		
		when:
		calculation = new Summ(expression: "1")
		
		then:
		calculation.getPartialExpressions().equals(["1"])
		
		when:
		calculation = new Aggregation(expression: "1")
		
		then:
		calculation.getPartialExpressions().equals([])
		
		when:
		calculation = new Aggregation(expression: "\$1/\$2")
		
		then:
		calculation.getPartialExpressions().equals(["\$1", "\$2"])
	}
	
	
}

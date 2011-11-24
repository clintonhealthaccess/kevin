package org.chai.kevin

import org.chai.kevin.data.Aggregation;
import org.chai.kevin.data.Average;
import org.chai.kevin.data.Sum;
import org.chai.kevin.value.AggregationPartialValue;

import grails.plugin.spock.UnitSpec;

class CalculationUnitSpec extends UnitSpec {

	def "partial expressions in calculation"() {
		setup:
		def calculation = null
		
		when:
		calculation = new Average(expression: "1")
		
		then:
		calculation.getPartialExpressions().equals(["1"])
		
		when:
		calculation = new Sum(expression: "1")
		
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

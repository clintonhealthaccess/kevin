package org.chai.kevin.data

import org.chai.kevin.Period;
import org.chai.kevin.data.Aggregation;
import org.chai.kevin.data.Summ;
import org.chai.kevin.value.AggregationPartialValue;
import org.chai.kevin.value.SumPartialValue;
import org.chai.location.DataLocationType;
import org.chai.location.Location;

import grails.plugin.spock.UnitSpec;

class CalculationUnitSpec extends UnitSpec {

	def "equals"() {
		
		when:
		def calculation1 = new Summ(code: '1', expression: "1")
		
		then:
		calculation1.equals(calculation1)
		calculation1.hashCode() == calculation1.hashCode()
		
		when:
		def calculation2 = new Summ(code: '2', expression: "1")
		
		then:
		!calculation2.equals(calculation1)
		calculation2.hashCode() != calculation1.hashCode()
		
		when:
		def calculation3 = new Aggregation(code: '1', expression: '1')
		
		then:
		// this is equal because code is equal
		calculation3.equals(calculation1)
		calculation3.hashCode() == calculation1.hashCode()
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
	
	def "partial values equals"() {
		setup:
		def location = new Location(code: 'location')
		def period = new Period(code: 'period')
		def data = new Summ(code: 'sum')
		def type1 = new DataLocationType(code: 'locationType1')
		def type2 = new DataLocationType(code: 'locationType2')
		
		when:
		def value1 = new SumPartialValue(location: location, period: period, data: data, type: type1)
		
		then:
		value1.equals(value1)
		value1.hashCode() == value1.hashCode()
		
		when:
		def value2 = new SumPartialValue(location: location, period: period, data: data, type: type2)
		
		then:
		!value2.equals(value1)
		!value2.hashCode() != value1.hashCode()
	}
	
}

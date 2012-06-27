package org.chai.kevin

import grails.plugin.spock.UnitSpec;

import org.chai.kevin.data.Aggregation;
import org.chai.kevin.data.Average;
import org.chai.kevin.data.Sum;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.value.AggregationPartialValue;
import org.chai.kevin.value.AggregationValue;
import org.chai.kevin.value.AveragePartialValue;
import org.chai.kevin.value.AverageValue;
import org.chai.kevin.value.CalculationPartialValue;
import org.chai.kevin.value.SumPartialValue;
import org.chai.kevin.value.SumValue;
import org.chai.kevin.value.Value;

class CalculationValueUnitSpec extends UnitSpec {

	def "test sum"() {
		setup:
		def partialValue1 = new SumPartialValue(value: v("1"))
		def partialValue2 = new SumPartialValue(value: v("2"))
		def partialValue3 = new SumPartialValue(value: v("3"))
		def sum = new Sum()
		def value = null
		
		when:
		value = new SumValue([partialValue1], sum, null, new Location())
		
		then:
		value.getValue().equals(v("1"))
		
		when:
		value = new SumValue([partialValue1, partialValue2], sum, null, new Location())
		
		then:
		value.getValue().equals(v("3"))
		
		when:
		value = new SumValue([partialValue1, partialValue3], sum, null, new Location())
		
		then:
		value.getValue().equals(v("4"))
		
		when:
		value = new SumValue([partialValue1, partialValue2, partialValue3], sum, null, new Location())
		
		then:
		value.getValue().equals(v("6"))
		
	}
	
	def "test average"() {
		setup:
		def partialValue1 = new AveragePartialValue(value: v("1"), numberOfDataLocations: 1)
		def partialValue2 = new AveragePartialValue(value: v("14"), numberOfDataLocations: 2)
		def partialValue3 = new AveragePartialValue(value: v("27"), numberOfDataLocations: 3)
		def average = new Average()
		def value = null
		
		when:
		value = new AverageValue([partialValue1], average, null, new Location())
		
		then:
		value.getValue().equals(v("1"))
		
		when:
		value = new AverageValue([partialValue1, partialValue2], average, null, new Location())
		
		then:
		value.getValue().equals(v("5"))
		
		when:
		value = new AverageValue([partialValue1, partialValue3], average, null, new Location())
		
		then:
		value.getValue().equals(v("7"))
		
		when:
		value = new AverageValue([partialValue1, partialValue2, partialValue3], average, null, new Location())
		
		then:
		value.getValue().equals(v("7"))
		
	}
	
	def "test aggregation"() {
		setup:
		def partialValue11 = new AggregationPartialValue(value: v("1"), expressionData: '\$1')
		def partialValue12 = new AggregationPartialValue(value: v("1"), expressionData: '\$2')
		def partialValue21 = new AggregationPartialValue(value: v("3"), expressionData: '\$1')
		def partialValue22 = new AggregationPartialValue(value: v("1"), expressionData: '\$2')
		def partialValue31 = new AggregationPartialValue(value: v("5"), expressionData: '\$1')
		def partialValue32 = new AggregationPartialValue(value: v("1"), expressionData: '\$2')
		def aggregation = new Aggregation(expression: '\$1/\$2')
		def value = null
		
		when:
		value = new AggregationValue([partialValue11, partialValue12], aggregation, null, new Location())
		
		then:
		value.getValue().equals(v("1"))
		
		when:
		value = new AggregationValue([partialValue11, partialValue12, partialValue21, partialValue22], aggregation, null, new Location())
		
		then:
		value.getValue().equals(v("2"))
		
		when:
		value = new AggregationValue([partialValue11, partialValue12, partialValue31, partialValue32], aggregation, null, new Location())
		
		then:
		value.getValue().equals(v("3"))
		
		when:
		value = new AggregationValue([partialValue11, partialValue12, partialValue21, partialValue22, partialValue31, partialValue32], aggregation, null, new Location())
		
		then:
		value.getValue().equals(v("3"))
		
	}
	
	def "test sum with null values on DataLocation"() {
		setup:
		def partialValue = null
		def value = null
		def sum = new Sum()
		
		when:
		partialValue = new SumPartialValue(value: Value.NULL_INSTANCE())
		value = new SumValue([partialValue], sum, null, new DataLocation())
		
		then:
		value.getValue().equals(Value.NULL_INSTANCE())
	}
	
	def "test average with null values on DataLocation"() {
		setup:
		def partialValue = null
		def value = null
		def average = new Average()
		
		when:
		partialValue = new AveragePartialValue(value: Value.NULL_INSTANCE())
		value = new AverageValue([partialValue], average, null, new DataLocation())
		
		then:
		value.getValue().equals(Value.NULL_INSTANCE())
	}
	
	def "test aggregation with null values on DataLocation"() {
		setup:
		def partialValue = null
		def value = null
		def aggregation = new Aggregation(expression: '\$1/\$2')
		
		when:
		def partialValue1 = new AggregationPartialValue(value: v("1"), expressionData: '\$1')
		def partialValue2 = new AggregationPartialValue(value: Value.NULL_INSTANCE(), expressionData: '\$2')
		value = new AggregationValue([partialValue1, partialValue2], aggregation, null, new DataLocation())
		
		then:
		value.getValue().equals(Value.NULL_INSTANCE())
	}
	
	def "test aggregation with null values on Location"() {
		setup:
		def partialValue = null
		def value = null
		def aggregation = new Aggregation(expression: '\$1/\$2')
		
		when:
		def partialValue1 = new AggregationPartialValue(value: v("1"), expressionData: '\$2')
		def partialValue2 = new AggregationPartialValue(value: Value.NULL_INSTANCE(), expressionData: '\$1')
		value = new AggregationValue([partialValue1, partialValue2], aggregation, null, new Location())
		
		then:
		value.getValue().equals(v("0"))
	}
	
	def "test average with invalid values"() {
		setup:
		def partialValue = null
		def value = null
		def average = new Average()
		
		when:
		partialValue = new AveragePartialValue(value: v("1"), numberOfDataLocations: 0)
		value = new AverageValue([partialValue], average, null, new Location())
		
		then:
		value.getValue().equals(Value.NULL_INSTANCE())
		
		when:
		partialValue = new AveragePartialValue(value: v("0"), numberOfDataLocations: 0)
		value = new AverageValue([partialValue], average, null, new Location())

		then:
		value.getValue().equals(Value.NULL_INSTANCE())
	}
	
	def "test aggregation with invalid values"() {
		setup:
		def partialValue1 = null
		def partialValue2 = null
		def value = null
		def aggregation = new Aggregation(expression: '\$1/\$2')
		
		when:
		partialValue1 = new AggregationPartialValue(value: v("1"), expressionData: '\$1')
		partialValue2 = new AggregationPartialValue(value: v("0"), expressionData: '\$2')
		value = new AggregationValue([partialValue1, partialValue2], aggregation, null, new Location())
		
		then:
		value.getValue().equals(Value.NULL_INSTANCE())
		
		when:
		partialValue1 = new AggregationPartialValue(value: v("0"), expressionData: '\$1')
		partialValue2 = new AggregationPartialValue(value: v("0"), expressionData: '\$2')
		value = new AggregationValue([partialValue1, partialValue2], aggregation, null, new Location())

		then:
		value.getValue().equals(Value.NULL_INSTANCE())
	}
	
	
	static v(def value) {
		return new Value("{\"value\":"+value+"}");
	}

}

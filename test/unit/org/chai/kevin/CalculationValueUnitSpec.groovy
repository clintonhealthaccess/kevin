package org.chai.kevin

import grails.plugin.spock.UnitSpec;

import org.chai.kevin.data.Aggregation;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Mode
import org.chai.kevin.data.Summ;
import org.chai.kevin.data.Type;
import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.kevin.value.AggregationPartialValue;
import org.chai.kevin.value.AggregationValue;
import org.chai.kevin.value.CalculationPartialValue;
import org.chai.kevin.value.ModePartialValue
import org.chai.kevin.value.ModeValue
import org.chai.kevin.value.SumPartialValue;
import org.chai.kevin.value.SumValue;
import org.chai.kevin.value.Value;

class CalculationValueUnitSpec extends UnitSpec {

	def "test sum"() {
		setup:
		def partialValue1 = new SumPartialValue(value: v("1"), numberOfDataLocations: 1)
		def partialValue2 = new SumPartialValue(value: v("2"), numberOfDataLocations: 2)
		def partialValue3 = new SumPartialValue(value: v("3"), numberOfDataLocations: 3)
		def sum = new Summ()
		def value = null
		
		when:
		value = new SumValue([partialValue1], sum, null, new Location())		
		
		then:
		value.getValue().equals(v("1"))
		value.getNumberOfDataLocations() == 1
		
		when:
		value = new SumValue([partialValue1, partialValue2], sum, null, new Location())
		
		then:
		value.getValue().equals(v("3"))
		value.getNumberOfDataLocations() == 3
		
		when:
		value = new SumValue([partialValue1, partialValue3], sum, null, new Location())
		
		then:
		value.getValue().equals(v("4"))
		value.getNumberOfDataLocations() == 4
		
		when:
		value = new SumValue([partialValue1, partialValue2, partialValue3], sum, null, new Location())
		
		then:
		value.getValue().equals(v("6"))
		value.getNumberOfDataLocations() == 6
		
	}
	
	def "test percentage"() {
		setup:
		def partialValue1 = new SumPartialValue(value: v("1"), numberOfDataLocations: 1)
		def partialValue2 = new SumPartialValue(value: v("0.5"), numberOfDataLocations: 2)
		def partialValue3 = new SumPartialValue(value: v("2"), numberOfDataLocations: 5)
		def partialValue4 = new SumPartialValue(value: Value.NULL_INSTANCE(), numberOfDataLocations: 1)
		def percentage = new Summ()
		def value = null
		
		when:
		value = new SumValue([partialValue1], percentage, null, new Location())
		
		then:
		value.getAverage().equals(v("1"))
		value.getValue().equals(v("1"))
		value.getNumberOfDataLocations() == 1
		
		when:
		value = new SumValue([partialValue1, partialValue2], percentage, null, new Location())
		
		then:
		value.getAverage().equals(v("0.5"))
		value.getValue().equals(v("1.5"))
		value.getNumberOfDataLocations() == 3
		
		when:
		value = new SumValue([partialValue1, partialValue3], percentage, null, new Location())
		
		then:
		value.getAverage().equals(v("0.5"))
		value.getValue().equals(v("3"))
		value.getNumberOfDataLocations() == 6
		
		when:
		value = new SumValue([partialValue1, partialValue2, partialValue3], percentage, null, new Location())
		
		then:
		value.getAverage().equals(v("0.4375"))
		value.getValue().equals(v("3.5"))
		value.getNumberOfDataLocations() == 8
		
		when: "null values are excluded from sum and average"
		value = new SumValue([partialValue1, partialValue4], percentage, null, new Location())
		
		then:
		value.getAverage().equals(v("1"))
		value.getValue().equals(v("1"))
		value.getNumberOfDataLocations() == 1
		
	}
	
	def "test mode boolean"() {
		setup:
		def partialValue0 = new ModePartialValue(value:Value.VALUE_MAP(["true":v("1")]))
		def partialValue1 = new ModePartialValue(value:Value.VALUE_MAP(["false":v("2")]))
		def partialValue2 = new ModePartialValue(value:Value.VALUE_MAP(["true":v("3")]))
		def partialValue3 = new ModePartialValue(value:Value.VALUE_MAP(["false":v("4")]))
		def mode = new Mode(type: Type.TYPE_LIST(Type.TYPE_BOOL()))
		def value = null
		
		when:
		value = new ModeValue([partialValue0], mode, null, new DataLocation())
		
		then:
		value.getValue().equals(Value.VALUE_LIST([v("true")]));
		
		when:
		value = new ModeValue([partialValue0, partialValue1], mode, null, new Location())
		then:
		value.getValue().equals(Value.VALUE_LIST([v("false")]))
		
		when:
		value = new ModeValue([partialValue1, partialValue2], mode, null, new Location())
		then:
		value.getValue().equals(Value.VALUE_LIST([v("true")]))
		
		when:
		value = new ModeValue([partialValue0, partialValue2, partialValue3], mode, null, new Location())
		then:
		value.getValue().equals(Value.VALUE_LIST([v("false"), v("true")]))	
	}
	
	def "test mode number"() {
		setup:
		def partialValue0 = new ModePartialValue(value:Value.VALUE_MAP(["2":v("1")]))
		def partialValue1 = new ModePartialValue(value:Value.VALUE_MAP(["4":v("2")]))
		def partialValue2 = new ModePartialValue(value:Value.VALUE_MAP(["2":v("3")]))
		def partialValue3 = new ModePartialValue(value:Value.VALUE_MAP(["4":v("4")]))
		def mode = new Mode(type: Type.TYPE_LIST(Type.TYPE_NUMBER()))
		def value = null
		
		when:
		value = new ModeValue([partialValue0], mode, null, new DataLocation())
		
		then:
		value.getValue().equals(Value.VALUE_LIST([v("2")]))
		
		when:
		value = new ModeValue([partialValue0, partialValue1], mode, null, new Location())
		then:
		value.getValue().equals(Value.VALUE_LIST([v("4")]))
		
		when:
		value = new ModeValue([partialValue1, partialValue2], mode, null, new Location())
		then:
		value.getValue().equals(Value.VALUE_LIST([v("2")]))
		
		when:
		value = new ModeValue([partialValue0, partialValue2, partialValue3], mode, null, new Location())
		then:
		value.getValue().equals(Value.VALUE_LIST([v("2"),v("4")]))
		
	}
	
	def "test mode string"() {
		setup:
//		def energy = new Enum(code: 'energy_source')
//		def nationalGrid = new EnumOption(enume: energy, value: 'national_grid')
//		def solar = new EnumOption(enume: energy, value: 'solar')
//		def none = new EnumOption(enume: energy, value: 'none')
		
		def partialValue0 = new ModePartialValue(value: Value.VALUE_MAP(["solar":v("1")]))
		def partialValue1 = new ModePartialValue(value: Value.VALUE_MAP(["national_grid":v("2")]))
		def partialValue2 = new ModePartialValue(value: Value.VALUE_MAP(["solar":v("3")]))
		def partialValue3 = new ModePartialValue(value: Value.VALUE_MAP(["none":v("4")]))
		def mode = new Mode(type: Type.TYPE_LIST(Type.TYPE_STRING()))
		def value = null
		
		when:
		value = new ModeValue([partialValue0], mode, null, new DataLocation())
		
		then:
		value.getValue().equals(Value.VALUE_LIST([v("solar")]))
		
		when:
		value = new ModeValue([partialValue0, partialValue1], mode, null, new Location())
		then:
		value.getValue().equals(Value.VALUE_LIST([v("national_grid")]))
		
		when:
		value = new ModeValue([partialValue1, partialValue2], mode, null, new Location())
		then:
		value.getValue().equals(Value.VALUE_LIST([v("solar")]))
		
		when:
		value = new ModeValue([partialValue0, partialValue2, partialValue3], mode, null, new Location())
		then:
		value.getValue().equals(Value.VALUE_LIST([v("solar"),v("none")]))
		
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
	
	def "test sum and average with null values on DataLocation"() {
		setup:
		def partialValue = null
		def value = null
		def sum = new Summ()
		
		when:
		partialValue = new SumPartialValue(value: Value.NULL_INSTANCE(), numberOfDataLocations: 1)
		value = new SumValue([partialValue], sum, null, new DataLocation())
		
		then:
		value.getValue().equals(Value.NULL_INSTANCE())
		value.getAverage().equals(Value.NULL_INSTANCE())
		value.getNumberOfDataLocations() == 1
	}
	
	def "test sum with null values on Location"() {
		setup:
		def partialValue1 = null
		def partialValue2 = null
		def value = null
		def percentage = new Summ()
		
		when:
		partialValue1 = new SumPartialValue(value: Value.NULL_INSTANCE(), numberOfDataLocations: 1)
		partialValue2 = new SumPartialValue(value: Value.NULL_INSTANCE(), numberOfDataLocations: 1)
		value = new SumValue([partialValue1, partialValue2], percentage, null, new Location())
		
		then:
		value.getValue().equals(Value.NULL_INSTANCE())
		value.getAverage().equals(Value.NULL_INSTANCE())
		value.getNumberOfDataLocations() == 0
		
	}
	
	def "test mode with null values on DataLocation"() {
		setup:
		def partialValue = null
		def value = null
		def mode = new Mode()
		
		when:
		partialValue = new ModePartialValue(value: Value.NULL_INSTANCE())
		value = new ModeValue([partialValue], mode, null, new DataLocation())
		then:
		value.getValue().equals(Value.NULL_INSTANCE())
	}
	
	def "test mode with null values on Location"() {
		setup:
		def partialValue1 = null
		def partialValue2 = null
		def value = null
		def mode = new Mode()
		
		when:
		partialValue1 = new ModePartialValue(value: Value.NULL_INSTANCE())
		partialValue2 = new ModePartialValue(value: Value.NULL_INSTANCE())
		value = new ModeValue([partialValue1, partialValue2], mode, null, new Location())
		
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
	
	def "test sum with invalid values"() {
		setup:
		def partialValue = null
		def value = null
		def percentage = new Summ()
		
		when:
		partialValue = new SumPartialValue(value: v("1"), numberOfDataLocations:0)
		value = new SumValue([partialValue], percentage, null, new Location())
		
		then:
		value.getValue().getNumberValue() == 1
		value.getAverage().equals(Value.NULL_INSTANCE())
		value.getNumberOfDataLocations() == 0
		
		when:
		partialValue = new SumPartialValue(value: v("0"), numberOfDataLocations:0)
		value = new SumValue([partialValue], percentage, null, new Location())

		then:
		value.getValue().getNumberValue() == 0
		value.getAverage().equals(Value.NULL_INSTANCE())
		value.getNumberOfDataLocations() == 0
	}
	
	def "test mode with invalid values"() {
		setup:
		def partialValue = null
		def value = null
		def mode = new Mode()
		
		when:
		partialValue = new ModePartialValue(value: v("0"))
		value = new ModeValue([partialValue], mode, null, new Location())
		then:
		value.getValue().equals(Value.NULL_INSTANCE())
		
		when:
		partialValue = new ModePartialValue(value: v("blah"))
		value = new ModeValue([partialValue], mode, null, new Location())
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

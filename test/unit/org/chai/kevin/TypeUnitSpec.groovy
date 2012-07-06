package org.chai.kevin;

import java.util.Map;
import java.util.Set;

import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.PrefixPredicate;
import org.chai.kevin.data.Type.Sanitizer;
import org.chai.kevin.data.Type.TypeVisitor;
import org.chai.kevin.data.Type.ValuePredicate;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.data.Type.ValueVisitor;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;

import grails.plugin.spock.IntegrationSpec;
import grails.plugin.spock.UnitSpec;

public class TypeUnitSpec extends UnitSpec {

//	def "test transform value"() {
//		
//		when:
//		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key1": Type.TYPE_NUMBER(), "key2": Type.TYPE_MAP(["key1.key1": Type.TYPE_STRING(), "key1.key2": Type.TYPE_NUMBER()])]))
//		def jaqlValue = '[{"key1":10,"key2":{"key1.key1":"value", "key1.key2":10}},{"key1":10,"key2":null}]'
//		def value = type.getValueFromJaql(jaqlValue)
//
//		def values = new HashSet([])		
//		type.transformValue(value, new ValuePredicate() {
//			public boolean transformValue(Value currentValue, Type currentType, String currentPrefix) {
//				values << currentType.getJaqlValue(currentValue)
//			}
//		});
//		def expectedJaqlValues = new HashSet([
//			'[{"key1":10.0,"key2":{"key1.key1":"value","key1.key2":10.0}},{"key1":10.0,"key2":null}]',
//			'{"key1":10.0,"key2":{"key1.key1":"value","key1.key2":10.0}}', // first line
//			'10.0', // value
//			'{"key1.key1":"value","key1.key2":10.0}',  
//			'"value"', // value
//			'10.0', // value
//			'{"key1":10.0,"key2":null}', // second line
//			'10.0',
//			'null'
//		])
//		
//		then:
//		expectedJaqlValues.equals(values)
//		
//		when:
//		values = new HashSet([])
//		type.transformValue(value, new ValuePredicate() {
//			public boolean transformValue(Value currentValue, Type currentType, String currentPrefix) {
//				if (!currentType.isComplexType()) values << currentType.getJaqlValue(currentValue)
//			}
//		});
//		
//	}
	
	def "test type"() {
		setup:
		def type = null
		
		when:
		type = new Type("{\"type\":\"number\"}");
		
		then:
		type.getType() == ValueType.NUMBER
		
		when:
		type = new Type("{\"type\":\"bool\"}");
		
		then:
		type.getType() == ValueType.BOOL
		
		when:
		type = new Type("{\"type\":\"string\"}");
		
		then:
		type.getType() == ValueType.STRING
		
		when:
		type = new Type("{\"type\":\"text\"}");
		
		then:
		type.getType() == ValueType.TEXT
		
		when:
		type = new Type("{\"type\":\"number\"}");
		
		then:
		type.getType() == ValueType.NUMBER
		
		when:
		type = new Type("{\"type\":\"list\", \"list_type\":{\"type\":\"number\"}}");
		
		then:
		type.getType() == ValueType.LIST
		type.getListType().equals(Type.TYPE_NUMBER())
		
		when:
		type = new Type("{\"type\":\"map\", \"elements\":[{\"name\":\"key1\",\"element_type\":{\"type\":\"number\"}}]}");
		
		then:
		type.getType() == ValueType.MAP
		type.getElementMap().equals(["key1":Type.TYPE_NUMBER()])
	}
	
	def "test equals"() {
		expect:
		new Type("{\"type\" : \"bool\"}").equals(Type.TYPE_BOOL())
		Type.TYPE_BOOL().equals(new Type("{\"type\" : \"bool\"}"))
	}
	
	def "test value"() {
		setup:
		def value = null
		
		when:
		value = new Value("{\"value\": 10}")
		
		then:
		value.getNumberValue() == 10
		value.getStringValue() == "10"
		
		when:
		value = new Value("{\"value\": 1.0E9}")
		
		then:
		value.getNumberValue() == 1000000000
		value.getStringValue() == "1.0E9"
		
		when: "value with attribute"
		value = new Value("{\"value\":10, \"skipped\":\"33\"}")

		then:
		value.getNumberValue() == 10

		when: "null value with attribute"
		value = new Value("{\"value\":null, \"skipped\":\"33\"}")

		then:
		value.isNull()
		
		when:
		value = new Value("{\"value\":[{\"map_value\":{\"skipped\":\"33\",\"value\":null},\"map_key\":\"key\"}]}")
		
		then:
		value.getMapValue().get("key").isNull()
		
		when:
		value = new Value("{\"value\": true}")
		
		then:
		value.getBooleanValue() == true
		value.getStringValue() == "true"
		value.getNumberValue() == null
		
		when:
		value = new Value("{\"value\": \"a\"}")
		
		then:
		value.getStringValue() == "a"
		value.getNumberValue() == null
		
		when:
		value = new Value("{\"value\": [{\"value\":10}, {\"value\":11}]}")
		
		then:
		value.getListValue().equals([new Value("{\"value\":10}"), new Value("{\"value\":11}")])
		value.getNumberValue() == null
		
		when:
		value = new Value("{\"value\": [{\"map_key\": \"key1\", \"map_value\": {\"value\":10}}, {\"map_key\": \"key2\", \"map_value\": {\"value\":11}}]}")
		
		then:
		value.getMapValue().equals(["key1": new Value("{\"value\":10}"), "key2": new Value("{\"value\":11}")])
		value.getNumberValue() == null
		
		when:
		value = new Value("{\"value\": \"10-02-2009\"}")
		
		then:
		value.getDateValue().equals(Utils.parseDate("10-02-2009"));
		
		when:
		value = new Value("{\"value\":[{\"map_value\":{\"value\":[{\"map_value\":{\"value\":[{\"map_value\":{\"value\":123},\"map_key\":\"key111\"}]},\"map_key\":\"key11\"}]},\"map_key\":\"key1\"}]}");
		
		then:
		value.getMapValue().equals(["key1": new Value("{\"value\":[{\"map_value\":{\"value\":[{\"map_value\":{\"value\":123},\"map_key\":\"key111\"}]},\"map_key\":\"key11\"}]}")])
		value.getMapValue().get('key1').getMapValue().equals(["key11": new Value("{\"value\":[{\"map_value\":{\"value\":123},\"map_key\":\"key111\"}]}")]);
		value.getMapValue().get('key1').getMapValue().get('key11').getMapValue().equals(["key111": new Value("{\"value\":123}")])
		
	}
	
	def "get value from object"() {
		setup:
		def value = null
		def type = null
		
		when:
		value = new Value("{\"value\": 10}")
		type = new Type("{\"type\":\"number\"}");
		
		then:
		type.getValue(10).equals(value)
		
		when:
		value = new Value("{\"value\": \"a\"}")
		type = new Type("{\"type\":\"string\"}");
		
		then:
		type.getValue("a").equals(value)
		
		when:
		value = new Value("{\"value\": \"10-02-2009\"}")
		type = new Type("{\"type\":\"date\"}")
		
		then:
		type.getValue(Utils.parseDate("10-02-2009")).equals(value)
	}
	
	def "get jaql from value"() {
		setup:
		def value = null
		def type = null
		
		when:
		value = new Value("{\"value\": 10}")
		type = new Type("{\"type\":\"number\"}");
		
		then:
		type.getJaqlValue(value) == "10.0";
		
		when:
		value = new Value("{\"value\": \"a\"}")
		type = new Type("{\"type\":\"string\"}");
		
		then:
		type.getJaqlValue(value) == "\"a\"";
		
		when:
		value = new Value("{\"value\": [{\"value\":10}, {\"value\":11}]}")
		type = new Type("{\"type\":\"list\", \"list_type\":{\"type\":\"number\"}}");
		
		then:
		type.getJaqlValue(value) == "[10.0,11.0,]";
		
		when:
		value = Value.NULL_INSTANCE()
		type = Type.TYPE_NUMBER()
		
		then:
		type.getJaqlValue(value) == "null";
		
		when:
		value = new Value("{\"value\": \"10-02-2009\"}")
		type = Type.TYPE_DATE()
		
		then:
		type.getJaqlValue(value) == "\"10-02-2009\""
		
		when:
		value = new Value("{\"value\": [{\"value\":10}, {\"value\":null}]}")
		type = new Type("{\"type\":\"list\", \"list_type\":{\"type\":\"number\"}}");
		
		then:
		type.getJaqlValue(value) == "[10.0,null,]";
		
		when:
		type = Type.TYPE_MAP(["key": Type.TYPE_STRING()])
		value = Value.VALUE_MAP(["non_existant": Value.VALUE_STRING("value")])
		
		then:
		type.getJaqlValue(value) == "{}"
		
		when:
		type = Type.TYPE_MAP(["key": Type.TYPE_STRING()])
		value = Value.VALUE_MAP(["key": Value.VALUE_STRING("value")])
		
		then:
		type.getJaqlValue(value) == "{\"key\":\"value\",}"
		
	}

	
	def "test null value"() {
		when:
		def value = Value.NULL_INSTANCE()
		
		then:
		value.isNull() == true
		value.getBooleanValue() == null
		value.getNumberValue() == null
		value.getStringValue() == null
		value.getListValue() == null
		value.getDateValue() == null
		value.getMapValue() == null
		
		when:
		value = new Value("{\"value\": null, \"attribute\": \"test\"}")
		
		then:
		value.isNull() == true
	}
	
	def "test merge value from map"() {
		setup:
		def value = null
		def type = null
		def sanitizer = new Sanitizer() {
			public Object sanitizeValue(Object currentValue, Type currentType, String prefix, String genericPrefix) {
				return currentValue;
			}
		}
		
		when:
		type = new Type("{\"type\":\"number\"}");
		value = type.mergeValueFromMap(Value.NULL_INSTANCE(), ['value': ''], 'value', new HashSet([]),sanitizer)
				
		then:
		value.isNull() == true

		when:
		type = new Type("{\"type\":\"number\"}");
		value = type.mergeValueFromMap(Value.NULL_INSTANCE(), ['value': 1], 'value', new HashSet([]),sanitizer)		
		then:
		value.isNull() == false
		value.numberValue == 1d
		
		when:
		type = new Type("{\"type\":\"number\"}");
		value = type.mergeValueFromMap(Value.NULL_INSTANCE(), ['value': 1d], 'value', new HashSet([]),sanitizer)
		
		then:
		value.isNull() == false
		value.numberValue == 1d
		
		when:
		type = new Type("{\"type\":\"string\"}");

		value = type.mergeValueFromMap(Value.NULL_INSTANCE(), ['value': ''], 'value', new HashSet([]),sanitizer)
				
		then:
		value.isNull() == false
		value.getStringValue() == ''
		
		when:
		type = new Type("{\"type\":\"string\"}");
		value = type.mergeValueFromMap(Value.NULL_INSTANCE(), ['value': 'test\\'], 'value', new HashSet([]),sanitizer)
		
		then:
		value.getStringValue() == 'test'
		
		when:
		type = new Type("{\"type\":\"bool\"}");
		value = type.mergeValueFromMap(Value.NULL_INSTANCE(), ['value': '0'], 'value', new HashSet([]),sanitizer)
		
		then:
		value.getBooleanValue() == null
		
        // to be put in SurveyPageTest
		//when:
		//type = new Type("{\"type\":\"bool\"}");
		//value = type.mergeValueFromMap(Value.NULL_INSTANCE(), ['value':["0", "1"]], 'value', new HashSet([]),sanitizer)
		//
		//then:
		//value.getBooleanValue() == true
		
		when:
		type = new Type("{\"type\":\"date\"}");
		value = type.mergeValueFromMap(Value.NULL_INSTANCE(), ['value':"12-12-2012"], 'value', new HashSet([]),sanitizer)
		
		then:
		value.getDateValue() == null
		
		when:
		type = new Type("{\"type\":\"date\"}");
		value = type.mergeValueFromMap(Value.NULL_INSTANCE(), ['value':new Date()], 'value', new HashSet([]),sanitizer)
		then:
		value.getDateValue() != null
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		value = type.mergeValueFromMap(Value.NULL_INSTANCE(), ['value[0]':10d, 'value[_]':2, 'value':['[0]', '[_]']], 'value', new HashSet([]),sanitizer)
		
		then:
		value.equals(new Value("{\"value\":[{\"value\":10}]}"))
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		value = type.mergeValueFromMap(Value.NULL_INSTANCE(), ['value[0]':10d, 'value[_]':2, 'value':['[0]', '[_]', '', '', '']], 'value', new HashSet([]),sanitizer)
		
		then:
		value.equals(new Value("{\"value\":[{\"value\":10}]}"))
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())

		value = type.mergeValueFromMap(Value.NULL_INSTANCE(), [:], 'value', new HashSet([]),sanitizer)
		
		then:
		value.equals(Value.NULL_INSTANCE())
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		value = type.mergeValueFromMap(Value.NULL_INSTANCE(), ['[3]':10d], '', new HashSet([]), sanitizer)
		
		then:
		value.listValue.size() == 4
		value.listValue[0].isNull()
		value.listValue[1].isNull()
		value.listValue[2].isNull()
		value.listValue[3].numberValue == 10d
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		def oldValue = Value.VALUE_LIST([Value.VALUE_NUMBER(5d)])
		value = type.mergeValueFromMap(oldValue, ['[3]':10d], '', new HashSet([]), sanitizer)
		
		then:
		value.listValue.size() == 4
		value.listValue[0].numberValue == 5d
		value.listValue[1].isNull()
		value.listValue[2].isNull()
		value.listValue[3].numberValue == 10d
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		oldValue = Value.VALUE_LIST([Value.VALUE_NUMBER(5d),Value.VALUE_NUMBER(10d)])
		value = type.mergeValueFromMap(oldValue, ['[0]':15d], '', new HashSet([]), sanitizer)
		
		then:
		value.listValue.size() == 2
		value.listValue[0].numberValue == 15d
		value.listValue[1].numberValue == 10d
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_MAP(["list": Type.TYPE_LIST(Type.TYPE_NUMBER())]))
		value = type.mergeValueFromMap(Value.NULL_INSTANCE(), ['[1].list[3]':10d], '', new HashSet([]), sanitizer)
		
		then:
		value.listValue.size() == 2
		value.listValue[0].mapValue['list'].isNull()
		value.listValue[1].mapValue['list'].listValue[0].isNull()
		value.listValue[1].mapValue['list'].listValue[1].isNull()
		value.listValue[1].mapValue['list'].listValue[2].isNull()
		value.listValue[1].mapValue['list'].listValue[3].numberValue == 10d
	}
	
	def "test equal"() {
		expect:
		new Value("{\"value\":10}").equals(new Value("{\"value\": 10}"));
		new Value("{\"value\":[{\"value\":10}]}").equals(new Value("{\"value\": [{\"value\":10}]}"));
		!new Value("{\"value\":[{\"value\":10}]}").equals(new Value("{\"value\": 10}"));
	}
	
	def "test to jaql and back"() {
		setup:
		def value = null
		def type = null
		
		when:
		type = typeObject
		value = Value.NULL_INSTANCE()
		
		then:
		type.getJaqlValue(value) == "null"
		type.getValueFromJaql(type.getJaqlValue(value)).isNull()
		
		where:
		typeObject << [Type.TYPE_DATE(), Type.TYPE_BOOL(), Type.TYPE_NUMBER(), Type.TYPE_STRING(), Type.TYPE_TEXT(), Type.TYPE_ENUM("test"), Type.TYPE_LIST(Type.TYPE_NUMBER()), Type.TYPE_MAP(["key1":Type.TYPE_NUMBER()])]
	}
	
	def "test from jaql"() {
		setup:
		def jaql = null
		def type = null
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		jaql = "[10, 11]"
		
		then:
		type.getValueFromJaql(jaql).equals(new Value("{\"value\": [{\"value\":10}, {\"value\":11}]}"))
		
		when:
		type = Type.TYPE_DATE()
		jaql = "\"10-02-2009\""
		
		then:
		type.getValueFromJaql(jaql).equals(new Value("{\"value\": \"10-02-2009\"}"))
		
		when:
		type = Type.TYPE_STRING()
		jaql = "\"a\""
		
		then:
		type.getValueFromJaql(jaql).equals(new Value("{\"value\": \"a\"}"))
		
		when:
		type = Type.TYPE_NUMBER()
		jaql = "1000000000"
		
		then:
		type.getValueFromJaql(jaql).equals(new Value("{\"value\": 1.0E9}"))
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_MAP(["test": Type.TYPE_NUMBER()]))
		jaql = "[]"
		
		then:
		type.getValueFromJaql(jaql).equals(Value.VALUE_LIST([]))
	}
	
	def "test null values"() {
		setup:
		def value = null
		def type = null
		
		when:
		value = new Value("{\"value\": null}");
		
		then:
		value.isNull() == true
		
		when:
		type = new Type("{\"type\":\"number\"}");
		value = type.getValueFromJaql("")
		
		then:
		thrown IllegalArgumentException
	}
	
	def "test replace"() {
		setup:
		def type = null
		def value = null
		def strings = null
		def list = null
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		value = new Value("{\"value\": [{\"value\":10}, {\"value\":11}]}")
		strings = ["values[_]"]
		list = new HashSet();
		type.getCombinations(value, strings, list, "values")
		
		then:
		list.containsAll([["values[0]"],["values[1]"]])
		
		when:
		type = Type.TYPE_NUMBER()
		value = new Value("{\"value\":10}");
		strings = [""]
		list = new HashSet();
		type.getCombinations(value, strings, list, "")
		
		then:
		list.containsAll([[""]])
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_LIST(Type.TYPE_NUMBER()))
		value = Value.VALUE_LIST([Value.VALUE_LIST([Value.VALUE_NUMBER(1), Value.VALUE_NUMBER(1)])])
		strings = ["\$1[_][_]"]
		list = new HashSet();
		type.getCombinations(value, strings, list, "\$1")
		
		then:
		list.containsAll([["\$1[0][0]"], ["\$1[0][1]"]])
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_MAP(["test": Type.TYPE_LIST(Type.TYPE_MAP(["test_nested": Type.TYPE_NUMBER()]))]))
		value = Value.VALUE_LIST([
			Value.VALUE_MAP([
				"test": Value.VALUE_LIST([
					Value.VALUE_MAP(["test_nested": Value.VALUE_NUMBER(1)]),
					Value.VALUE_MAP(["test_nested": Value.VALUE_NUMBER(1)]),
				])
			]),
			Value.VALUE_MAP([
				"test": Value.VALUE_LIST([Value.VALUE_MAP(["test_nested": Value.VALUE_NUMBER(1)])])
			])
		])
		strings = ["\$1[_].test[_].test_nested", "\$1[_].test[_].test_nested > 100"]
		list = new HashSet();
		type.getCombinations(value, strings, list, "\$1")
		
		then:
		list.containsAll([
			["\$1[0].test[0].test_nested", "\$1[0].test[0].test_nested > 100"],
			["\$1[0].test[1].test_nested", "\$1[0].test[1].test_nested > 100"],
			["\$1[1].test[0].test_nested", "\$1[1].test[0].test_nested > 100"],
		])
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_MAP(["test": Type.TYPE_NUMBER()]))
		value = Value.VALUE_LIST([Value.VALUE_MAP(["test":Value.VALUE_NUMBER(1)])])
		strings = ["[_].test"]
		list = new HashSet();
		type.getCombinations(value, strings, list, "")
		
		then:
		list.containsAll([["[0].test"]])
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_MAP(["test1": Type.TYPE_NUMBER(), "test2": Type.TYPE_NUMBER()]))
		value = Value.VALUE_LIST([
			Value.VALUE_MAP(["test1":Value.VALUE_NUMBER(1), "test2":Value.VALUE_NUMBER(1)]),Value.VALUE_MAP(["test1":Value.VALUE_NUMBER(1), "test2":Value.VALUE_NUMBER(1)])
		])
		strings = ["[_].test2 > [_].test1", "[_]"]
		list = new HashSet();
		type.getCombinations(value, strings, list, "")
		
		then:
		list.containsAll([["[0].test2 > [0].test1", "[0]"]])
		!list.contains(["[1].test2 > [0].test1", "[0]"])
		!list.contains(["[1].test2 > [1].test1", "[0]"])
		!list.contains(["[1].test2 > [0].test1", "[1]"])
		!list.contains(["[0].test2 > [0].test1", "[1]"])
		!list.contains(["[0].test2 > [1].test1", "[1]"])
		!list.contains(["[0].test2 > [1].test1", "[0]"])
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_MAP(["test1": Type.TYPE_NUMBER(), "test2": Type.TYPE_NUMBER()]))
		value = Value.VALUE_LIST([Value.VALUE_MAP(["test1":Value.VALUE_NUMBER(1), "test2":Value.VALUE_NUMBER(1)])])
		strings = ["\$1[_].test2 > \$2[_].test1", "\$1[_]"]
		list = new HashSet();
		type.getCombinations(value, strings, list, "\$1")
		
		then:
		list.containsAll([["\$1[0].test2 > \$2[_].test1", "\$1[0]"]])
		
	}
	
	def "test generic prefix for merge value from map"() {
		setup:
		def type = null
		def map = null
		def genericPrefixes = null
		def prefixes = null
		def sanitizer = new Sanitizer() {
			public Object sanitizeValue(Object value, Type currentType, String prefix, String genericPrefix) {
				genericPrefixes << genericPrefix
				prefixes << prefix
				return value;
			}
		}
		
		when:
		genericPrefixes = []
		prefixes = []
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		map = ['value[0]':'10', 'value':['[0]']]
		type.mergeValueFromMap(Value.NULL_INSTANCE(), map, 'value', new HashSet(), sanitizer);
	
		then:
		genericPrefixes.equals(['value[_]'])
		prefixes.equals(['value[0]'])
		
		when:
		genericPrefixes = []
		prefixes = []
		type = Type.TYPE_LIST(Type.TYPE_MAP(["key1":Type.TYPE_NUMBER()]))
		map = ['value[0].key1':'10', 'value':['[0]']]
		type.mergeValueFromMap(Value.NULL_INSTANCE(), map, 'value', new HashSet(), sanitizer);
		
		then:
		genericPrefixes.equals(['value[_].key1'])
		prefixes.equals(['value[0].key1'])
		
		when:
		genericPrefixes = []
		prefixes = []
		type = Type.TYPE_LIST(Type.TYPE_LIST(Type.TYPE_NUMBER()))
		map = ['value[0][0]':'10', 'value':['[0]'], 'value[0]':['[0]']]
		type.mergeValueFromMap(Value.NULL_INSTANCE(), map, 'value', new HashSet(), sanitizer);
		
		then:
		genericPrefixes.equals(['value[_][_]'])
		prefixes.equals(['value[0][0]'])
	}
	
	def "test type visit"() {
		setup:
		def type = null
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_MAP(["key1":Type.TYPE_NUMBER()]))
		def expectedVisitedTypes = [Type.TYPE_LIST(Type.TYPE_MAP(["key1":Type.TYPE_NUMBER()])), Type.TYPE_MAP(["key1":Type.TYPE_NUMBER()]), Type.TYPE_NUMBER()]
		def visitedTypes = []
		type.visit(new TypeVisitor() {
			public void handle(Type currentType, String prefix) {
				visitedTypes << currentType
			}	
		})
		
		then:
		expectedVisitedTypes.equals(visitedTypes)
	}
	
	def "test value visit"() {
		setup:
		def type = null
		def value = null
		
		when:
		type = Type.TYPE_NUMBER()
		value = new Value("{\"value\":10}");
		
		then:
		type.visit(value, new ValueVisitor() {
			public void handle(Type currentType, Value currentValue, String prefix, String genericPrefix) {
				assert value.equals(currentValue)
				assert getTypes().equals(["":type])	
			}
		})
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		value = new Value("{\"value\": [{\"value\":null}, {\"value\":11}]}")
		def expectedVisitedValues = [new Value("{\"value\": [{\"value\":null}, {\"value\":11}]}"), new Value("{\"value\":null}"), new Value("{\"value\":11}]}")]
		def expectedVisitedTypes = [
			["":Type.TYPE_LIST(Type.TYPE_NUMBER())],
			["":Type.TYPE_LIST(Type.TYPE_NUMBER()), "[0]":Type.TYPE_NUMBER()],
			["":Type.TYPE_LIST(Type.TYPE_NUMBER()), "[1]":Type.TYPE_NUMBER()],
		]
		def visitedValues = []
		def visitedTypeMap = []
		type.visit(value, new ValueVisitor() {
			public void handle(Type currentType, Value currentValue, String prefix, String genericPrefix) {
				visitedValues << currentValue
				visitedTypeMap << new TreeMap(getTypes())
			}
		})
		
		then:
		expectedVisitedValues.equals(visitedValues)
		expectedVisitedTypes.equals(visitedTypeMap)
	}
	
//	def "test value list visit"() {
//		setup:
//		def type = null
//		def value = null
//		
//		when:
//		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
//		value = new Value("{\"value\": [{\"value\":null}, {\"value\":11}]}")
//		def expectedVisitedValues = [new Value("{\"value\":null}"), new Value("{\"value\":11}]}")]
//		def expectedVisitedTypes = [["[0]":Type.TYPE_NUMBER()],["[1]":Type.TYPE_NUMBER()]]
//		def visitedValues = []
//		def visitedTypeMap = []
//		def listType = type.getListType()		
//		for (int i = 0; i < value.getListValue().size(); i++) {
//			listType.listVisit(i, value.getListValue().get(i), new ValueVisitor() {
//				public void handle(Type currentType, Value currentListValue, String prefix, String genericPrefix) {
//					visitedValues << currentListValue
//					visitedTypeMap << new TreeMap(getTypes())
//				}
//			})
//		}
//		
//		then:
//		expectedVisitedValues.equals(visitedValues)
//		expectedVisitedTypes.equals(visitedTypeMap)
//	}
		
	def "test value visit when value not complete"() {
		setup:
		def value;
		def type;
		
		when:
		type = Type.TYPE_MAP(["key0": Type.TYPE_NUMBER()])
		value = new Value("{\"value\": []}")
		def visitedValues = []
		type.visit(value, new ValueVisitor() {
			public void handle(Type currentType, Value currentValue, String prefix, String genericPrefix) {
				visitedValues << currentValue
			}
		})
		
		then:
		visitedValues.equals([Value.VALUE_MAP([:])])
	}
	
	public static class NullPrefixPredicate extends PrefixPredicate {
		public boolean holds(Type type, Value value, String prefix) {
			return value.isNull();
		}
	}
	
	def "null prefixes"() {
		setup: 
		def type = null
		def value = null
		def list = null
		
		when:
		type = Type.TYPE_NUMBER()
		value = Value.NULL_INSTANCE()
		list = type.getPrefixes(value, new NullPrefixPredicate())
		
		then:
		list.equals(["": Value.NULL_INSTANCE()])
		
		when:
		type = Type.TYPE_NUMBER()
		value = new Value("{\"value\":10}");
		list = type.getPrefixes(value, new NullPrefixPredicate())
		
		then:
		list.size() == 0
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		value = new Value("{\"value\": [{\"value\":null}, {\"value\":11}]}")
		list = type.getPrefixes(value, new NullPrefixPredicate())
		
		then:
		list.equals(["[0]": Value.NULL_INSTANCE()]);
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		value = Value.NULL_INSTANCE()
		list = type.getPrefixes(value, new NullPrefixPredicate())
		
		then:
		list.equals("": Value.NULL_INSTANCE())
		
	}
		
	def "set attribute through value"() {
		setup:
		def value = null
		
		when:
		value = new Value("{\"value\":10}")
		value.setAttribute("attribute", "test")
		
		then:
		value.equals(new Value("{\"value\":10, \"attribute\":\"test\"}"))
		
		when:
		value = new Value("{\"value\":10}")
		value.setAttribute("attribute", "test")
		value.setAttribute("attribute", null)
		
		then:
		value.equals(new Value("{\"value\":10}"))
	}
	
	def "get attribute through type"() {
		setup:
		def type = null
		def value = null

		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		value = Value.NULL_INSTANCE()
		def attribute = type.getAttribute(value, "[0]", "attribute")
		
		then:
		attribute == null
		
	}
	
	def "set attribute through type"() {
		setup:
		def type = null
		def value = null
		
		when:
		type = Type.TYPE_NUMBER()
		value = new Value("{\"value\":10}")
		type.setAttribute(value, "", "attribute", "test")
	
		then:
		value.getAttribute("attribute") == "test"
		type.getAttribute(value, "", "attribute") == "test"
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		value = new Value("{\"value\": [{\"value\":null}, {\"value\":11}]}")
		type.setAttribute(value, "[0]", "attribute", "test")
		
		then:
		value.getListValue().get(0).getAttribute("attribute") == "test"
		type.getAttribute(value, "[0]", "attribute") == "test"
		value.jsonValue.contains ("attribute")
		
//		when:
//		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
//		value = Value.NULL_INSTANCE()
//		type.setAttribute(value, "[0]", "attribute", "text")
		
//		then:
//		thrown IndexOutOfBoundsException
		
		when:
		type = Type.TYPE_MAP(["key1":Type.TYPE_NUMBER()])
		value = new Value("{\"value\":[{\"map_key\":\"key1\", \"map_value\":{\"value\":10}}]}")
		type.setAttribute(value, ".key1", "attribute", "test")
		
		then:
		value.getMapValue().get("key1").getAttribute("attribute") == "test"
		type.getAttribute(value, ".key1", "attribute") == "test"
		value.jsonValue.contains ("attribute")
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_MAP(["key1":Type.TYPE_NUMBER()]))
		value = new Value("{\"value\": [{\"value\":[{\"map_key\":\"key1\", \"map_value\":{\"value\":10}}]}]}")
		type.setAttribute(value, "[0].key1", "attribute", "test")
		value.jsonValue.contains ("attribute")
		
		then:
		value.getListValue().get(0).getMapValue().get("key1").getAttribute("attribute") == "test"
		type.getAttribute(value, "[0].key1", "attribute") == "test"
		value.jsonValue.contains ("attribute")
	}
	
	def "set value"() {
		setup:
		def type = null
		def value = null
		
		when:
		type = Type.TYPE_NUMBER()
		value = new Value("{\"value\":10}")
		type.setValue(value, "", new Value("{\"value\":11}"))
		
		then:
		value.equals(new Value("{\"value\":11}"))
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		value = new Value("{\"value\": [{\"value\":null}, {\"value\":11}]}")
		type.setValue(value, "[0]", new Value("{\"value\":10}"))
		
		then:
		value.equals(new Value("{\"value\": [{\"value\":10},{\"value\":11}]}"))
		
		when:
		type = Type.TYPE_MAP(["key1": (Type.TYPE_NUMBER()), "key2": (Type.TYPE_NUMBER())])
		value = new Value("{\"value\":[{\"map_key\":\"key1\", \"map_value\":{\"value\":10}}, {\"map_key\":\"key2\", \"map_value\":{\"value\": null}}]}")
		type.setValue(value, ".key1", new Value("{\"value\":11}"))
		
		then:
		value.equals(new Value("{\"value\":[{\"map_key\":\"key1\", \"map_value\":{\"value\":11}}, {\"map_key\":\"key2\", \"map_value\":{\"value\": null}}]}"))
	}
	
	def "set value preserves attributes"() {
		setup:
		def type = null
		def value = null
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		value = new Value("{\"value\": [{\"value\":null}, {\"value\":11}], \"attribute\":\"test\"}")
		type.setValue(value, "[0]", new Value("{\"value\":10}"))
		
		then:
		value.getAttribute("attribute") == "test"
	}
	
	def "get value"() {
		setup:
		def type = null
		def value = null
		
		when:
		type = Type.TYPE_NUMBER()
		value = new Value("{\"value\":10}")

		then:
		type.getValue(value, "").equals(value)
				
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		value = new Value("{\"value\": [{\"value\":10}, {\"value\":11}]}")
	
		then:
		type.getValue(value, "[0]").equals(new Value("{\"value\":10}"));
		type.getValue(value, "").equals(value)
	
		when:
		type = Type.TYPE_MAP(["key1": Type.TYPE_LIST(Type.TYPE_NUMBER())])
		value = new Value("{\"value\":[{\"map_key\":\"key1\", \"map_value\":{\"value\": [{\"value\":10}, {\"value\":11}]}}]}")	
		
		then:
		type.getValue(value, ".key1[0]").equals(new Value("{\"value\":10}"))
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		value = Value.NULL_INSTANCE()
		
		then:
		type.getValue(value, "[0]") == null
		
		when:
		type = Type.TYPE_MAP(["key":Type.TYPE_NUMBER()])
		value = Value.NULL_INSTANCE()
		
		then:
		type.getValue(value, ".key") == null
	}
	
	def "test get type with prefix"() {
		setup:
		def type = null
		def value = null
		
		when:
		type = Type.TYPE_NUMBER()

		then:
		type.getType('').equals(type)
		
		when:
		type = Type.TYPE_NUMBER()
		type.getType('[_]')
		
		then:
		thrown IllegalArgumentException
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		
		then:
		type.getType('').equals(type)
		type.getType('[_]').equals(Type.TYPE_NUMBER())
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		
		then:
		type.getType('').equals(type)
		type.getType('[1]').equals(Type.TYPE_NUMBER())
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_MAP(["test": Type.TYPE_NUMBER()]))
		
		then:
		type.getType('').equals(type)
		type.getType('[1].test').equals(Type.TYPE_NUMBER())
		type.getType('[12].test').equals(Type.TYPE_NUMBER())
	
		when:
		type = Type.TYPE_MAP(["key1": Type.TYPE_NUMBER()])
		
		then:
		type.getType('').equals(type)
		type.getType('.key1').equals(Type.TYPE_NUMBER())
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_MAP(["key1": Type.TYPE_NUMBER(), 'key2': Type.TYPE_NUMBER()]))
		
		then:
		type.getType('').equals(type)
		type.getType('[_].key1').equals(Type.TYPE_NUMBER())
		type.getType('[_].key2').equals(Type.TYPE_NUMBER())
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_MAP(["key": Type.TYPE_NUMBER(), 'key_test': Type.TYPE_NUMBER()]))
		
		then:
		type.getType("[_].key_test").equals(Type.TYPE_NUMBER())

		when:
		type = Type.TYPE_LIST(Type.TYPE_MAP(["key": Type.TYPE_NUMBER(), 'key_test': Type.TYPE_MAP(["number": Type.TYPE_NUMBER()])]))
		
		then:
		type.getType("[_].key_test.number").equals(Type.TYPE_NUMBER())
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_MAP(["key": Type.TYPE_NUMBER(), 'key_test': Type.TYPE_LIST(Type.TYPE_NUMBER())]))
		
		then:
		type.getType("[_].key_test[_]").equals(Type.TYPE_NUMBER())
		
		when:
		type = Type.TYPE_MAP(["key": Type.TYPE_NUMBER(), 'key_test': Type.TYPE_LIST(Type.TYPE_NUMBER())])
		
		then:
		type.getType(".key_test[0]").equals(Type.TYPE_NUMBER())
	}
	
	def "get displayed value"() {
		setup:
		def type = null
		
		when:
		type = Type.TYPE_MAP (["key1": Type.TYPE_NUMBER()]);
		
		then:
		type.getDisplayedValue(2, null) == "map\n  key1 : number"
		type.getDisplayedValue(2, 1) == "map ..."
		
		when:
		type = Type.TYPE_MAP (["key1": Type.TYPE_NUMBER(), "key2": Type.TYPE_NUMBER()]);
		
		then:
		type.getDisplayedValue(2, 2) == "map\n  key1 : number ..."
	}
	
	def "test transform"() {
		setup:
		def predicate = null
		def type = null
		def value = null
		
		when:
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		value = new Value("{\"value\": [{\"value\":10}, {\"value\":11}]}");
		predicate = new ValuePredicate() {
			public boolean transformValue(Value currentValue, Type currentType, String currentPrefix) {
				currentValue.setAttribute("attribute", "test");
			}
		}
		type.transformValue(value, predicate);
		
		then:
		type.getValue(value, "[0]").getAttribute("attribute") == "test"
		type.getValue(value, "[1]").getAttribute("attribute") == "test"
		type.getValue(value, "").getAttribute("attribute") == "test"
		
	}
	
	def "test transform with type mismatch"() {
		setup:
		def predicate = null
		def type = null
		def value = null
		
		when:
		type = Type.TYPE_MAP("test": Type.TYPE_NUMBER())
		value = Value.VALUE_MAP(["test1": Value.VALUE_NUMBER(2)])
		predicate = new ValuePredicate() {
			public boolean transformValue(Value currentValue, Type currentType, String currentPrefix) {
				currentValue.setAttribute("attribute", "test");
			}
		}
		type.transformValue(value, predicate);
		
		then:
		type.getValue(value, "").getAttribute("attribute") == "test"
		
	}
	
	def "test setjsonvalue"() {
		when:
		def value = new Value("{\"value\":10}")
		value.setJsonValue(Value.NULL_INSTANCE().toString())
		
		then:
		value.equals(Value.NULL_INSTANCE())
	}
	
	def "is valid"() {
		setup:
		def type = null
		
		expect:
		!new Type("").isValid()
		!new Type("{\"type\":\"prout\"}").isValid()
		new Type("{\"type\":\"number\"}").isValid()
		!new Type("{\"type\":\"list\", \"list_type\":{\"type\":\"prout\"}}").isValid()
	}
	
}
package org.chai.kevin;

import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.PrefixPredicate;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.Value;

import grails.plugin.spock.IntegrationSpec;
import grails.plugin.spock.UnitSpec;

public class TypeSpec extends UnitSpec {

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
		type = new Type("{\"type\":\"number\"}");
		
		then:
		type.getType() == ValueType.NUMBER
		
		when:
		type = new Type("{\"type\":\"list\", \"list_type\":{\"type\":\"number\"}}");
		
		then:
		type.getType() == ValueType.LIST
		type.getListType().equals(JSONUtils.TYPE_NUMBER)
		
		when:
		type = new Type("{\"type\":\"map\", \"elements\":[{\"name\":\"key1\",\"element_type\":{\"type\":\"number\"}}]}");
		
		then:
		type.getType() == ValueType.MAP
		type.getElementMap().equals(["key1":JSONUtils.TYPE_NUMBER])
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
		value = new Value("{\"value\": [{\"key\": \"key1\", \"value\": {\"value\":10}}, {\"key\": \"key2\", \"value\": {\"value\":11}}]}")
		
		then:
		value.getMapValue().equals(["key1": new Value("{\"value\":10}"), "key2": new Value("{\"value\":11}")])
		value.getNumberValue() == null
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
	}

	
	def "test null value"() {
		when:
		def value = Value.NULL
		
		then:
		value.isNull() == true
		value.getBooleanValue() == null
		value.getNumberValue() == null
		value.getStringValue() == null
		value.getListValue() == null
		value.getDateValue() == null
		value.getMapValue() == null
	}
	
	def "test map"() {
		setup:
		def value = null
		def type = null
		
		when:
		type = new Type("{\"type\":\"number\"}");
		value = type.getValueFromMap(['value': ''], 'value')
		
		then:
		value.isNull() == true

		when:
		type = new Type("{\"type\":\"string\"}");
		value = type.getValueFromMap(['value': ''], 'value')
		
		then:
		value.isNull() == true
		
		when:
		type = new Type("{\"type\":\"bool\"}");
		value = type.getValueFromMap(['value': '0'], 'value')
		
		then:
		value.getBooleanValue() == false
		
		when:
		type = new Type("{\"type\":\"bool\"}");
		value = type.getValueFromMap(['value':["0", "1"]], 'value')
		
		then:
		value.getBooleanValue() == true
		
		when:
		type = JSONUtils.TYPE_LIST(JSONUtils.TYPE_NUMBER)
		value = type.getValueFromMap(['value[0]':'10', 'value[_]':'2', 'value':['[0]', '[_]']], 'value')
		
		then:
		value.equals(new Value("{\"value\":[{\"value\":10}]}"))
	}
	
	def "test equal"() {
		expect:
		new Value("{\"value\":10}").equals(new Value("{\"value\": 10}"));
		new Value("{\"value\":[{\"value\":10}]}").equals(new Value("{\"value\": [{\"value\":10}]}"));
	}
	
	def "test to jaql and back"() {
		setup:
		def value = null
		def type = null
		
		when:
		type = typeObject
		value = Value.NULL
		
		then:
		type.getJaqlValue(value) == "null"
		type.getValueFromJaql(type.getJaqlValue(value)).isNull()
		
		where:
		typeObject << [JSONUtils.TYPE_BOOL, JSONUtils.TYPE_NUMBER, JSONUtils.TYPE_STRING, JSONUtils.TYPE_ENUM(1), JSONUtils.TYPE_LIST(JSONUtils.TYPE_NUMBER), JSONUtils.TYPE_MAP(["key1":JSONUtils.TYPE_NUMBER])]
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
		thrown NumberFormatException
	}
	
	def "test replace"() {
		setup:
		def type = null
		def value = null
		def strings = null
		def list = null
		
		when:
		type = JSONUtils.TYPE_LIST(JSONUtils.TYPE_NUMBER)
		value = new Value("{\"value\": [{\"value\":10}, {\"value\":11}]}")
		strings = ["values[_]"]
		list = new HashSet();
		type.getCombinations(value, strings, list, "values")
		
		then:
		list.containsAll([["values[0]"],["values[1]"]])
		
		when:
		type = JSONUtils.TYPE_NUMBER
		value = new Value("{\"value\":10}");
		strings = [""]
		list = new HashSet();
		type.getCombinations(value, strings, list, "")
		
		then:
		list.containsAll([[""]])
	}
	
	public static class NullPrefixPredicate implements PrefixPredicate {
		public boolean holds(Type type, Value value, String prefix) {
			return value.isNull();
		}
	}
	
	def "null prefixes"() {
		setup: 
		def type = null
		def value = null
		def list = null
		def prefixPredicate = new NullPrefixPredicate();
		
		when:
		type = JSONUtils.TYPE_NUMBER
		value = Value.NULL
		list = new HashMap();
		type.getPrefixes(value, "", list, prefixPredicate)
		
		then:
		list.equals(["": Value.NULL])
		
		when:
		type = JSONUtils.TYPE_NUMBER
		value = new Value("{\"value\":10}");
		list = new HashMap();
		type.getPrefixes(value, "", list, prefixPredicate)
		
		then:
		list.size() == 0
		
		when:
		type = JSONUtils.TYPE_LIST(JSONUtils.TYPE_NUMBER)
		value = new Value("{\"value\": [{\"value\":null}, {\"value\":11}]}")
		list = new HashMap();
		type.getPrefixes(value, "", list, prefixPredicate)
		
		then:
		list.equals(["[0]": Value.NULL]);
		
	}
		
	def "set attribute through value"() {
		setup:
		def value = null
		
		when:
		value = new Value("{\"value\":10}")
		value.setAttribute("attribute", "test")
		
		then:
		value.equals(new Value("{\"value\":10, \"attribute\":\"test\"}"))
	}
	
	def "set attribute through type"() {
		setup:
		def type = null
		def value = null
		
		when:
		type = JSONUtils.TYPE_NUMBER
		value = new Value("{\"value\":10}")
		type.setAttribute(value, "", "attribute", "test")
	
		then:
		value.getAttribute("attribute") == "test"
		type.getAttribute(value, "", "attribute") == "test"
		
		when:
		type = JSONUtils.TYPE_LIST(JSONUtils.TYPE_NUMBER)
		value = new Value("{\"value\": [{\"value\":null}, {\"value\":11}]}")
		type.setAttribute(value, "[0]", "attribute", "test")
	
		then:
		value.getListValue().get(0).getAttribute("attribute") == "test"
		type.getAttribute(value, "[0]", "attribute") == "test"
	}
	
	def "set value"() {
		setup:
		def type = null
		def value = null
		
		when:
		type = JSONUtils.TYPE_NUMBER
		value = new Value("{\"value\":10}")
		type.setValue(value, "", new Value("{\"value\":11}"))
		
		then:
		value.equals(new Value("{\"value\":11}"))
	}
	
	def "get value"() {
		setup:
		def type = null
		def value = null
		
		when:
		type = JSONUtils.TYPE_NUMBER
		value = new Value("{\"value\":10}")

		then:
		type.getValue(value, "").equals(value)
				
		when:
		type = JSONUtils.TYPE_LIST(JSONUtils.TYPE_NUMBER)
		value = new Value("{\"value\": [{\"value\":10}, {\"value\":11}]}")
	
		then:
		type.getValue(value, "[0]").equals(new Value("{\"value\":10}"));
		type.getValue(value, "").equals(value)
	
		when:
		type = JSONUtils.TYPE_MAP(["key1": JSONUtils.TYPE_LIST(JSONUtils.TYPE_NUMBER)])
		value = new Value("{\"value\":[{\"key\":\"key1\", \"value\":{\"value\": [{\"value\":10}, {\"value\":11}]}}]}")	
		
		then:
		type.getValue(value, ".key1[0]").equals(new Value("{\"value\":10}"))
		
	}
	
}


package org.chai.kevin.value

import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.DataElement;
import grails.plugin.spock.UnitSpec;

class ValidatableUnitSpec extends UnitSpec {

	def NULL_SKIPPED_VALUE = "{\"value\":null, \"skipped\":\"1\"}"
	
	def "test skipped prefix completes question"() {
		when:
		def value = new Value(NULL_SKIPPED_VALUE)
		def type = Type.TYPE_NUMBER()
		def validatable = new ValidatableValue(value, type)
		
		then:
		validatable.isComplete() == true
	}
	
	def "test get skipped prefixes"() {
		when:
		def value = new Value(NULL_SKIPPED_VALUE)
		def type = Type.TYPE_NUMBER()
		def validatable = new ValidatableValue(value, type)
		
		then:
		validatable.getSkippedPrefixes().equals(new HashSet([""]))
	}
	def "test is tree valid"() {
		setup:
		def value
		def type
		def validatable
		
		when:
		value = Value.VALUE_LIST([new Value("{\"value\":10, \"invalid\":\"1\"}"), Value.VALUE_NUMBER(10)])
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		validatable = new ValidatableValue(value, type)
		
		then:
		validatable.isTreeValid("[0]") == false
		validatable.isTreeValid("[1]") == true
		
		when:
		value = Value.VALUE_LIST([new Value("{\"value\":10, \"invalid\":\"1\", \"warning\":\"1\"}"), Value.VALUE_NUMBER(10)])
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		validatable = new ValidatableValue(value, type)
		
		then:
		validatable.isTreeValid("[0]") == true
		validatable.isTreeValid("[1]") == true
		
		when:
		value = Value.VALUE_LIST([Value.VALUE_MAP(["key1":new Value("{\"value\":10, \"invalid\":\"1\"}"), "key2":Value.VALUE_NUMBER(10)])])
		type = Type.TYPE_LIST(Type.TYPE_MAP(["key1": Type.TYPE_NUMBER(), "key2": Type.TYPE_NUMBER()]))
		validatable = new ValidatableValue(value, type)
		
		then:
		validatable.isTreeValid("[0]") == false
		validatable.isTreeValid("[0].key1") == false
		validatable.isTreeValid("[0].key2") == true
	}
	
	def "test is tree complete"() {
		def value
		def type
		def validatable
		
		when:
		value = Value.VALUE_LIST([Value.NULL_INSTANCE(), Value.VALUE_NUMBER(10)])
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		validatable = new ValidatableValue(value, type)
		
		then:
		validatable.isTreeComplete("[0]") == false
		validatable.isTreeComplete("[1]") == true
		
		when:
		value = Value.VALUE_LIST([new Value(NULL_SKIPPED_VALUE), Value.VALUE_NUMBER(10)])
		type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		validatable = new ValidatableValue(value, type)
		
		then:
		validatable.isTreeComplete("[0]") == true
		validatable.isTreeComplete("[1]") == true
		
	}
	
	def "test values get sanitized properly with merge"() {
		setup:
		def value
		def type
		def validatable
		
		when:
		value = Value.NULL_INSTANCE()
		type = Type.TYPE_DATE()
		validatable = new ValidatableValue(value, type)
		validatable.mergeValue(['':'01-01-1999'], '', new HashSet())
		
		then:
		!value.isNull()
		value.dateValue != null
		
		when:
		value = Value.NULL_INSTANCE()
		type = Type.TYPE_NUMBER()
		validatable = new ValidatableValue(value, type)
		validatable.mergeValue(['':'1'], '', new HashSet())
		
		then:
		!value.isNull()
		value.numberValue != null
		
		when:
		value = Value.NULL_INSTANCE()
		type = Type.TYPE_BOOL()
		validatable = new ValidatableValue(value, type)
		validatable.mergeValue(['':'0'], '', new HashSet())
		
		then:
		!value.isNull()
		value.booleanValue == false
		
		when:
		value = Value.NULL_INSTANCE()
		type = Type.TYPE_BOOL()
		validatable = new ValidatableValue(value, type)
		validatable.mergeValue(['':'0,1'], '', new HashSet())
		
		then:
		!value.isNull()
		value.booleanValue == true
	}
}

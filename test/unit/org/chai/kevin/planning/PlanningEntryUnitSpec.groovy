package org.chai.kevin.planning

import grails.plugin.spock.UnitSpec;

import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;

class PlanningEntryUnitSpec extends UnitSpec {
	
	def "get value"() {
		setup:
		def planningEntry = null;
		
		when:
		def value = Value.VALUE_LIST([Value.VALUE_MAP(["key1":Value.VALUE_STRING("value")])]);
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key1":Type.TYPE_STRING()]))
		planningEntry = new PlanningEntry()
		def enteredValue = Mock(FormEnteredValue)
		enteredValue.getValidatable() >> new ValidatableValue(value, type)
		planningEntry.enteredValue = enteredValue
		planningEntry.lineNumber = 0
		
		then:
		planningEntry.getValue("[_].key1").equals(Value.VALUE_STRING("value"));
		
		when:
		planningEntry = new PlanningEntry()
		enteredValue = Mock(FormEnteredValue)
		enteredValue.getValidatable() >> new ValidatableValue(value, type)
		planningEntry.enteredValue = enteredValue
		planningEntry.lineNumber = 1
		
		then:
		planningEntry.getValue("[_].key1") == null
	}
	
	def "get invalid sections"() {
		setup:
		def planningEntry = null
		def value = null
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key1":Type.TYPE_STRING(), "key2":Type.TYPE_STRING()]))
		
		when:
		def planningType = Mock(PlanningType)
		planningType.getSections() >> ["[_].key1", "[_].key2"]
		value = Value.VALUE_LIST([Value.VALUE_MAP(["key1":new Value("{\"value\":\"test\", \"invalid\":\"1\"}"), "key2":Value.VALUE_STRING("value")])]);
		planningEntry = new PlanningEntry()
		def enteredValue = Mock(FormEnteredValue)
		enteredValue.getValidatable() >> new ValidatableValue(value, type)
		planningEntry.enteredValue = enteredValue
		planningEntry.lineNumber = 0
		planningEntry.type = planningType
		
		then:
		planningEntry.getInvalidSections().equals(new HashSet(['[_].key1']))
		
		when:
		value = Value.VALUE_LIST([Value.VALUE_MAP(["key1":Value.VALUE_STRING("test"), "key2":Value.VALUE_STRING("value")])]);
		planningEntry = new PlanningEntry()
		enteredValue = Mock(FormEnteredValue)
		enteredValue.getValidatable() >> new ValidatableValue(value, type)
		planningEntry.enteredValue = enteredValue
		planningEntry.lineNumber = 0
		planningEntry.type = planningType
		
		then:
		planningEntry.getInvalidSections().empty
	}
	
	def "get fixed header value"() {
		setup:
		def value = Value.VALUE_LIST([Value.VALUE_MAP(["key1":Value.VALUE_STRING("value")])]);
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key1":Type.TYPE_STRING(), "key2":Type.TYPE_STRING()]))
		
		when:
		def planningType = Mock(PlanningType)
		planningType.getFixedHeader() >> '[_].key1'
		def planningEntry = new PlanningEntry()
		def enteredValue = Mock(FormEnteredValue)
		enteredValue.getValidatable() >> new ValidatableValue(value, type)
		planningEntry.enteredValue = enteredValue
		planningEntry.lineNumber = 0
		planningEntry.type = planningType
		
		then:
		planningEntry.fixedHeaderValue.equals(Value.VALUE_STRING("value"))
		
		when:
		planningType = Mock(PlanningType)
		planningType.getFixedHeader() >> '[_].key2'
		planningEntry = new PlanningEntry()
		enteredValue = Mock(FormEnteredValue)
		enteredValue.getValidatable() >> new ValidatableValue(value, type)
		planningEntry.enteredValue = enteredValue
		planningEntry.lineNumber = 0
		planningEntry.type = planningType
		
		then:
		planningEntry.fixedHeaderValue.equals(null)
		
		when:
		planningType = Mock(PlanningType)
		planningType.getFixedHeader() >> '[_].key3'
		planningEntry = new PlanningEntry()
		enteredValue = Mock(FormEnteredValue)
		enteredValue.getValidatable() >> new ValidatableValue(value, type)
		planningEntry.enteredValue = enteredValue
		planningEntry.lineNumber = 0
		planningEntry.type = planningType
		
		then:
		planningEntry.fixedHeaderValue.equals(null)
	}
	
}

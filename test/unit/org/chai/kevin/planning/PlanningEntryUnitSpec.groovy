package org.chai.kevin.planning

import grails.plugin.spock.UnitSpec;

import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
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
		planningEntry = new PlanningEntry(new ValidatableValue(value, type), 0)
		
		then:
		planningEntry.getValue("[_].key1").equals(Value.VALUE_STRING("value"));
		
		when:
		planningEntry = new PlanningEntry(new ValidatableValue(value, type), 1)
		
		then:
		planningEntry.getValue("[_].key1") == null
	}
	
	def "budget updated"() {
		setup:
		def planningEntry = null
		
		when:
		def value = Value.VALUE_LIST([Value.VALUE_MAP(["key1":Value.VALUE_STRING("value")])]);
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key1":Type.TYPE_STRING()]))
		planningEntry = new PlanningEntry(new ValidatableValue(value, type), 0)

		then:
		planningEntry.isBudgetUpdated() == false
		
		when:
		value.listValue[0].setAttribute("budget_updated", "true")
				
		then:
		planningEntry.isBudgetUpdated() == true
		
	}
	
	def "set budget updated"() {
		setup:
		def planningEntry = null
		
		when:
		def value = Value.VALUE_LIST([Value.VALUE_MAP(["key1":Value.VALUE_STRING("value")])]);
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key1":Type.TYPE_STRING()]))
		planningEntry = new PlanningEntry(new ValidatableValue(value, type), 0)

		then:
		value.listValue[0].getAttribute('budget_updated') == null
		type.getAttribute(value, "[0]", "budget_updated") == null
		
		when:
		planningEntry.setBudgetUpdated(true)
				
		then:
		value.listValue[0].jsonValue.contains("budget_updated")
		value.jsonValue.contains ("budget_updated")
		value.listValue[0].getAttribute('budget_updated') == "true"
		type.getAttribute(value, "[0]", "budget_updated") == "true"
	}
	
}
package org.chai.kevin.planning

import grails.plugin.spock.UnitSpec;

import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;

class PlanningEntryUnitSpec extends UnitSpec {

	
	def "get value"() {
		setup:
		def planningEntry = null;
		
		when:
		def value = Value.VALUE_LIST([Value.VALUE_MAP(["key1":Value.VALUE_STRING("value")])]);
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key1":Type.TYPE_STRING()]))
		planningEntry = new PlanningEntry(new RawDataElementValue(value: value, data: new RawDataElement(type: type)), 0)
		
		then:
		planningEntry.getValue("[_].key1").equals(Value.VALUE_STRING("value"));
		
		when:
		planningEntry = new PlanningEntry(new RawDataElementValue(value: value, data: new RawDataElement(type: type)), 1)
		
		then:
		planningEntry.getValue("[_].key1") == null
	}
	
}

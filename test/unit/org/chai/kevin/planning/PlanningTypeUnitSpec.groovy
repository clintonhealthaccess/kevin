package org.chai.kevin.planning

import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormElement;

import grails.plugin.spock.UnitSpec;

class PlanningTypeUnitSpec extends UnitSpec {

	def "test get sections"() {
		when:
		def formElement = new FormElement(dataElement: new RawDataElement(type: Type.TYPE_LIST(Type.TYPE_MAP([
			"key1": Type.TYPE_NUMBER()	
		]))))
		def planningType = new PlanningType(formElement: formElement)
		
		then:
		planningType.sections == ["[_].key1"]
		
	}
	
	def "test get value prefixes ignores fixed header"() {
		when:
		def formElement = new FormElement(dataElement: new RawDataElement(type: Type.TYPE_LIST(Type.TYPE_MAP([
			"key1": Type.TYPE_NUMBER(),
			"key2": Type.TYPE_STRING()
		]))))
		def planningType = new PlanningType(fixedHeader: "[_].key1", formElement: formElement)
		
		then:
		planningType.getValuePrefixes("").equals (['[_].key2'])
		
	}
	
	def "get type when type does not exist returns null"() {
		when:
		def formElement = new FormElement(dataElement: new RawDataElement(type: Type.TYPE_LIST(Type.TYPE_MAP([
			"key1": Type.TYPE_NUMBER(),
			"key2": Type.TYPE_ENUM("code"),
			"key3": Type.TYPE_STRING()
		]))))
		def planningType = new PlanningType(fixedHeader: "[_].key1", formElement: formElement)
		
		then:
		planningType.getType("[_].doesnotexist") == null
	}
	
}

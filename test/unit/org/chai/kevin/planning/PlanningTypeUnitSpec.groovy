package org.chai.kevin.planning

import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;

import grails.plugin.spock.UnitSpec;

class PlanningTypeUnitSpec extends UnitSpec {

	def "test get sections"() {
		when:
		def dataElement = new RawDataElement(type: Type.TYPE_LIST(Type.TYPE_MAP([
			"key1": Type.TYPE_NUMBER()	
		])))
		def planningType = new PlanningType(dataElement: dataElement)
		
		then:
		planningType.sections == ["[_].key1"]
		
	}
	
	def "test get value prefixes ignores discriminator and fixed header"() {
		when:
		def dataElement = new RawDataElement(type: Type.TYPE_LIST(Type.TYPE_MAP([
			"key1": Type.TYPE_NUMBER(),
			"key2": Type.TYPE_ENUM("code"),
			"key3": Type.TYPE_STRING()
		])))
		def planningType = new PlanningType(discriminator: "[_].key2", fixedHeader: "[_].key1", dataElement: dataElement)
		
		then:
		planningType.getValuePrefixes("").equals (['[_].key3'])
		
	}
	
}

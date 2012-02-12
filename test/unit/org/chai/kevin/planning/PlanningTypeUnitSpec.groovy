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
	
}

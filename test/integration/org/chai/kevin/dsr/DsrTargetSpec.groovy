package org.chai.kevin.dsr

import org.chai.kevin.data.Type;

import grails.validation.ValidationException;

class DsrTargetSpec extends DsrIntegrationTests {

	def "can save target"() {
		setup:
		def objective = newDsrObjective(CODE(1))
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		
		when:
		new DsrTarget(objective: objective, groupUuids: [DISTRICT_HOSPITAL_GROUP], code: CODE(1), dataElement: dataElement).save(failOnError: true)
		
		then:
		DsrTarget.count() == 1
	}
	
	def "cannot save target with null data element"() {
		setup:
		def objective = newDsrObjective(CODE(1))
		
		when:
		new DsrTarget(objective: objective, groupUuids: [DISTRICT_HOSPITAL_GROUP], code: CODE(1)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "cannot save target with null code"() {
		setup:
		def objective = newDsrObjective(CODE(1))
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		
		when:
		new DsrTarget(objective: objective, groupUuids: [DISTRICT_HOSPITAL_GROUP], dataElement: dataElement).save(failOnError: true)
		
		then:
		thrown ValidationException
		
	}
	
}

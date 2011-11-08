package org.chai.kevin.dsr

import org.chai.kevin.data.Type;

import grails.validation.ValidationException;

class DsrTargetSpec extends DsrIntegrationTests {

	def "can save target"() {
		setup:
		def objective = newDsrObjective(CODE(1))
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "1")
		
		when:
		new DsrTarget(objective: objective, groupUuids: [DISTRICT_HOSPITAL_GROUP], code: CODE(1), expression: expression).save(failOnError: true)
		
		then:
		DsrTarget.count() == 1
	}
	
	def "cannot save target with null expression"() {
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
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "1")
		
		when:
		new DsrTarget(objective: objective, groupUuids: [DISTRICT_HOSPITAL_GROUP], expression: expression).save(failOnError: true)
		
		then:
		thrown ValidationException
		
	}
	
}

package org.chai.kevin.dsr

import org.chai.kevin.data.Type;

import grails.validation.ValidationException;

class DsrObjectiveSpec extends DsrIntegrationTests {

	def "can save objective"() {
		when:
		new DsrObjective(code: CODE(1)).save(failOnError: true)
		
		then:
		DsrObjective.count() == 1
	}
	
	def "cannot save target with null code"() {
		when:
		new DsrObjective().save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
}

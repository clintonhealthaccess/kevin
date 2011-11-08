package org.chai.kevin.fct

import org.chai.kevin.data.Type;

import grails.validation.ValidationException;

class FctObjectiveSpec extends FctIntegrationTests {

	def "can save objective"() {
		when:
		new FctObjective(code: CODE(1)).save(failOnError: true)
		
		then:
		FctObjective.count() == 1
	}
	
	def "cannot save target with null code"() {
		when:
		new FctObjective().save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
}

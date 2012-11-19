package org.chai.kevin.data

import grails.validation.ValidationException;

import org.chai.kevin.IntegrationTests;

class SourceSpec extends IntegrationTests {

	def "constraints"() {
		when:
		new Source(code: 'source').save(failOnError: true)
		
		then:
		Source.count() == 1
		
		when:
		new Source().save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
}

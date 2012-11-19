package org.chai.kevin

import grails.validation.ValidationException;

class PeriodSpec extends IntegrationTests {

	def "nullable constraints"() {
		when:
		new Period(code: '123', startDate: new Date(), endDate: new Date() + 1, defaultSelected: true).save(failOnError: true)
		
		then:
		Period.count() == 1
		
		when:
		new Period(code: '123', endDate: new Date() + 1, defaultSelected: true).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new Period(code: '123', startDate: new Date() + 1, defaultSelected: true).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new Period(code: '123', startDate: new Date(), endDate: new Date() + 1, defaultSelected: true).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
}

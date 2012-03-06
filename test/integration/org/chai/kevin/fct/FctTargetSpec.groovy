package org.chai.kevin.fct

import org.chai.kevin.data.Type;

import grails.validation.ValidationException;

class FctTargetSpec extends FctIntegrationTests {

	def "can save target"() {
		setup:
		def program = newReportProgram(CODE(1))
		def sum = newSum("1", CODE(1))
		
		when:
		new FctTarget(program: program, typeCodes: [DISTRICT_HOSPITAL_GROUP], code: CODE(1), sum: sum).save(failOnError: true)
		
		then:
		FctTarget.count() == 1
	}
	
	def "cannot save target with null expression"() {
		setup:
		def program = newReportProgram(CODE(1))
		
		when:
		new FctTarget(program: program, typeCodes: [DISTRICT_HOSPITAL_GROUP], code: CODE(1)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "cannot save target with null code"() {
		setup:
		def program = newReportProgram(CODE(1))
		def sum = newSum("1", CODE(1))
		
		when:
		new FctTarget(program: program, typeCodes: [DISTRICT_HOSPITAL_GROUP], sum: sum).save(failOnError: true)
		
		then:
		thrown ValidationException
		
	}
	
}

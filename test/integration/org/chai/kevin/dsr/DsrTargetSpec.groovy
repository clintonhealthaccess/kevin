package org.chai.kevin.dsr

import org.chai.kevin.data.Type;

import grails.validation.ValidationException;

class DsrTargetSpec extends DsrIntegrationTests {

	def "can save target"() {
		setup:
		def program = newReportProgram(CODE(1))
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		
		when:
		new DsrTarget(program: program, code: CODE(1), dataElement: dataElement).save(failOnError: true)
		
		then:
		DsrTarget.count() == 1
	}
	
	def "cannot save target with null data element"() {
		setup:
		def program = newReportProgram(CODE(1))
		
		when:
		new DsrTarget(program: program, code: CODE(1)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "cannot save target with null code"() {
		setup:
		def program = newReportProgram(CODE(1))
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		
		when:
		new DsrTarget(program: program, dataElement: dataElement).save(failOnError: true)
		
		then:
		thrown ValidationException
		
	}
	
}

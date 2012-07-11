package org.chai.kevin.dsr

import org.chai.kevin.data.Type;

import grails.validation.ValidationException;

class DsrTargetSpec extends DsrIntegrationTests {

	def "can save target with data element"() {
		setup:
		def program = newReportProgram(CODE(1))
		def data = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CODE(1), 1)
		
		when:
		new DsrTarget(program: program, code: CODE(1), data: data, category: category).save(failOnError: true)
		
		then:
		DsrTarget.count() == 1
	}

	def "can save target with sum"() {
		setup:
		def program = newReportProgram(CODE(1))
		def data = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CODE(1), 1)
		
		when: "sum is default"
		new DsrTarget(program: program, code: CODE(1), data: data, category: category).save(failOnError: true)
		
		then:
		DsrTarget.count() == 1
		DsrTarget.list()[0].average.equals(null)
		
		when: "sum is specified"
		new DsrTarget(program: program, code: CODE(2), data: data, average: false, category: category).save(failOnError: true)
		
		then:
		DsrTarget.count() == 2
		DsrTarget.list()[1].average.equals(false)
	}
	
	def "can save target with average"() {
		setup:
		def program = newReportProgram(CODE(1))
		def data = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CODE(1), 1)
		
		when: "average is specified"
		new DsrTarget(program: program, code: CODE(1), data: data, average: true, category: category).save(failOnError: true)
		
		then:
		DsrTarget.count() == 1
		DsrTarget.list()[0].average.equals(true)
	}
		
	def "cannot save target with null calculation element"() {
		setup:
		def program = newReportProgram(CODE(1))
		def category = newDsrTargetCategory(CODE(1), 1)
		
		when:
		new DsrTarget(program: program, code: CODE(1), category: category).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "cannot save target with null code"() {
		setup:
		def program = newReportProgram(CODE(1))
		def data = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CODE(1), 1)
		
		when:
		new DsrTarget(program: program, data: data, category: category).save(failOnError: true)
		
		then:
		thrown ValidationException
		
	}
	
	def "cannot save target with no category"() {
		setup:
		def program = newReportProgram(CODE(1))
		def data = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		
		when:
		new DsrTarget(program: program, code: CODE(1), data: data).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
}

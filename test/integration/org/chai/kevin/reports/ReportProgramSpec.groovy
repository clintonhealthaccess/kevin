package org.chai.kevin.reports

import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportProgram

import grails.validation.ValidationException;

public class ReportProgramSpec extends ReportIntegrationTests {

	def "can save report program"() {
		when:
		def root = new ReportProgram(code: CODE(1)).save(failOnError: true)
		
		then:
		root != null
		ReportProgram.count() == 1
	}
	
	def "can save child report program"() {
		when:
		def root = newReportProgram(CODE(1))
		def program = newReportProgram(CODE(2), root)
		
		then:
		root != null
		program != null
		ReportProgram.count() == 2
	}
	
	def "cannot save program with null code"() {
		when:
		new ReportProgram().save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
}

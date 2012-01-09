package org.chai.kevin.reports

import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportObjective

import grails.validation.ValidationException;

public class ReportObjectiveSpec extends ReportIntegrationTests {

	def "can save report objective"() {
		when:
		def root = new ReportObjective(code: CODE(1)).save(failOnError: true)
		
		then:
		root != null
		ReportObjective.count() == 1
	}
	
	def "can save child report objective"() {
		when:
		def root = newReportObjective(CODE(1))
		def objective = newReportObjective(CODE(2), root)
		
		then:
		root != null
		objective != null
		ReportObjective.count() == 2
	}
	
	def "cannot save objective with null code"() {
		when:
		new ReportObjective().save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
}

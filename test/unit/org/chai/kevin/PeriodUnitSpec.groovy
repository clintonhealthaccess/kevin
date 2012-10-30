package org.chai.kevin;

import grails.plugin.spock.UnitSpec;

class PeriodUnitSpec extends UnitSpec {

	def "equals"() {
		setup:
		def date1 = new Date()
		def date2 = new Date() + 1
		def date3 = new Date() + 2
		
		when:
		def period1 = new Period(startDate: date1, endDate: date2)
		
		then:
		period1.equals(period1)
		
		when:
		def period2 = new Period(startDate: date1, endDate: date3)
	
		then:
		!period1.equals(period2)
		
		when:
		def period3 = new Period(startDate: date1, endDate: date2)
		
		then:
		period3.equals(period1)
		
	}
	
}

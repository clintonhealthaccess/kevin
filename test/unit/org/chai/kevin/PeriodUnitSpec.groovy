package org.chai.kevin;

import grails.plugin.spock.UnitSpec;

class PeriodUnitSpec extends UnitSpec {

	def "equals"() {
		
		when:
		def period1 = new Period(startDate: new Date(), endDate: new Date() + 1)
		
		then:
		period1.equals(period1)
		
		when:
		def period2 = new Period(startDate: new Date(), endDate: new Date() + 2)
		
		then:
		!period1.equals(period2)
		
		when:
		def period3 = new Period(startDate: new Date(), endDate: new Date() + 1)
		
		then:
		period3.equals(period1)
		
	}
	
}

package org.chai.kevin.data;

import grails.plugin.spock.UnitSpec;

class SourceUnitSpec extends UnitSpec {

	def "equals"() {
		
		when:
		def source1 = new Source(code: 'source')
		
		then:
		source1.equals(source1)
		
		when:
		def source2 = new Source(code: 'source1')
		
		then:
		!source1.equals(source2)
		
	}
	
}

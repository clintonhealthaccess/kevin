package org.chai.kevin.data;

import grails.plugin.spock.UnitSpec;

class EnumUnitSpec extends UnitSpec {

	def "equals enum"() {
		when:
		def enum1 = new Enum(code: 'enum')
		
		then:
		enum1.equals(enum1)
		
		when:
		def enum2 = new Enum(code: 'enum1')
		
		then:
		!enum1.equals(enum2)
	}
	
	def "equals enum option"() {
		setup:
		def enum1 = new Enum(code: 'enum')
		
		when:
		def enumOption1 = new EnumOption(code: 'enum', enume: enum1)
		
		then:
		enumOption1.equals(enumOption1)
		
		when:
		def enumOption2 = new EnumOption(code: 'enum1', enume: enum1)
		
		then:
		!enumOption1.equals(enumOption2)
		
		when:
		def enumOption3 = new EnumOption(code: 'enum', enume: enum1)
		
		then:
		enumOption3.equals(enumOption1)
		
		when:
		def enum2 = new Enum(code: 'enum1')
		def enumOption4 = new EnumOption(code: 'enum', enume: enum2)
		
		then:
		!enumOption4.equals(enumOption1)

	}
	
}

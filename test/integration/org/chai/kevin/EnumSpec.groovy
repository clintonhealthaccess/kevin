package org.chai.kevin

class EnumSpec extends IntegrationTests {

	def "get active options work"() {
		setup:
		def enume = newEnume(CODE(1))
		def option = newEnumOption(enume, "1")
		
		expect:
		enume.activeEnumOptions.equals([option])
	}
	
	def "get active options work with inactive options"() {
		setup:
		def enume = newEnume(CODE(1))
		def option = newEnumOption(enume, "1")
		
		when:
		option.inactive = true
		option.save()
		
		then:
		enume.activeEnumOptions.equals([])
	}
	
}

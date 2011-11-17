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
	
	def "test enum option ordering"() {
		setup:
		def enume = newEnume(CODE(1))
		def option1 = newEnumOption(enume, v("\"test\""), o("en":2, "fr":1))
		def option2 = newEnumOption(enume, v("\"absent\""), o("en":1, "fr":2))
		
		when:
		def enumefromdb = Enum.findByCode(CODE(1))
		
		then:
		enumefromdb.enumOptions.equals([option2, option1])
	}
	
	
}

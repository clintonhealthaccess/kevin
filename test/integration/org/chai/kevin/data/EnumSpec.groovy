package org.chai.kevin.data

import java.util.Map;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Enum;

class EnumSpec extends IntegrationTests {
	
	def enumService
	def enumOptionService

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
		
	def "test seacrh enum"(){
		setup:
		def enumeTwo = newEnume(CODE("the code two"), "My Enum two", "Enum two for test");
		def enumeOne = newEnume(CODE("the code one"), "My Enum one", "Enum one for test one");	
		when:
		def enumOnes = enumService.searchEnum("two",[:])
		def enumEnums = enumService.searchEnum("test",["sort":"names"])
		def enumCode = enumService.searchEnum("code",[:])
		def enumCount = enumService.countEnum("enum")
		then:
		enumOnes.equals([enumeTwo]);
		enumEnums.equals([enumeOne,enumeTwo]);
		enumCode.equals([enumeTwo,enumeOne]);
		enumCount==2
		
		
	}
	
	def "test seacrh enum option"(){
		setup:
		def enume = newEnume(CODE("the code one"), "My Enum two", "Enum two for test");
		def option1 = newEnumOption(enume, v("\"test\""), o("en":2, "fr":1))
		def option2 = newEnumOption(enume, v("\"absent\""), o("en":1, "fr":2))
		def option3 = newEnumOption(enume, v("\"options 3\""), o("en":3, "fr":3))
		when:
		def optionOne = enumOptionService.searchEnumOption(enume,"tion",[:]);
		def optionTwo = enumOptionService.searchEnumOption(enume,"s",["sort":"order"]);
		def enumOptionCount = enumOptionService.countEnumOption(enume,"option")
		then:
		optionOne.equals([option3])
		optionTwo.equals([option2,option1,option3])
		enumOptionCount==1
		
	}
	
}

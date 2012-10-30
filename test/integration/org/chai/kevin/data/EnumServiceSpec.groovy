package org.chai.kevin.data

import java.util.Map;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Enum;
import org.hibernate.exception.ConstraintViolationException;

class EnumServiceSpec extends IntegrationTests {
	
	def enumService
		
	def "test search enum"(){
		setup:
		def enumeTwo = newEnume(CODE(2), ['en': "My Enum two"]);
		def enumeOne = newEnume(CODE(1), ['en': "My Enum one"]);
		def result
		
		when:
		result = enumService.searchEnum("two",[:])
		
		then:
		result.equals([enumeTwo]);
		result.totalCount == 1
		
		when:
		result = enumService.searchEnum("code",[:])
		
		then:
		result.equals([enumeTwo,enumeOne]);
		result.totalCount == 2
		
		when:
		result = enumService.searchEnum("my", ["sort":"names_en"])
		
		then:
		result.equals([enumeOne,enumeTwo]);
		result.totalCount == 2
	}
	
	def "test search enum option"(){
		setup:
		def enume = newEnume(CODE(1));
		def option1 = newEnumOption(enume, "\"test\"", ["en":2, "fr":1])
		def option2 = newEnumOption(enume, "\"absent\"", ["en":1, "fr":2])
		def option3 = newEnumOption(enume, "\"options 3\"", ["en":3, "fr":3])
		def result
		
		when:
		result = enumService.searchEnumOption(enume,"tion",[:]);
		
		then:
		result.equals([option3])
		result.totalCount == 1
		
		when:
		result = enumService.searchEnumOption(enume, "s", ["sort":"orders_en"]);
		
		then:
		result.equals([option2, option1, option3])
		result.totalCount == 3
		
		when:
		result = enumService.searchEnumOption(enume, "s", ["sort":"orders_fr"]);
		
		then:
		result.equals([option1, option2, option3])
		result.totalCount == 3
	}
	
}

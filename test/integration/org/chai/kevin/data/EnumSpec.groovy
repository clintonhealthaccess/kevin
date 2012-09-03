package org.chai.kevin.data

import java.util.Map;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Enum;
import org.hibernate.exception.ConstraintViolationException;

class EnumSpec extends IntegrationTests {
	
	def enumService
	def enumOptionService
	
	//TODO grails 2.0.0 bug needs to be fixed in order for these to validate
	//this will fail grails 2 bug has to be fixed so this can pass
	//def "enum code has to be unique (this will fail grails bug)"(){
	//when:
	//def enumeOne = newEnume(CODE("code"), "My Enum one", "Enum one");
	//then:
	//Enum.count()==1
	//when:
	//def enumeTwo = newEnume(CODE("code"), "My Enum two", "Enum two");
	//then:
	//thrown ConstraintViolationException
	//}

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
		def option1 = newEnumOption(enume, "\"test\"", o("en":2, "fr":1))
		def option2 = newEnumOption(enume, "\"absent\"", o("en":1, "fr":2))
		
		when:
		def enumefromdb = Enum.findByCode(CODE(1))
		
		then:
		enumefromdb.enumOptions.equals([option2, option1])
	}	
}

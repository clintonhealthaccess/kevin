package org.chai.kevin.data

import grails.validation.ValidationException;

import java.util.Map;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Enum;
import org.hibernate.exception.ConstraintViolationException;

class EnumSpec extends IntegrationTests {
	
	def enumService
	def enumOptionService
	
	def "enum code has to be unique"() {
		when:
		new Enum(code: CODE(1)).save(failOnError: true)
		
		then:
		Enum.count()==1
		
		when:
		new Enum(code: CODE(1)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "null constraints"() {
		when:
		new Enum(code: CODE(1)).save(failOnError: true)
		
		then:
		Enum.count() == 1
		
		when:
		new Enum().save(failOnError: true)
		
		then:
		thrown ValidationException
	}

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
	
	def "deleting enum deletes enum option"() {
		setup:
		def enume = newEnume(CODE(1))
		def option1 = newEnumOption(enume, "\"test\"")
	
		when:
		enume.delete(flush: true)
		
		then:
		Enum.count() == 0
		EnumOption.count() == 0
	}
}

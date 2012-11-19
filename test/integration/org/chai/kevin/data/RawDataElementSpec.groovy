package org.chai.kevin.data

import grails.validation.ValidationException;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.JSONUtils;

class RawDataElementSpec extends IntegrationTests {

	def "data element type is not blank"() {
		when:
		new RawDataElement(code: CODE(1), type: new Type("")).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "data element code is unique"() {
		when:
		new RawDataElement(code: CODE(1), type: Type.TYPE_NUMBER()).save(failOnError: true)
		
		then:
		RawDataElement.count() == 1;
		
		when:
		new RawDataElement(code: CODE(1), type: Type.TYPE_NUMBER()).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "data element enum is present when type is enum"() {
		when:
		new RawDataElement(code: CODE(1), type: new Type("{\"type\":\"enum\"}")).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		def enume = newEnume(CODE(1))
		new RawDataElement(code: CODE(2), type: Type.TYPE_ENUM (enume.code)).save(failOnError: true)
		
		then:
		RawDataElement.count() == 1
	}
	
	def "data element type must be valid"() {
		when:
		def dataElement = new RawDataElement(code: CODE(1), type: INVALID_TYPE)
		dataElement.save(failOnError: true)
		
		then:
		thrown ValidationException
		dataElement.errors.getFieldErrors('type').size() == 1
		
		when:
		new RawDataElement(code: CODE(1), type: Type.TYPE_NUMBER()).save(failOnError: true)
		
		then:
		RawDataElement.count() == 1
	}
	
	def "source can be null"() {
		when:
		new RawDataElement(code: CODE(1), type: Type.TYPE_NUMBER()).save(failOnError: true)
		
		then:
		RawDataElement.count() == 1
	}
	
	def "deleting data element does not delete source"() {
		when:
		def source = newSource(CODE(1))
		def dataElement = new RawDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), source: source).save(failOnError: true)
		
		then:
		RawDataElement.count() == 1
		
		when:
		dataElement.delete()
		
		then:
		RawDataElement.count() == 0
		Source.count() == 1
	}
}

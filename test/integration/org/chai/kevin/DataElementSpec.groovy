package org.chai.kevin

import grails.validation.ValidationException;

import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.JSONUtils;

class DataElementSpec extends IntegrationTests {

	static final String INVALID_TYPE = "invalid_type"

	def "data element code is unique"() {
		when:
		newDataElement(CODE(1), Type.TYPE_NUMBER)

		then:
		thrown ValidationException

	}
	
	def "data element enum is present when type is enum"() {
		when:
		newDataElement(CODE(1), Type.TYPE_ENUM)
		
		then:
		thrown ValidationException
		
		when:
		def enume = newEnume(code: CODE(1))
		newDataElement(CODE(2), Type.TYPE_ENUM (enume.code))
		
		then:
		DataElement.count() == 1
	}
	
	def "data element type must be valid"() {
		when:
		newDataElement(CODE(1), INVALID_TYPE)
		
		then:
		thrown ValidationException
		
		when:
		newDataElement(CODE(1), Type.TYPE_NUMBER)
		
		then:
		DataElement.count() == 1
	}
	
}

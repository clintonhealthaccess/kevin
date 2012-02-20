package org.chai.kevin.value

import grails.validation.ValidationException;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.RawDataElementValue;

class StoredValueSpec extends IntegrationTests {

	def "raw data element value: data cannot be null"() {
		setup:
		def period = newPeriod()
		def type = newDataEntityType(DISTRICT_HOSPITAL_GROUP)
		def entity = newDataLocationEntity(BUTARO, type)
		
		
		when:
		new RawDataElementValue(entity: entity, period: period, value: v("1")).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		new RawDataElementValue(data: dataElement, entity: entity, period: period, value: v("1")).save(failOnError: true)
		
		then:
		RawDataElementValue.count() == 1
		
	}
	
}

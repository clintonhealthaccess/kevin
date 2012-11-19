package org.chai.kevin.value

import grails.validation.ValidationException;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.kevin.value.RawDataElementValue;

class StoredValueSpec extends IntegrationTests {

	def "raw data element value: data cannot be null"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def type = DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)
		
		when:
		new RawDataElementValue(location: DataLocation.findByCode(BUTARO), period: period, value: v("1")).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		new RawDataElementValue(data: dataElement, location: DataLocation.findByCode(BUTARO), period: period, value: v("1")).save(failOnError: true)
		
		then:
		RawDataElementValue.count() == 1
		
	}
	
}

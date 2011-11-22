package org.chai.kevin

import grails.validation.ValidationException;

import org.chai.kevin.data.Type;
import org.chai.kevin.value.RawDataElementValue;

class StoredValueSpec extends IntegrationTests {

	def "raw data element value: data cannot be null"() {
		setup:
		def period = newPeriod()
		def organisationUnit = newOrganisationUnit(BUTARO)
		
		when:
		new RawDataElementValue(organisationUnit: organisationUnit, period: period, value: v("1")).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		new RawDataElementValue(data: dataElement, organisationUnit: organisationUnit, period: period, value: v("1")).save(failOnError: true)
		
		then:
		RawDataElementValue.count() == 1
		
	}
	
}

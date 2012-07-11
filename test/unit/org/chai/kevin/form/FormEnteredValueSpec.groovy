package org.chai.kevin.form

import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.location.DataLocation;

import grails.plugin.spock.UnitSpec;

class FormEnteredValueSpec extends UnitSpec {

	def "haschode and equals"() {
		
		setup:
		def dataLocation1 = new DataLocation(code: 'data1')
		def dataLocation2 = new DataLocation(code: 'data2')
		def dataElement = new RawDataElement(code: 'element1')
		def formElement = new FormElement(dataElement: dataElement)
		def value1 = new FormEnteredValue(dataLocation: dataLocation1, formElement: formElement)
		def value11 = new FormEnteredValue(dataLocation: dataLocation1, formElement: formElement)
		def value2 = new FormEnteredValue(dataLocation: dataLocation2, formElement: formElement)
		
		expect:
		!value1.equals(value2)
		value1.hashCode() != value2.hashCode()
		
		value1.equals(value11)
		value1.hashCode() == value11.hashCode()
	}
	
}

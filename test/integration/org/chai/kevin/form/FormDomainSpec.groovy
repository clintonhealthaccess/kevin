package org.chai.kevin.form

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;

class FormDomainSpec extends IntegrationTests {

	def "delete form element deletes form validation rule and form headers map"() {
		setup:
		def element = newFormElement(newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_NUMBER())), ['[_]': ['en': 'Test']])
		newFormValidationRule(CODE(1), element, '[_]', [(DISTRICT_HOSPITAL_GROUP)], 'true')
		
		when:
		element.delete()
		
		then:
		FormElement.count() == 0
		FormValidationRule.count() == 0
		FormElementHeadersMap.count() == 0
	}
	
	def "delete skip rule deletes skip element map"() {
		expect: 
		false
	}
	
	def "delete validation rule deletes dependency map"() {
		expect:
		false
	}
	
	
}

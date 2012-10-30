package org.chai.kevin.form

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.location.DataLocationType;
import org.chai.location.DataLocation;
import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.survey.SurveyIntegrationTests;

class FormElementServiceSpec extends IntegrationTests {

	def formElementService
	
	def "search form element"() {
		setup:
		def dataElement = newRawDataElement(["en":"test"], "code", Type.TYPE_NUMBER())
		def formElement = newFormElement(dataElement)
		def formElements = null
		
		when:
		formElements = formElementService.searchFormElements("test", [], [:])
		
		then:
		formElements.equals([formElement])
		
		when:
		formElements = formElementService.searchFormElements("test", ["bool"], [:])
		
		then:
		formElements.empty
		
		when:
		formElements = formElementService.searchFormElements("code", ["number"], [:])
		
		then:
		formElements.equals([formElement])
	}
	
	def "get form element"() {
		setup:
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		
		when:
		def formElement = newFormElement(dataElement)
		
		then:
		formElementService.getFormElement(formElement.id).equals(formElement)
		
		when:
		def survey = SurveyIntegrationTests.newSurvey(CODE(1), period)
		def program = SurveyIntegrationTests.newSurveyProgram(CODE(1), survey, 1, [])
		def section = SurveyIntegrationTests.newSurveySection(CODE(1), program, 1, [])
		def question = SurveyIntegrationTests.newSimpleQuestion(CODE(1), section, 1, [])
		def surveyElement = SurveyIntegrationTests.newSurveyElement(question, dataElement)
		
		then:
		formElementService.getFormElement(surveyElement.id).equals(surveyElement)
	}
	
	def "get or create form entered element"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def element1 = newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		expect:
		FormEnteredValue.count() == 0
		
		when:
		def formValue = formElementService.getOrCreateFormEnteredValue(DataLocation.findByCode(KIVUYE), element1)
		
		then:
		FormEnteredValue.count() == 0
	}
	
	def "test retrieve skip rule - no rule"() {
		setup:
		def period = newPeriod()
		def element = newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def list = null
		
		when:
		list = formElementService.searchSkipRules(element)
		
		then:
		list.isEmpty()
	}
		
	def "test retrieve skip rule - no element in expression"() {
		setup:
		def period = newPeriod()
		def element = newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def list = null
		
		when:
		def rule1 = newFormSkipRule(CODE(1), "1==1", [(element): ""])
		list = formElementService.searchSkipRules(element)
		
		then:
		list.isEmpty()
	}
		
	def "test retrieve skip rule - element in expression"() {
		setup:
		def period = newPeriod()
		def element = newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def list = null
		
		when:
		def rule2 = newFormSkipRule(CODE(1), "\$"+element.id+"==1", [(element): ""])
		list = formElementService.searchSkipRules(element)
		
		then:
		list.equals(new HashSet([rule2]))
	}
	
	def "test retrieve skip rule - several rules"() {
		setup:
		def period = newPeriod()
		def element = newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def list = null
	
		when:
		def rule3 = newFormSkipRule(CODE(1), "\$"+element.id+"0"+"==1", [(element): ""])
		def rule4 = newFormSkipRule(CODE(2), "\$"+element.id+"==1", [(element): ""])
		list = formElementService.searchSkipRules(element)
		
		then:
		list.equals(new HashSet([rule4]))
	}
	
	def "test retrieve skip rule with super class"() {
		setup:
		def period = newPeriod()
		def survey = SurveyIntegrationTests.newSurvey(CODE(1), period)
		def program = SurveyIntegrationTests.newSurveyProgram(CODE(1), survey, 1, [])
		def section = SurveyIntegrationTests.newSurveySection(CODE(1), program, 1, [])
		def question = SurveyIntegrationTests.newSimpleQuestion(CODE(1), section, 1, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = SurveyIntegrationTests.newSurveyElement(question, dataElement)
		
		when:
		def rule1 = SurveyIntegrationTests.newSurveySkipRule(CODE(1), survey, "\$"+element.id+" == 1", [:], [])
		def list = formElementService.searchSkipRules(element)
		
		then:
		list.equals(new HashSet([rule1]))
	}
	
	def "test retrieve validation rules"() {
		setup:
		def hc = newDataLocationType(HEALTH_CENTER_GROUP);
		def dh = newDataLocationType(DISTRICT_HOSPITAL_GROUP);
		
		def period = newPeriod()
		def element = newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))

		def list = null
		
		when:
		list = formElementService.searchValidationRules(element, DataLocationType.findByCode( (HEALTH_CENTER_GROUP) ))
		
		then:
		list.isEmpty()
		
		when:
		def rule1 = newFormValidationRule(CODE(1), element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+"==1")
		list = formElementService.searchValidationRules(element, DataLocationType.findByCode( (HEALTH_CENTER_GROUP) ))
		
		then:
		list.equals(new HashSet([rule1]))
		
		when:
		list = formElementService.searchValidationRules(element, DataLocationType.findByCode( (DISTRICT_HOSPITAL_GROUP) ))
		
		then:
		list.isEmpty()
	}
	
	def "test retrieve validation rule - several rules"() {
		setup:
		def hc = newDataLocationType(HEALTH_CENTER_GROUP);
		def dh = newDataLocationType(DISTRICT_HOSPITAL_GROUP);
		
		def period = newPeriod()
		
		def element = newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
	
		def list = null
	
		when:
		def rule3 = newFormValidationRule(CODE(1), element, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "\$"+element.id+"0"+"==1")
		def rule4 = newFormValidationRule(CODE(2), element, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "\$"+element.id+"==1")
		list = formElementService.searchValidationRules(element, DataLocationType.findByCode( (HEALTH_CENTER_GROUP) ))
		
		then:
		list.equals(new HashSet([rule4]))
	}
	
	
}

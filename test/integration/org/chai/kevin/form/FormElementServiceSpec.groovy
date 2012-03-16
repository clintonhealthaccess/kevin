package org.chai.kevin.form

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.survey.SurveyIntegrationTests;

class FormElementServiceSpec extends IntegrationTests {

	def formElementService
	
	def "saving entered entities saves user and timestamp"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def element1 = newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		def formEnteredValue = newFormEnteredValue(element1, period, DataLocationEntity.findByCode(KIVUYE), v("1"))
		
		then:
		formEnteredValue.userUuid == null
		formEnteredValue.timestamp == null
		
		when:
		formElementService.save(formEnteredValue)
		
		then:
		formEnteredValue.userUuid == 'uuid'
		formEnteredValue.timestamp != null

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
		def rule1 = newFormSkipRule("1==1", [(element): ""])
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
		def rule2 = newFormSkipRule("\$"+element.id+"==1", [(element): ""])
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
		def rule3 = newFormSkipRule("\$"+element.id+"0"+"==1", [(element): ""])
		def rule4 = newFormSkipRule("\$"+element.id+"==1", [(element): ""])
		list = formElementService.searchSkipRules(element)
		
		then:
		list.equals(new HashSet([rule4]))
	}
	
	def "test retrieve skip rule with super class"() {
		setup:
		def period = newPeriod()
		def survey = SurveyIntegrationTests.newSurvey(period)
		def program = SurveyIntegrationTests.newSurveyProgram(survey, 1, [])
		def section = SurveyIntegrationTests.newSurveySection(program, 1, [])
		def question = SurveyIntegrationTests.newSimpleQuestion(section, 1, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = SurveyIntegrationTests.newSurveyElement(question, dataElement)
		
		when:
		def rule1 = SurveyIntegrationTests.newSurveySkipRule(survey, "\$"+element.id+" == 1", [:], [])
		def list = formElementService.searchSkipRules(element)
		
		then:
		list.equals(new HashSet([rule1]))
	}
	
	def "test retrieve validation rules"() {
		setup:
		def hc = newDataEntityType(HEALTH_CENTER_GROUP);
		def dh = newDataEntityType(DISTRICT_HOSPITAL_GROUP);
		
		def period = newPeriod()
		def element = newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))

		def list = null
		
		when:
		list = formElementService.searchValidationRules(element, DataEntityType.findByCode( (HEALTH_CENTER_GROUP) ))
		
		then:
		list.isEmpty()
		
		when:
		def rule1 = newFormValidationRule(element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+"==1")
		list = formElementService.searchValidationRules(element, DataEntityType.findByCode( (HEALTH_CENTER_GROUP) ))
		
		then:
		list.equals(new HashSet([rule1]))
		
		when:
		list = formElementService.searchValidationRules(element, DataEntityType.findByCode( (DISTRICT_HOSPITAL_GROUP) ))
		
		then:
		list.isEmpty()
	}
	
	def "test retrieve validation rule - several rules"() {
		setup:
		def hc = newDataEntityType(HEALTH_CENTER_GROUP);
		def dh = newDataEntityType(DISTRICT_HOSPITAL_GROUP);
		
		def period = newPeriod()
		
		def element = newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
	
		def list = null
	
		when:
		def rule3 = newFormValidationRule(element, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "\$"+element.id+"0"+"==1")
		def rule4 = newFormValidationRule(element, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "\$"+element.id+"==1")
		list = formElementService.searchValidationRules(element, DataEntityType.findByCode( (HEALTH_CENTER_GROUP) ))
		
		then:
		list.equals(new HashSet([rule4]))
	}
	
	
}

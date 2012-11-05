package org.chai.kevin.form

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.survey.SurveyIntegrationTests;
import org.chai.kevin.survey.SurveySkipRule;

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
		setup:
		def period = newPeriod()
		def survey = SurveyIntegrationTests.newSurvey(CODE(1), period)
		def program = SurveyIntegrationTests.newSurveyProgram(CODE(1), survey, 1, [])
		def section = SurveyIntegrationTests.newSurveySection(CODE(1), program, 1, [])
		def question = SurveyIntegrationTests.newSimpleQuestion(CODE(1), section, 1, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = SurveyIntegrationTests.newSurveyElement(question, dataElement)
		def rule = SurveyIntegrationTests.newSurveySkipRule(CODE(1), survey, 'true', [(element): ''], [])
		
		when:
		rule.delete()
		survey.removeFromSkipRules(rule)
		
		then:
		SurveySkipRule.count() == 0
		FormSkipRuleElementMap.count() == 0
	}
	
	def "delete validation rule deletes dependency map"() {
		setup:
		def element = newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def validationRule = newFormValidationRule(CODE(1), element, '', [], 'true', [element])
		
		when:
		element.delete()
		
		then:
		FormValidationRule.count() == 0
		FormValidationRuleDependency.count() == 0
	}
	
	
}

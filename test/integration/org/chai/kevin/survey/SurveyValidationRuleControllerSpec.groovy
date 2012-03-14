package org.chai.kevin.survey

import org.chai.kevin.data.Type
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.survey.validation.SurveyValidationRuleController;
import org.chai.kevin.util.Utils

class SurveyValidationRuleControllerSpec extends SurveyIntegrationTests {

	def surveyValidationRuleController
	
	def "test group uuids are correctly saved"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		surveyValidationRuleController = new SurveyValidationRuleController()
		
		when:
		surveyValidationRuleController.params['formElement.id'] = element.id
		surveyValidationRuleController.params['prefix'] = ""
		surveyValidationRuleController.params['typeCodes'] = [HEALTH_CENTER_GROUP]
		surveyValidationRuleController.params['expression'] = "true"
		surveyValidationRuleController.params['allowOutlier'] = false
		surveyValidationRuleController.params.messages = [:]
		surveyValidationRuleController.params.messages['en'] = "Validation rule"
		surveyValidationRuleController.saveWithoutTokenCheck()
		
		then:
		FormValidationRule.count() == 1
		FormValidationRule.list()[0].typeCodeString == Utils.unsplit([(HEALTH_CENTER_GROUP)])
		FormValidationRule.list()[0].messages['en'] == "Validation rule"
		
	}
	
	def "test list"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		def validationRule = newFormValidationRule(element, "", [(DISTRICT_HOSPITAL_GROUP)], "\$"+element.id+" > 0")
		surveyValidationRuleController = new SurveyValidationRuleController()
		
		when:
		surveyValidationRuleController.params['surveyElement.id'] = element.id
		surveyValidationRuleController.list()
		
		then:
		surveyValidationRuleController.modelAndView.model.entities.size() == 1
		
	}
	
	def "test list when no survey 404"() {
		setup:
		surveyValidationRuleController = new SurveyValidationRuleController()
		
		when:
		surveyValidationRuleController.list()
		
		then:
		surveyValidationRuleController.modelAndView == null
	}
	
}

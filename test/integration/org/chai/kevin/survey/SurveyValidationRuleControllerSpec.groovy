package org.chai.kevin.survey

import org.chai.kevin.data.Type
import org.chai.kevin.survey.validation.SurveyValidationRuleController
import org.chai.kevin.util.Utils

class SurveyValidationRuleControllerSpec extends SurveyIntegrationTests {

	def surveyValidationRuleController
	
	def "test group uuids are correctly saved"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		surveyValidationRuleController = new SurveyValidationRuleController()
		
		when:
		surveyValidationRuleController.params['surveyElement.id'] = element.id
		surveyValidationRuleController.params['prefix'] = ""
		surveyValidationRuleController.params['groupUuids'] = [(HEALTH_CENTER_GROUP)]
		surveyValidationRuleController.params['expression'] = "true"
		surveyValidationRuleController.params['allowOutlier'] = false
		surveyValidationRuleController.params.messages = [:]
		surveyValidationRuleController.params.messages['en'] = "Validation rule"
		surveyValidationRuleController.saveWithoutTokenCheck()
		
		then:
		SurveyValidationRule.count() == 1
		SurveyValidationRule.list()[0].groupUuidString == Utils.unsplit([(HEALTH_CENTER_GROUP)])
		SurveyValidationRule.list()[0].messages['en'] == "Validation rule"
		
	}
	
	def "test list"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		def validationRule = newSurveyValidationRule(element, "", [(DISTRICT_HOSPITAL_GROUP)], "\$"+element.id+" > 0")
		surveyValidationRuleController = new SurveyValidationRuleController()
		
		when:
		surveyValidationRuleController.params.elementId = element.id
		surveyValidationRuleController.list()
		
		then:
		surveyValidationRuleController.modelAndView.model.entities.size() == 1
		
	}
	
}

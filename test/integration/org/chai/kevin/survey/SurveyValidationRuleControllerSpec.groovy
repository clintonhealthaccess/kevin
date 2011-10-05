package org.chai.kevin.survey

import org.chai.kevin.data.Type;
import org.chai.kevin.survey.validation.SurveyValidationRuleController;
import org.chai.kevin.util.Utils;

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
		def element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		def validationMessage = newValidationMessage()
		
		surveyValidationRuleController = new SurveyValidationRuleController()
		
		when:
		surveyValidationRuleController.params['surveyElement.id'] = element.id
		surveyValidationRuleController.params['prefix'] = ""
		surveyValidationRuleController.params['validationMessage.id'] = validationMessage.id
		surveyValidationRuleController.params['groupUuids'] = [(HEALTH_CENTER_GROUP)]
		surveyValidationRuleController.params['expression'] = "true"
		surveyValidationRuleController.params['allowOutlier'] = false
		surveyValidationRuleController.saveWithoutTokenCheck()
		
		then:
		SurveyValidationRule.count() == 1
		SurveyValidationRule.list()[0].groupUuidString == Utils.unsplit([(HEALTH_CENTER_GROUP)])
		
	}
	
	def "test list"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		def validationMessage = newValidationMessage()
		def validationRule = newSurveyValidationRule(element, "", [(DISTRICT_HOSPITAL_GROUP)], "\$"+element.id+" > 0", validationMessage)
		surveyValidationRuleController = new SurveyValidationRuleController()
		
		when:
		surveyValidationRuleController.params.elementId = element.id
		surveyValidationRuleController.list()
		
		then:
		surveyValidationRuleController.modelAndView.model.entities.size() == 1
		
	}
	
}

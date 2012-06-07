package org.chai.kevin.survey

import org.chai.kevin.data.Type
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.survey.validation.SurveyValidationRuleController;
import org.chai.kevin.util.Utils

class SurveyValidationRuleControllerSpec extends SurveyIntegrationTests {

	def surveyValidationRuleController
	
	
	def "test list"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		def validationRule = newFormValidationRule(CODE(1), element, "", [(DISTRICT_HOSPITAL_GROUP)], "\$"+element.id+" > 0")
		surveyValidationRuleController = new SurveyValidationRuleController()
		
		when:
		surveyValidationRuleController.params['formElement.id'] = element.id
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

package org.chai.kevin.form

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type

import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.survey.validation.SurveyValidationRuleController;
import org.chai.kevin.util.Utils

class FormValidationRuleControllerSpec extends IntegrationTests {

	def formValidationRuleController
	
	def "test list"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		
		def element = newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def validationRule = newFormValidationRule(element, "", [(DISTRICT_HOSPITAL_GROUP)], "\$"+element.id+" > 0")
		formValidationRuleController = new FormValidationRuleController()
		
		when:
		formValidationRuleController.params['formElement.id'] = element.id
		formValidationRuleController.list()
		
		then:
		formValidationRuleController.modelAndView.model.entities.size() == 1
		
	}
	
	def "test group uuids are correctly saved"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def element = newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		formValidationRuleController = new FormValidationRuleController()
		
		when:
		formValidationRuleController.params['formElement.id'] = element.id
		formValidationRuleController.params['prefix'] = ""
		formValidationRuleController.params['typeCodes'] = [HEALTH_CENTER_GROUP]
		formValidationRuleController.params['expression'] = "true"
		formValidationRuleController.params['allowOutlier'] = false
		formValidationRuleController.params.messages = [:]
		formValidationRuleController.params.messages['en'] = "Validation rule"
		formValidationRuleController.saveWithoutTokenCheck()
		
		then:
		FormValidationRule.count() == 1
		FormValidationRule.list()[0].typeCodeString == Utils.unsplit([(HEALTH_CENTER_GROUP)])
		FormValidationRule.list()[0].messages['en'] == "Validation rule"
		
	}
	
	def "test copy validation rule"() {
		setup:
		def formElement = newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def validationRule = newFormValidationRule(formElement, "", [HEALTH_CENTER_GROUP, DISTRICT_HOSPITAL_GROUP], "1==1")
		formValidationRuleController = new FormValidationRuleController()
		
		when:
		formValidationRuleController.params['id'] = validationRule.id
		formValidationRuleController.copy()
		
		then:
		FormValidationRule.count() == 2
	}
	
}

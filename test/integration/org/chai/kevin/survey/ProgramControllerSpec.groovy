package org.chai.kevin.survey

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormSkipRuleElementMap;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.form.FormValidationRuleDependency;
import org.chai.kevin.value.Value;
import org.chai.location.DataLocation;

class ProgramControllerSpec extends SurveyIntegrationTests {

	def programController
	
	def "program list 404 when no survey"() {
		setup:
		programController = new ProgramController()
		
		when:
		programController.list()
		
		then:
		programController.modelAndView == null
	}
	
	def "program list"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [])
		programController = new ProgramController()
		
		when:
		programController.params['survey.id'] = survey.id
		programController.list()
		
		then:
		programController.modelAndView.model.entities.equals([program])
	}
	
	def "delete program deletes enterd program, deletes entered section, entered questions, values, skip rules and validation"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(1))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(BUTARO), false, false)
		newSurveyEnteredSection(section, period, DataLocation.findByCode(BUTARO), false, false)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(BUTARO), false, false, false)
		newSurveySkipRule(CODE(1), survey, "true", [(element): ''], [])
		newFormValidationRule(CODE(1), element, "", [(DISTRICT_HOSPITAL_GROUP)], "true", [])
		programController = new ProgramController()
		
		when:
		programController.params.id = program.id
		programController.delete()
		
		then:
		SurveyProgram.count() == 0
		SurveySection.count() == 0
		SurveySimpleQuestion.count() == 0
		SurveyElement.count() == 0
		FormEnteredValue.count() == 0
		SurveyEnteredQuestion.count() == 0
		SurveyEnteredSection.count() == 0
		SurveyEnteredProgram.count() == 0
		SurveySkipRule.count() == 1
		FormSkipRuleElementMap.count() == 0
		FormValidationRule.count() == 0
		FormValidationRuleDependency.count() == 0
	}
	
}

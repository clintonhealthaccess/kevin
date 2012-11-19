package org.chai.kevin.survey

import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormSkipRuleElementMap;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.form.FormValidationRuleDependency;
import org.chai.kevin.value.Value;
import org.chai.location.DataLocation;

class SurveyControllerSpec extends SurveyIntegrationTests {

	def surveyController
	
	def "create survey with active flag resets active flag on other surveys"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), [:], period, true)
		surveyController = new SurveyController()

		when:
		surveyController.params.code = survey.code+"2"
		surveyController.params['period.id'] = period.id
		surveyController.params.active = true
		surveyController.saveWithoutTokenCheck()
		
		then:
		Survey.count() == 2
		Survey.list()[1].active == true
		Survey.list()[0].active == false
	}
	
	def "create survey with active flag does not reset active flag on other survey if survey incomplete"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), [:], period, true)
		surveyController = new SurveyController()

		when:
		surveyController.params.code = survey.code+"2"
		surveyController.params.active = true
		surveyController.saveWithoutTokenCheck()
		
		then:
		Survey.count() == 1
		Survey.list()[0].active == true
	}

	
	def "delete survey deletes entered program, deletes entered section, entered questions, values, skip rules and validation"() {
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
		surveyController = new SurveyController()
		
		when:
		surveyController.params.id = survey.id
		surveyController.delete()
		
		then:
		Survey.count() == 0
		SurveyProgram.count() == 0
		SurveySection.count() == 0
		SurveySimpleQuestion.count() == 0
		SurveyElement.count() == 0
		FormEnteredValue.count() == 0
		SurveyEnteredQuestion.count() == 0
		SurveyEnteredSection.count() == 0
		SurveyEnteredProgram.count() == 0
		SurveySkipRule.count() == 0
		FormSkipRuleElementMap.count() == 0
		FormValidationRule.count() == 0
		FormValidationRuleDependency.count() == 0
	}
	
}

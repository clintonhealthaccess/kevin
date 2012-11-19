package org.chai.kevin.survey

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormSkipRuleElementMap;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.form.FormValidationRuleDependency;
import org.chai.kevin.value.Value;
import org.chai.location.DataLocation;

class SectionControllerSpec extends SurveyIntegrationTests {

	def sectionController
	
	def "section list 404 when no survey"() {
		setup:
		sectionController = new SectionController()
		
		when:
		sectionController.list()
		
		then:
		sectionController.modelAndView == null
	}
	
	def "section list"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [])
		def section = newSurveySection(CODE(1), program, 1, [])
		sectionController = new SectionController()
		
		when:
		sectionController.params['program.id'] = program.id
		sectionController.list()
		
		then:
		sectionController.modelAndView.model.entities.equals([section])
	}
	
	def "delete section deletes entered section, entered questions, values, skip rules and validation"() {
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
		newSurveySkipRule(CODE(1), survey, "true", [(element): ''], [])
		newFormValidationRule(CODE(1), element, "", [(DISTRICT_HOSPITAL_GROUP)], "true", [])
		sectionController = new SectionController()
		
		when:
		sectionController.params.id = section.id
		sectionController.delete()
		
		then:
		SurveySection.count() == 0
		SurveySimpleQuestion.count() == 0
		SurveyElement.count() == 0
		FormEnteredValue.count() == 0
		SurveyEnteredQuestion.count() == 0
		SurveyEnteredQuestion.count() == 0
		SurveySkipRule.count() == 1
		FormSkipRuleElementMap.count() == 0
		FormValidationRule.count() == 0
		FormValidationRuleDependency.count() == 0
	}
	
}

package org.chai.kevin.survey

import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.util.JSONUtils;

class SurveyCopyServiceSpec extends SurveyIntegrationTests {
	
	def surveyCopyService
	
	def "test clone double number of elements"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [])
		def section = newSurveySection(program, 1, [])
		def question = newTableQuestion(section, 1, [])
		
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		def column = newTableColumn(question, 1, [])
		def row = newTableRow(question, 1, [], [(column): element])
		
		expect:
		Survey.count() == 1
		SurveyProgram.count() == 1
		SurveySection.count() == 1
		SurveyTableQuestion.count() == 1
		SurveyTableColumn.count() == 1
		SurveyTableRow.count() == 1
		SurveyElement.count() == 1
			
		when:
		def surveyCopy = surveyCopyService.copySurvey(survey)
		def copy = surveyCopy.copy
				
		then:
		Survey.count() == 2
		SurveyProgram.count() == 2
		SurveySection.count() == 2
		SurveyTableQuestion.count() == 2
		SurveyTableColumn.count() == 2
		SurveyTableRow.count() == 2
		SurveyElement.count() == 2
		RawDataElement.count() == 1 
		
		survey != copy
		
		!copy.programs[0].equals(survey.programs[0])
		copy.programs[0].survey.equals(copy)
		survey.programs[0].survey.equals(survey)
		
		survey.names['en']+' (copy)' == copy.names['en']
	}
	
	def "test clone survey with skip rule"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [])
		def section = newSurveySection(program, 1, [])
		def question = newSimpleQuestion(section, 1, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		def skipRule = newSurveySkipRule(survey, "\$"+element.id+" == 1", [:], [])
		
		when:
		def surveyCopy = surveyCopyService.copySurvey(survey)
		def copy = surveyCopy.copy
		
		then:
		SurveySkipRule.count() == 2
		copy.getSkipRules().size() == 1
		copy.getSkipRules()[0].expression != survey.getSkipRules()[0].expression
		copy.getSkipRules()[0].expression == "\$" + SurveyElement.list()[1].id + " == 1"
		surveyCopy.getUnchangedSkipRules().size() == 0
	}
	
	def "test clone survey with validation rule from other survey"() {
		setup:
		def period = newPeriod()
		def survey1 = newSurvey(period)
		def program1 = newSurveyProgram(survey1, 1, [])
		def section1 = newSurveySection(program1, 1, [])
		def question1 = newSimpleQuestion(section1, 1, [])
		def dataElement1 = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element1 = newSurveyElement(question1, dataElement1)
		
		def survey2 = newSurvey(period)
		def program2 = newSurveyProgram(survey2, 1, [])
		def section2 = newSurveySection(program2, 1, [])
		def question2 = newSimpleQuestion(section2, 1, [])
		def dataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def element2 = newSurveyElement(question2, dataElement2)
		def validationRule = newFormValidationRule(element2, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "\$"+element1+" == 1", [element1])
		
		when:
		def surveyCopy = surveyCopyService.copySurvey(survey2)
		def copy = surveyCopy.copy
		
		then:
		FormValidationRule.count() == 2
		def element3 = SurveyElement.list()[2]
		element3.validationRules.size() == 1
		element3.validationRules.iterator().next().expression == element2.validationRules.iterator().next().expression
		surveyCopy.getUnchangedValidationRules().size() == 1
	}

	def "test clone survey with validation rule transforms dependencies"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [])
		def section = newSurveySection(program, 1, [])
		def question = newSimpleQuestion(section, 1, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		def validationRule = newFormValidationRule(element, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "1 == 1", [element])
		
		when:
		def surveyCopy = surveyCopyService.copySurvey(survey)
		def copy = surveyCopy.copy
		
		then:
		def elementCopy = SurveyElement.list()[1]
		elementCopy.validationRules.size() == 1
		elementCopy.validationRules.iterator().next().dependencies.size() == 1
		elementCopy.validationRules.iterator().next().dependencies[0].equals(elementCopy)
		surveyCopy.getUnchangedValidationRules().size() == 0
	}
	
		
}

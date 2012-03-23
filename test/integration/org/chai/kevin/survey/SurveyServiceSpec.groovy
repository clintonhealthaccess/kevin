package org.chai.kevin.survey

import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocationType;

class SurveyServiceSpec extends SurveyIntegrationTests {

	def surveyService
	
	def "get survey question"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(["en": "question"], section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(["en": "somethig"], section, 2, [(HEALTH_CENTER_GROUP)])
		
		expect:
		surveyService.getSurveyQuestion(question1.id).equals(question1)
		surveyService.getSurveyQuestion(question2.id).equals(question2)
	}
	
	def "search question test"() {
		setup:
		def period = newPeriod() 
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(["en": "question"], section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(["en": "somethig"], section, 2, [(HEALTH_CENTER_GROUP)])
		
		expect:
		surveyService.searchSurveyQuestions("que", survey).equals([question1])
		surveyService.searchSurveyQuestions("que some", survey).equals([])
	}
	
	def "search question - paging works"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(["en": "question"], section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(["en": "somethig"], section, 2, [(HEALTH_CENTER_GROUP)])
		
		expect:
		surveyService.searchSurveyQuestions("", survey, [offset: 0, max:1]).equals([question1])
		surveyService.searchSurveyQuestions("", survey, [offset: 1, max:1]).equals([question2])
	}
		

	def "test number of location applicable with all types"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = null
		def section = null
		def question = null
		def element = null
		
		when:
		program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])

		element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		then:
		element.getTypeApplicable().equals(new HashSet([(HEALTH_CENTER_GROUP)]))
		surveyService.getNumberOfApplicableDataLocationTypes(element) == 1
	}
	
	def "test number of location applicable with empty group"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = null
		def section = null
		def question = null
		def element = null
		
		when:
		program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		section = newSurveySection(program, 1, [])
		question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])

		element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
	
		then:
		element.getTypeApplicable().equals(new HashSet([]))
		surveyService.getNumberOfApplicableDataLocationTypes(element) == 0
	}
	
	
	def "test get survey elements for data element"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)

		when:
		def surveyElements = surveyService.getSurveyElements(dataElement, null)
		
		then:
		surveyElements.size() == 1
		surveyElements.iterator().next().equals(element)
		
	}

	def "search survey elements"() {
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def dataElement = newRawDataElement(j(["en": "element"]),CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)

		def surveyElements = null
				
		when: "search by element text"
		surveyElements = surveyService.searchSurveyElements("ele", survey, [], [:])
		
		then:
		surveyElements.equals([element])
		
		when: "search by element id"
		surveyElements = surveyService.searchSurveyElements(""+element.id, survey, [], [:])
		
		then:
		surveyElements.equals([element])
		
		when: "search by data element id"
		surveyElements = surveyService.searchSurveyElements(""+dataElement.id, survey, [], [:])
		
		then:
		surveyElements.equals([element])

		when: "search filtered by type"
		surveyElements = surveyService.searchSurveyElements("ele", survey, ['bool'], [:])
		
		then:
		surveyElements.isEmpty()

		when: "search filtered by type"
		surveyElements = surveyService.searchSurveyElements("ele", survey, ['number'], [:])
		
		then:
		surveyElements.equals([element])
				
		when: "search filtered by survey"
		def survey2 = newSurvey(period)
		surveyElements = surveyService.searchSurveyElements("ele", survey2, [], [:])
		
		then:
		surveyElements.isEmpty()
	}
	
}

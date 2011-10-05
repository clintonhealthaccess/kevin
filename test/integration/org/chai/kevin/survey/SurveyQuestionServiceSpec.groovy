package org.chai.kevin.survey

import org.chai.kevin.data.Type;

class SurveyQuestionServiceSpec extends SurveyIntegrationTests {

	def surveyQuestionService
	
	def "search question test"() {
		setup:
		def period = newPeriod() 
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(["en": "question"], section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(["en": "somethig"], section, 2, [(HEALTH_CENTER_GROUP)])
		
		expect:
		surveyQuestionService.searchSurveyQuestions("que", survey).equals([question1])
		surveyQuestionService.searchSurveyQuestions("que some", survey).equals([])
	}
	
	def "search question - paging works"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(["en": "question"], section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(["en": "somethig"], section, 2, [(HEALTH_CENTER_GROUP)])
		
		expect:
		surveyQuestionService.searchSurveyQuestions("", survey, [offset: 0, max:1]).equals([question1])
		surveyQuestionService.searchSurveyQuestions("", survey, [offset: 1, max:1]).equals([question2])
		
	}
	
}

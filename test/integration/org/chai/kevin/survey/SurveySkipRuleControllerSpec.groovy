package org.chai.kevin.survey

import org.chai.kevin.data.Type;
import org.chai.kevin.survey.validation.SurveySkipRuleController;

class SurveySkipRuleControllerSpec extends SurveyIntegrationTests {

	def surveySkipRuleController
	def sessionFactory
	
	def "save skip rule"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		(0..12).each{order -> newSimpleQuestion(section, order, [(HEALTH_CENTER_GROUP)]) }
		
		def surveySkipRuleController = new SurveySkipRuleController()
		
		expect:
		SurveySimpleQuestion.count() == 13
		
		when:
		surveySkipRuleController.params['survey.id'] = survey.id
		surveySkipRuleController.params.skippedSurveyQuestions = SurveySimpleQuestion.findByOrder(12).id+''
		surveySkipRuleController.params.expression = '1'
		surveySkipRuleController.saveWithoutTokenCheck()
		
		then:
		SurveySkipRule.count() == 1
		SurveySkipRule.list()[0].skippedSurveyQuestions.size() == 1
		
	}
	
}

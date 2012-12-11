package org.chai.kevin.survey

import org.chai.location.DataLocation
import org.chai.kevin.data.Type;
import org.chai.kevin.survey.validation.SurveySkipRuleController;

class SurveySkipRuleControllerSpec extends SurveyIntegrationTests {

	def surveySkipRuleController
	def sessionFactory
	
	def "save skip rule"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		(0..12).each{order -> newSimpleQuestion(CODE(order), section, order, [(HEALTH_CENTER_GROUP)]) }
		
		surveySkipRuleController = new SurveySkipRuleController()
		
		expect:
		SurveySimpleQuestion.count() == 13
		
		when:
		surveySkipRuleController.params['code'] = 'code'
		surveySkipRuleController.params['survey.id'] = survey.id
		surveySkipRuleController.params.skippedSurveyQuestions = SurveySimpleQuestion.findByOrder(12).id+''
		surveySkipRuleController.params.expression = '1'
		surveySkipRuleController.saveWithoutTokenCheck()
		
		then:
		SurveySkipRule.count() == 1
		SurveySkipRule.list()[0].skippedSurveyQuestions.size() == 1
	}
	
	def "list skip rules"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def skipRule = newSurveySkipRule(CODE(1), survey, "1 == 1", [:], [])
		surveySkipRuleController = new SurveySkipRuleController()
		
		when:
		surveySkipRuleController.params['survey.id'] = survey.id
		surveySkipRuleController.list()
		
		then:
		surveySkipRuleController.modelAndView.model.entities.equals([skipRule])
	}
	
	def "list skip rule when no survey 404"() {
		setup:
		surveySkipRuleController = new SurveySkipRuleController()
		
		when:
		surveySkipRuleController.list()
		
		then:
		surveySkipRuleController.modelAndView == null
	}
	
	def "delete skip rules deletes them from survey entered question"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def skipRule = newSurveySkipRule(CODE(1), survey, "1 == 1", [:], [])
		def enteredQuestion = newSurveyEnteredQuestion(question, period, DataLocation.findByCode(KIVUYE), false, true)
		enteredQuestion.addToSkippedRules(skipRule)
		enteredQuestion.save(failOnError: true)
		surveySkipRuleController = new SurveySkipRuleController()
		
		when:
		surveySkipRuleController.params['id'] = skipRule.id
		surveySkipRuleController.delete()
		
		then:
		SurveySkipRule.count() == 0
		SurveyEnteredQuestion.list()[0].skippedRules.empty == true
	}
	
}

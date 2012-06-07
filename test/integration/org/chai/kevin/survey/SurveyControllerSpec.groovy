package org.chai.kevin.survey

class SurveyControllerSpec extends SurveyIntegrationTests {

	def surveyController
	
	def "create survey with active flag resets active flag on other surveys"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), j([:]), period, true)
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
		def survey = newSurvey(CODE(1), j([:]), period, true)
		surveyController = new SurveyController()

		when:
		surveyController.params.code = survey.code+"2"
		surveyController.params.active = true
		surveyController.saveWithoutTokenCheck()
		
		then:
		Survey.count() == 1
		Survey.list()[0].active == true
	}
	
}

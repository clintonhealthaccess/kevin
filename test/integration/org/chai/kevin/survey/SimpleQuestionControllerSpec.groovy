package org.chai.kevin.survey

class SimpleQuestionControllerSpec extends SurveyIntegrationTests {

	def simpleQuestionController
	
	def "create question works"() {
		setup:
		
		simpleQuestionController = new SimpleQuestionController()
		
		when:
		simpleQuestionController.params['surveyElement.dataElement.id'] = dataElement.id
		
	}
	
}

package org.chai.kevin.exports

class SurveyExportControllerSpec {

	def surveyExportController
	
	def "export survey works"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		surveyExportController = new SurveyExportController()
		
		when:
		surveyExportController.params.location = Location.findByCode(RWANDA).id
		surveyExportController.params.survey = survey.id
		surveyExportController.export()
		
		then:
		surveyExportController.response.getContentType() == "application/zip"
	}
	
}

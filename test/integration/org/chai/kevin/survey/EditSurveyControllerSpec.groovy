package org.chai.kevin.survey

class EditSurveyControllerSpec extends SurveyIntegrationTests {

	def editSurveyController
	
	def "get survey page with null survey elements"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(objective, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newTableQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def column = newTableColumn(question1, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def row = newTableRow(question1, 1, [(DISTRICT_HOSPITAL_GROUP)], [(column): null])

		def question2 = newSimpleQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])

		def question3 = newCheckboxQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def option = newCheckboxOption(question3, 1, [(DISTRICT_HOSPITAL_GROUP)], null)
		editSurveyController = new EditSurveyController()
		
		when:
		editSurveyController.params.organisation = getOrganisation(BUTARO).id
		editSurveyController.params.section = section.id
		editSurveyController.sectionPage()
		
		then:
		editSurveyController.modelAndView.model.surveyPage.survey.equals(survey)
		editSurveyController.modelAndView.model.surveyPage.section.equals(section)
		
	}
	
	def "get survey page with valid parameters"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		editSurveyController = new EditSurveyController()
		
		when:
		editSurveyController.params.organisation = getOrganisation(BUTARO).id
		editSurveyController.params.survey = survey.id
		editSurveyController.surveyPage()
		
		then:
		editSurveyController.modelAndView.model.surveyPage.survey.equals(survey)
	}
	
}

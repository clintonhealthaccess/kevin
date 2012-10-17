package org.chai.kevin.survey

import org.chai.location.DataLocation;
import org.chai.location.Location;

class EditSurveyControllerSpec extends SurveyIntegrationTests {

	def editSurveyController
	
	def "get survey page with null survey elements"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newTableQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def column = newTableColumn(CODE(1), question1, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def row = newTableRow(CODE(1), question1, 1, [(DISTRICT_HOSPITAL_GROUP)], [(column): null])

		def question2 = newSimpleQuestion(CODE(2), section, 1, [(DISTRICT_HOSPITAL_GROUP)])

		def question3 = newCheckboxQuestion(CODE(3), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def option = newCheckboxOption(CODE(1), question3, 1, [(DISTRICT_HOSPITAL_GROUP)], null)
		editSurveyController = new EditSurveyController()
		
		when:
		editSurveyController.params.location = DataLocation.findByCode(BUTARO).id
		editSurveyController.params.section = section.id
		editSurveyController.sectionPage()
		
		then:
		editSurveyController.modelAndView.model.surveyPage.survey.equals(survey)
		editSurveyController.modelAndView.model.surveyPage.section.equals(section)
		
	}
	
	def "get survey page with valid parameters"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		editSurveyController = new EditSurveyController()
		
		when:
		editSurveyController.params.location = DataLocation.findByCode(BUTARO).id
		editSurveyController.params.survey = survey.id
		editSurveyController.surveyPage()
		
		then:
		editSurveyController.modelAndView.model.surveyPage.survey.equals(survey)
	}
		
	def "access to view action redirects to active survey if SurveyUser"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), [:], period, true)
		editSurveyController = new EditSurveyController()
		
		when:
		editSurveyController.view()
		
		then:
		editSurveyController.response.redirectedUrl == '/editSurvey/surveyPage/'+DataLocation.findByCode(BUTARO).id+'?survey='+survey.id
	}
	
	def "access to view action redirects to 404 if no active survey with SurveyUser"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		editSurveyController = new EditSurveyController()
		
		when:
		editSurveyController.view()
		
		then:
		editSurveyController.response.redirectedUrl == null
	}
	
	def "access to view action redirects to summary page if normal User"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		editSurveyController = new EditSurveyController()
		
		when:
		editSurveyController.view()
		
		then:
		editSurveyController.response.redirectedUrl == '/surveySummary/summaryPage'
	}
	
//	def "refresh survey works with location"() {
//		setup:
//		setupLocationTree()
//		def period = newPeriod()
//		def survey = newSurvey(period)
//		def program = newSurveyProgram(survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
//		def section = newSurveySection(program, 1, [(DISTRICT_HOSPITAL_GROUP)])
//		def question1 = newSimpleQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])
//		editSurveyController = new EditSurveyController()
//		
//		when:
//		editSurveyController.params.location = Location.findByCode(RWANDA).id
//		editSurveyController.params.survey = survey.id
//		editSurveyController.refresh()
//		
//		then:
//		editSurveyController.redirectedUrl.contains('editSurvey/surveyPage')
//	}
	
//	def "refresh survey works with data location"() {
//		setup:
//		setupLocationTree()
//		def period = newPeriod()
//		def survey = newSurvey(period)
//		def program = newSurveyProgram(survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
//		def section = newSurveySection(program, 1, [(DISTRICT_HOSPITAL_GROUP)])
//		def question1 = newSimpleQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])
//		editSurveyController = new EditSurveyController()
//		
//		when:
//		editSurveyController.params.location = DataLocation.findByCode(BUTARO).id
//		editSurveyController.params.survey = survey.id
//		editSurveyController.refresh()
//		
//		then:
//		editSurveyController.redirectedUrl.contains('editSurvey/surveyPage')
//	}
	
}

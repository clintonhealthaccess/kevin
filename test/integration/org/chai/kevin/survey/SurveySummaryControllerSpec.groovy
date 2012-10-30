package org.chai.kevin.survey

import org.chai.kevin.Period;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.kevin.security.User;

class SurveySummaryControllerSpec extends SurveyIntegrationTests {

	static transactional = false
	
	def surveySummaryController
	
	def cleanup() {
		SurveyEnteredProgram.executeUpdate("delete SurveyEnteredProgram")
		SurveyEnteredSection.executeUpdate("delete SurveyEnteredSection")
		SurveyEnteredQuestion.executeUpdate("delete SurveyEnteredQuestion")
		FormEnteredValue.executeUpdate("delete FormEnteredValue")
		SurveyElement.executeUpdate("delete SurveyElement")
		SurveySimpleQuestion.executeUpdate("delete SurveySimpleQuestion")
		SurveySection.executeUpdate("delete SurveySection")
		SurveyProgram.executeUpdate("delete SurveyProgram")
		Survey.executeUpdate("delete Survey")
		RawDataElement.executeUpdate("delete RawDataElement")
		DataLocation.executeUpdate("delete DataLocation")
		Location.executeUpdate("delete Location")
		LocationLevel.executeUpdate("delete LocationLevel")
		DataLocationType.executeUpdate("delete DataLocationType")
		Period.executeUpdate("delete Period")
		User.executeUpdate("delete User")
		sessionFactory.currentSession.flush()
	}
	
	
	def "test refresh"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		surveySummaryController = new SurveySummaryController()
		
		when:
		surveySummaryController.params.location = DataLocation.findByCode(KIVUYE).id+''
		surveySummaryController.params.survey = survey.id+''
		surveySummaryController.refresh()
		
		then:
		SurveyEnteredProgram.count() == 1
		SurveyEnteredSection.count() == 1
		SurveyEnteredQuestion.count() == 1
		FormEnteredValue.count() == 1
	}
	
	def "test refresh with inexistant location and survey"() {
		setup:
		setupSecurityManager(newUser('test', 'uuid'))
		
		surveySummaryController = new SurveySummaryController()
		
		when:
		surveySummaryController.params.location = '1'
		surveySummaryController.params.survey = '1'
		surveySummaryController.refresh()
		
		then:
		surveySummaryController.response.redirectedUrl == null
		surveySummaryController.modelAndView == null
	}
	
}

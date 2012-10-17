package org.chai.kevin.survey;

import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.survey.summary.SurveySummaryPage;

class SummaryServiceSpec extends SurveyIntegrationTests {
	
	def summaryService
	
	def "test summary page order"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def enteredProgram = newSurveyEnteredProgram(program, period, DataLocation.findByCode(KIVUYE), false, false, false, 0, 2)
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def enteredSection = newSurveyEnteredSection(section, period, DataLocation.findByCode(KIVUYE),false, false, 0, 2)
		def question1 = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def enteredQuestion1 = newSurveyEnteredQuestion(question1, period, DataLocation.findByCode(KIVUYE), false, false)
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def question2 = newSimpleQuestion(CODE(2), section, 2, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def enteredQuestion2 = newSurveyEnteredQuestion(question2, period, DataLocation.findByCode(KIVUYE), false, false)
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))
		
		def summaryPage
		def questionSummary
		
		when:
		summaryPage = summaryService.getSurveySummaryPage(Location.findByCode(RWANDA), null, survey)
		questionSummary = summaryPage.getQuestionSummary(DataLocation.findByCode(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		
		when:
		summaryPage = summaryService.getProgramSummaryPage(Location.findByCode(RWANDA), null, program)
		questionSummary = summaryPage.getQuestionSummary(DataLocation.findByCode(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		
		when:
		summaryPage = summaryService.getSectionSummaryPage(Location.findByCode(RWANDA), null, section)
		questionSummary = summaryPage.getQuestionSummary(DataLocation.findByCode(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		
		when:
		summaryPage = summaryService.getSurveySummaryPage(Location.findByCode(RWANDA), null, survey)
		summaryPage.sort(SurveySummaryPage.LOCATION_SORT, 'desc', 'en')
		questionSummary = summaryPage.getQuestionSummary(DataLocation.findByCode(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		summaryPage.locations.equals([DataLocation.findByCode(KIVUYE), DataLocation.findByCode(BUTARO)])
		
		when:
		summaryPage = summaryService.getSurveySummaryPage(Location.findByCode(RWANDA), null, survey)
		summaryPage.sort(SurveySummaryPage.LOCATION_SORT, 'asc', 'en')
		questionSummary = summaryPage.getQuestionSummary(DataLocation.findByCode(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		summaryPage.locations.equals([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)])
		
		when:
		enteredQuestion1.complete = true
		enteredQuestion1.save(failOnError: true)
		enteredSection.completedQuestions = 1
		enteredSection.save(failOnError: true)
		enteredProgram.completedQuestions = 1
		enteredProgram.save(failOnError: true)
		summaryPage = summaryService.getSurveySummaryPage(Location.findByCode(RWANDA), null, survey)
		summaryPage.sort(SurveySummaryPage.PROGRESS_SORT, 'asc', 'en')
		questionSummary = summaryPage.getQuestionSummary(DataLocation.findByCode(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 1
		summaryPage.locations.equals([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)])
	}
	
	def "test program summary page with types"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def question2 = newSimpleQuestion(CODE(2), section, 2, [(HEALTH_CENTER_GROUP)])
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))		
		
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])				
		
		when:
		def burera = Location.findByCode(BURERA)
		def summaryPage = summaryService.getProgramSummaryPage(burera, types, program)
		def dataLocations = burera.collectDataLocations(null, types);
		
		then:
		dataLocations.equals([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)])			
		!dataLocations.equals(summaryPage.locations)
		summaryPage.locations.equals([DataLocation.findByCode(KIVUYE)])
	}

	def "test section summary page with types"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def question2 = newSimpleQuestion(CODE(2), section, 2, [(HEALTH_CENTER_GROUP)])
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))		
		
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])				
		
		when:
		def burera = Location.findByCode(BURERA)
		def summaryPage = summaryService.getSectionSummaryPage(burera, types, section)
		def dataLocations = burera.collectDataLocations(null, types);
		
		then:
		dataLocations.equals([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)])			
		!dataLocations.equals(summaryPage.locations)
		summaryPage.locations.equals([DataLocation.findByCode(KIVUYE)])
	}
		
	
	def "test locations are collected at all levels"() {
		setupLocationTree()
		def north = Location.findByCode(NORTH)
		newDataLocation(j(["en":'DP']), "DP", north, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP));
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		
		when:
		def summaryPage = summaryService.getSurveySummaryPage(Location.findByCode(RWANDA), null, survey)
		
		then:
		s(summaryPage.locations*.code).equals(s([BUTARO, KIVUYE, "DP"]))
		
	}
}

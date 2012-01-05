package org.chai.kevin.survey;

import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.location.LocationEntity;
import org.hisp.dhis.organisationunit.OrganisationUnit;

class SummaryServiceSpec extends SurveyIntegrationTests {
	
	def summaryService
	
	def "test summary page order"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def question2 = newSimpleQuestion(section, 2, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))
		
		def summaryPage
		def questionSummary
		
		when:
		summaryPage = summaryService.getSurveySummaryPage(LocationEntity.findByCode(RWANDA), survey)
		questionSummary = summaryPage.getQuestionSummary(DataEntity.findByCode(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		
		when:
		summaryPage = summaryService.getObjectiveSummaryPage(LocationEntity.findByCode(RWANDA), objective)
		questionSummary = summaryPage.getQuestionSummary(DataEntity.findByCode(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		
		when:
		summaryPage = summaryService.getSectionSummaryPage(LocationEntity.findByCode(RWANDA), section)
		questionSummary = summaryPage.getQuestionSummary(DataEntity.findByCode(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		
		when:
		summaryPage = summaryService.getSurveySummaryPage(LocationEntity.findByCode(RWANDA), survey)
		summaryPage.sort(SummaryPage.FACILITY_SORT, 'desc', 'en')
		questionSummary = summaryPage.getQuestionSummary(DataEntity.findByCode(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		summaryPage.facilities.equals([DataEntity.findByCode(KIVUYE), DataEntity.findByCode(BUTARO)])
		
		when:
		summaryPage = summaryService.getSurveySummaryPage(LocationEntity.findByCode(RWANDA), survey)
		summaryPage.sort(SummaryPage.FACILITY_SORT, 'asc', 'en')
		questionSummary = summaryPage.getQuestionSummary(DataEntity.findByCode(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		summaryPage.facilities.equals([DataEntity.findByCode(BUTARO), DataEntity.findByCode(KIVUYE)])
		
		when:
		newSurveyEnteredQuestion(question1, period, DataEntity.findByCode(KIVUYE), false, true)
		summaryPage = summaryService.getSurveySummaryPage(LocationEntity.findByCode(RWANDA), survey)
		summaryPage.sort(SummaryPage.PROGRESS_SORT, 'asc', 'en')
		questionSummary = summaryPage.getQuestionSummary(DataEntity.findByCode(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 1
		summaryPage.facilities.equals([DataEntity.findByCode(BUTARO), DataEntity.findByCode(KIVUYE)])
	}
	
	def "test counted questions does not apply to group"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newSurveyEnteredQuestion(question1, period, DataEntity.findByCode(BUTARO), false, true)
		def summaryPage = summaryService.getSurveySummaryPage(LocationEntity.findByCode(RWANDA), survey)
		def questionSummary = summaryPage.getQuestionSummary(DataEntity.findByCode(BUTARO))
		
		then:
		questionSummary.questions == 0
		questionSummary.completedQuestions == 1
	}
	
}

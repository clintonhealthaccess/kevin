package org.chai.kevin.survey;

import org.chai.kevin.data.Type;
import org.hisp.dhis.organisationunit.OrganisationUnit;

class SummaryServiceSpec extends SurveyIntegrationTests {
	
	def summaryService
	
	def "test summary page order"() {
		setup:
		setupOrganisationUnitTree()
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
		summaryPage = summaryService.getSurveySummaryPage(getOrganisation(RWANDA), survey)
		questionSummary = summaryPage.getQuestionSummary(getOrganisation(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		
		when:
		summaryPage = summaryService.getObjectiveSummaryPage(getOrganisation(RWANDA), objective)
		questionSummary = summaryPage.getQuestionSummary(getOrganisation(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		
		when:
		summaryPage = summaryService.getSectionSummaryPage(getOrganisation(RWANDA), section)
		questionSummary = summaryPage.getQuestionSummary(getOrganisation(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		
		when:
		summaryPage = summaryService.getSurveySummaryPage(getOrganisation(RWANDA), survey)
		summaryPage.sort(SummaryPage.FACILITY_SORT, 'desc')
		questionSummary = summaryPage.getQuestionSummary(getOrganisation(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		summaryPage.facilities.equals([getOrganisation(KIVUYE), getOrganisation(BUTARO)])
		
		when:
		summaryPage = summaryService.getSurveySummaryPage(getOrganisation(RWANDA), survey)
		summaryPage.sort(SummaryPage.FACILITY_SORT, 'asc')
		questionSummary = summaryPage.getQuestionSummary(getOrganisation(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 0
		summaryPage.facilities.equals([getOrganisation(BUTARO), getOrganisation(KIVUYE)])
		
		when:
		newSurveyEnteredQuestion(question1, period, OrganisationUnit.findByName(KIVUYE), false, true)
		summaryPage = summaryService.getSurveySummaryPage(getOrganisation(RWANDA), survey)
		summaryPage.sort(SummaryPage.PROGRESS_SORT, 'asc')
		questionSummary = summaryPage.getQuestionSummary(getOrganisation(KIVUYE))
		
		then:
		questionSummary.questions == 2
		questionSummary.completedQuestions == 1
		summaryPage.facilities.equals([getOrganisation(BUTARO), getOrganisation(KIVUYE)])
	}
	
	def "test counted questions does not apply to group"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newSurveyEnteredQuestion(question1, period, OrganisationUnit.findByName(BUTARO), false, true)
		def summaryPage = summaryService.getSurveySummaryPage(getOrganisation(RWANDA), survey)
		def questionSummary = summaryPage.getQuestionSummary(getOrganisation(BUTARO))
		
		then:
		questionSummary.questions == 0
		questionSummary.completedQuestions == 1
	}
	
}

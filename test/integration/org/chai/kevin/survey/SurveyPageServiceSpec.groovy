package org.chai.kevin.survey

import org.chai.kevin.data.Type;
import org.hisp.dhis.organisationunit.OrganisationUnit;

class SurveyPageServiceSpec extends SurveyIntegrationTests {

	def surveyPageService
	
	def "test submit objective"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newSurveyEnteredValue(element, period, OrganisationUnit.findByName(KIVUYE), v("1"))
		newSurveyEnteredQuestion(question, period, OrganisationUnit.findByName(KIVUYE), false, true)
		newSurveyEnteredSection(section, period, OrganisationUnit.findByName(KIVUYE), false, true)
		newSurveyEnteredObjective(objective, period, OrganisationUnit.findByName(KIVUYE), false, true, false)
				
		then:
		surveyPageService.submit(getOrganisation(KIVUYE), objective) == true
		
	}
	
}

package org.chai.kevin.survey

import org.chai.kevin.AbstractController;
import org.chai.kevin.Organisation;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class SummaryController extends AbstractController {

	SummaryService summaryService
	
	def index = {
		redirect (action: 'summaryPage', params: params)
	}
	
	// TODO refactor into several actions for survey/objective/section
	def summaryPage = {
		Organisation organisation = getOrganisation(false)

		SurveySection section = SurveySection.get(params.int('section'))
		SurveyObjective objective = SurveyObjective.get(params.int('objective'))
		Survey survey = Survey.get(params.int('survey'))
		
		def template = null;
		SummaryPage summaryPage = null;
		
		// TODO build different classes for this and refactor into several actions
		if (section != null && organisation != null) {
			summaryPage = summaryService.getSectionSummaryPage(organisation, section)
			template = '/survey/summary/summarySectionTable'
		}
		else if (objective != null && organisation != null) {
			summaryPage = summaryService.getObjectiveSummaryPage(organisation, objective)
			template = '/survey/summary/summaryObjectiveTable'
		}
		else if (survey != null && organisation != null) {
			summaryPage = summaryService.getSurveySummaryPage(organisation, survey);
			template = '/survey/summary/summarySurveyTable'
		}

		if (summaryPage != null) summaryPage.sort(params.sort, params.order)
			
		Integer organisationLevel = ConfigurationHolder.config.facility.level;
		def organisationTree = organisationService.getOrganisationTreeUntilLevel(organisationLevel)

		render (view: '/survey/summary/summaryPage', model: [
			currentSurvey: survey,
			currentObjective: objective,
			currentSection: section,
			organisation: organisation,
			summaryPage: summaryPage,
			surveys: Survey.list(),
			organisationTree: organisationTree,
			template: template
		])
	}

	def objectiveTable = {
		Organisation currentOrganisation = getOrganisation(false)
		Survey currentSurvey = Survey.get(params.int('survey'))

		SummaryPage summaryPage = summaryService.getObjectiveTable(currentOrganisation, currentSurvey)

		render (view: '/survey/summary/objectiveTable', model: [
			organisation: currentOrganisation,
			summaryPage: summaryPage
		])
	}

	def sectionTable = {
		Organisation currentOrganisation = getOrganisation(false)
		SurveyObjective currentObjective = SurveyObjective.get(params.int('objective'))

		SummaryPage summaryPage = summaryService.getSectionTable(currentOrganisation, currentObjective)

		render (view: '/survey/summary/sectionTable', model: [
			organisation: currentOrganisation,
			summaryPage: summaryPage
		])
	}
	
}

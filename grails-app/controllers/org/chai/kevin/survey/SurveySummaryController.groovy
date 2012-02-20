package org.chai.kevin.survey

import org.chai.kevin.AbstractController;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.survey.summary.SurveySummaryPage;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class SurveySummaryController extends AbstractController {

	def summaryService
	def languageService
	
	def index = {
		redirect (action: 'summaryPage', params: params)
	}
	
	// TODO refactor into several actions for survey/objective/section
	def summaryPage = {
		LocationEntity entity = LocationEntity.get(params.int('location'))

		SurveySection section = SurveySection.get(params.int('section'))
		SurveyObjective objective = SurveyObjective.get(params.int('objective'))
		Survey survey = Survey.get(params.int('survey'))
		
		def template = null;
		SurveySummaryPage summaryPage = null;
		
		// TODO build different classes for this and refactor into several actions
		if (section != null && entity != null) {
			summaryPage = summaryService.getSectionSummaryPage(entity, section)
			template = '/survey/summary/summarySectionTable'
		}
		else if (objective != null && entity != null) {
			summaryPage = summaryService.getObjectiveSummaryPage(entity, objective)
			template = '/survey/summary/summaryObjectiveTable'
		}
		else if (survey != null && entity != null) {
			summaryPage = summaryService.getSurveySummaryPage(entity, survey);
			template = '/survey/summary/summarySurveyTable'
		}

		if (summaryPage != null) summaryPage.sort(params.sort, params.order, languageService.currentLanguage)
			
		render (view: '/survey/summary/summaryPage', model: [
			currentSurvey: survey,
			currentObjective: objective,
			currentSection: section,
			currentLocation: entity,
			summaryPage: summaryPage,
			surveys: Survey.list(),
			template: template
		])
	}

	def objectiveTable = {
		DataLocationEntity entity = DataLocationEntity.get(params.int('location'))
		Survey currentSurvey = Survey.get(params.int('survey'))

		SurveySummaryPage summaryPage = summaryService.getObjectiveTable(entity, currentSurvey)

		render (view: '/survey/summary/objectiveTable', model: [
			location: entity,
			summaryPage: summaryPage
		])
	}

	def sectionTable = {
		DataLocationEntity entity = DataLocationEntity.get(params.int('location'))
		SurveyObjective currentObjective = SurveyObjective.get(params.int('objective'))

		SurveySummaryPage summaryPage = summaryService.getSectionTable(entity, currentObjective)

		render (view: '/survey/summary/sectionTable', model: [
			location: entity,
			summaryPage: summaryPage
		])
	}
	
}

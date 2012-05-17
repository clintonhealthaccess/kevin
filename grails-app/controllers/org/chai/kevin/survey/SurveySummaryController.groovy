package org.chai.kevin.survey

import org.chai.kevin.AbstractController;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.survey.summary.SurveySummaryPage;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class SurveySummaryController extends AbstractController {
	
	def summaryService
	def languageService
	def surveyPageService
	
	def index = {
		redirect (action: 'summaryPage', params: params)
	}
	
	// TODO refactor into several actions for survey/program/section
	def summaryPage = {
		Location location = Location.get(params.int('location'))

		SurveySection section = SurveySection.get(params.int('section'))
		SurveyProgram program = SurveyProgram.get(params.int('program'))
		Survey survey = Survey.get(params.int('survey'))
		
		def template = null;
		SurveySummaryPage summaryPage = null;
		
		// TODO build different classes for this and refactor into several actions
		if (section != null && location != null) {
			summaryPage = summaryService.getSectionSummaryPage(location, section)
			template = '/survey/summary/summarySectionTable'
		}
		else if (program != null && location != null) {
			summaryPage = summaryService.getProgramSummaryPage(location, program)
			template = '/survey/summary/summaryProgramTable'
		}
		else if (survey != null && location != null) {
			summaryPage = summaryService.getSurveySummaryPage(location, survey);
			template = '/survey/summary/summarySurveyTable'
		}

		if (summaryPage != null) summaryPage.sort(params.sort, params.order, languageService.currentLanguage)
			
		render (view: '/survey/summary/summaryPage', model: [
			currentSurvey: survey,
			currentProgram: program,
			currentSection: section,
			currentLocation: location,
			summaryPage: summaryPage,
			surveys: Survey.list(),
			template: template
		])
	}

	def programTable = {
		DataLocation entity = DataLocation.get(params.int('location'))
		Survey currentSurvey = Survey.get(params.int('survey'))

		SurveySummaryPage summaryPage = summaryService.getProgramTable(entity, currentSurvey)

		render (view: '/survey/summary/programTable', model: [
			location: entity,
			summaryPage: summaryPage
		])
	}

	def sectionTable = {
		DataLocation entity = DataLocation.get(params.int('location'))
		SurveyProgram currentProgram = SurveyProgram.get(params.int('program'))

		SurveySummaryPage summaryPage = summaryService.getSectionTable(entity, currentProgram)

		render (view: '/survey/summary/sectionTable', model: [
			location: entity,
			summaryPage: summaryPage
		])
	}
	
	def submitAll = {
		if (log.isDebugEnabled()) log.debug("survey.submit, params:"+params)

		Survey survey = Survey.get(params.int('survey'))		
		Location location = Location.get(params.int('location'));
		
		boolean success = surveyPageService.submitAll(location, survey);

		if (success) {
			flash.message = message(code: "survey.all.submitted", default: "Thanks for submitting.");
		}
		else {
			flash.message = message(code: "survey.all.review", default: "The surveys could not be submitted, please review the programs and sections.");
		}

//		SurveySummaryPage summaryPage = summaryService.getSurveySummaryPage(location, survey);
//		def template = '/survey/summary/summarySurveyTable'
		
//		if (summaryPage != null) summaryPage.sort(params.sort, params.order, languageService.currentLanguage)
		
		redirect(action: 'summaryPage', params: [location: location.id, survey: survey.id])
//		render (view: '/survey/summary/summaryPage', model: [
//			currentSurvey: survey,
//			currentProgram: null,
//			currentSection: null,
//			currentLocation: location,
//			summaryPage: summaryPage,
//			surveys: Survey.list(),
//			template: template
//		])				
	}
}

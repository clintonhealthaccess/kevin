package org.chai.kevin.survey

import org.chai.kevin.AbstractController;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocationType;
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
		Set<DataLocationType> dataLocationTypes = getLocationTypes()
		
		SurveySection section = SurveySection.get(params.int('section'))
		SurveyProgram program = SurveyProgram.get(params.int('program'))
		Survey survey = Survey.get(params.int('survey'))
		
		def template = null;
		SurveySummaryPage summaryPage = null;
		
		// TODO build different classes for this and refactor into several actions
		if (section != null && location != null) {
			summaryPage = summaryService.getSectionSummaryPage(location, dataLocationTypes, section)
			template = '/survey/summary/summarySectionTable'
		}
		else if (program != null && location != null) {
			summaryPage = summaryService.getProgramSummaryPage(location, dataLocationTypes, program)
			template = '/survey/summary/summaryProgramTable'
		}
		else if (survey != null && location != null) {
			summaryPage = summaryService.getSurveySummaryPage(location, dataLocationTypes, survey);
			template = '/survey/summary/summarySurveyTable'
		}

		def locationSkipLevels = surveyPageService.getSkipLocationLevels()
		def submitSkipLevels = surveyPageService.getSkipSubmitLevels()
		
		if (summaryPage != null) summaryPage.sort(params.sort, params.order, languageService.currentLanguage)
			
		render (view: '/survey/summary/summaryPage', model: [
			currentSurvey: survey,
			currentProgram: program,
			currentSection: section,
			currentLocation: location,
			currentLocationTypes: dataLocationTypes,
			summaryPage: summaryPage,
			surveys: Survey.list(),
			template: template,
			locationSkipLevels: locationSkipLevels,
			submitSkipLevels: submitSkipLevels
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

	def refresh = {
		if (log.isDebugEnabled()) log.debug("survey.refresh, params:"+params)

		DataLocation location = locationService.getCalculationLocation(params.int('location'), DataLocation.class)
		Survey survey = Survey.get(params.int('survey'))
		
		if (survey != null && location != null) {
			surveyPageService.refresh(location, survey,
				(params.boolean('closeIfComplete')==null?false:params.boolean('closeIfComplete')),
				(params.boolean('reset')==null?false:params.boolean('reset')), 
				null
			);
		
			redirect (controller: 'editSurvey', action: "surveyPage", params: [location: location.id, survey: survey.id])
		} 
		else {
			response.sendError(404)
		}
	}
	
	def submitAll = {
		// TODO this should be a task
		if (log.isDebugEnabled()) log.debug("survey.submit, params:"+params)

		SurveyProgram program = SurveyProgram.get(params.int('program'))
		Survey survey = Survey.get(params.int('survey'))
		
		Location location = Location.get(params.int('location'))		
		def submitLocation = Location.get(params.int('submitLocation'))
		if(submitLocation == null) submitLocation = DataLocation.get(params.int('submitLocation'))						
		Set<DataLocationType> dataLocationTypes = getLocationTypes()
		
		boolean success = false
		if (submitLocation != null && dataLocationTypes != null && (survey != null || program != null)) {
			// TODO create task with that stuff
			success = surveyPageService.submitAll(submitLocation, dataLocationTypes, survey, program);
		}

		if (success) {
			flash.message = message(code: "survey.all.submitted", default: "Thanks for submitting.");
		}
		else{
			if(program != null) flash.message = message(code: "program.all.review", default: "The programs could not be submitted.");
			else flash.message = message(code: "survey.all.review", default: "The surveys could not be submitted.");
		}		
		
		redirect(action: 'summaryPage', params: [
			location: location.id, 
			survey: survey?.id, 
			program: program?.id, 
			dataLocationTypes: dataLocationTypes.collect { it.getId() }
		])
	}
}

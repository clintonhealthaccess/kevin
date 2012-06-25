package org.chai.kevin.survey;
/** 
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.chai.kevin.AbstractController
import org.chai.kevin.LocationService
import org.chai.kevin.form.FormValidationService
import org.chai.kevin.location.CalculationLocation
import org.chai.kevin.location.DataLocation
import org.chai.kevin.security.UserType;
import org.chai.kevin.survey.summary.SummaryService
import org.chai.kevin.util.Utils
import org.hibernate.SessionFactory

class EditSurveyController extends AbstractController {

	SummaryService summaryService;
	SurveyPageService surveyPageService;
	FormValidationService formValidationService;
	SessionFactory sessionFactory;
	
	def index = {
		redirect (action: 'view', params: params)
	}

	def view = {
		// this action redirects to the current survey if a DataUser logs in
		// or to a survey summary page if an admin logs in
		if (log.isDebugEnabled()) log.debug("survey.view, params:"+params)
		def user = getUser()

		if (user.userType == UserType.SURVEY) {
			Survey dataEntry = Survey.get(params.int('survey'))

			if (dataEntry == null) {
				dataEntry = surveyPageService.getDefaultSurvey()
			}
			if (dataEntry == null) {
				log.info("no default survey - redirecting to 404")
				response.sendError(404)
			}
			else {
				redirect (controller:'editSurvey', action: 'surveyPage', params: [survey: dataEntry?.id, location: user.location.id])
			}
		}
		else {
			redirect (controller: 'surveySummary', action: 'summaryPage')
		}
	}

	def validateParameters(def location, def types) {
		def valid = true;

		if (location == null) valid = false
		else {
			if (types != null && !types.contains(location.type.code)) valid = false
		}

		if (!valid) {
			response.sendError(404)
		}
		return valid
	}

	def sectionPage = {
		if (log.isDebugEnabled()) log.debug("survey.section, params:"+params)

		DataLocation dataLocation = DataLocation.get(params.int('location'))
		SurveySection currentSection =  SurveySection.get(params.int('section'));

		if (validateParameters(dataLocation, Utils.split(currentSection?.typeCodeString))) {
			def surveyPage = surveyPageService.getSurveyPage(dataLocation,currentSection)

			render (view: '/survey/sectionPage', model: [surveyPage: surveyPage])
		}
	}

	def programPage = {
		if (log.isDebugEnabled()) log.debug("survey.program, params:"+params)

		DataLocation dataLocation = DataLocation.get(params.int('location'))
		SurveyProgram currentProgram = SurveyProgram.get(params.int('program'));

		if (validateParameters(dataLocation, Utils.split(currentProgram?.typeCodeString))) {
			def surveyPage = surveyPageService.getSurveyPage(dataLocation, currentProgram)

			render (view: '/survey/programPage', model: [surveyPage: surveyPage])
		}
	}

	def surveyPage = {
		if (log.isDebugEnabled()) log.debug("survey.survey, params:"+params)

		DataLocation dataLocation = DataLocation.get(params.int('location'))

		if (validateParameters(dataLocation, null)) {
			Survey survey = Survey.get(params.int('survey'))

			def surveyPage = surveyPageService.getSurveyPage(dataLocation, survey)

			render (view: '/survey/surveyPage', model: [surveyPage: surveyPage])
		}
	}

	def refresh = {
		if (log.isDebugEnabled()) log.debug("survey.refresh, params:"+params)

		CalculationLocation location = locationService.getCalculationLocation(params.int('location'), CalculationLocation.class)
		Survey survey = Survey.get(params.int('survey'))
		
		surveyPageService.refresh(location, survey, params.boolean('closeIfComplete')==null?false:params.boolean('closeIfComplete'));

		redirect (action: "surveyPage", params: [location: location.id, survey: survey.id])
	}

	def reopen = {
		if (log.isDebugEnabled()) log.debug("survey.submit, params:"+params)

		DataLocation dataLocation = DataLocation.get(params.int('location'))
		SurveyProgram currentProgram = SurveyProgram.get(params.int('program'));

		if (validateParameters(dataLocation, Utils.split(currentProgram?.typeCodeString))) {
			surveyPageService.reopen(dataLocation, currentProgram);

			redirect (action: "programPage", params: [location: dataLocation.id, program: currentProgram.id])
		}
	}

	def submit = {
		if (log.isDebugEnabled()) log.debug("survey.submit, params:"+params)

		DataLocation dataLocation = DataLocation.get(params.int('location'))
		SurveyProgram currentProgram = SurveyProgram.get(params.int('program'));

		if (validateParameters(dataLocation, Utils.split(currentProgram?.typeCodeString))) {
			boolean success = surveyPageService.submit(dataLocation, currentProgram);

			if (success) {
				flash.message = message(code: "survey.program.submitted", default: "Thanks for submitting.");
			}
			else {
				flash.message = message(code: "survey.program.review", default: "The survey could not be submitted, please review the sections.");
			}

			redirect (action: "programPage", params: [location: dataLocation.id, program: currentProgram.id])
		}
	}
	
	def saveValue = {
		if (log.isDebugEnabled()) log.debug("survey.saveValue, params:"+params)

		def location = DataLocation.get(params.int('location'))
		def currentSection = SurveySection.get(params.int('section'))
		def currentProgram = SurveyProgram.get(params.int('program'))
		def surveyElements = [SurveyElement.get(params.int('element'))]

		def surveyPage = surveyPageService.modify(location, currentProgram, surveyElements, params);

		if (surveyPage == null) {
			render(contentType:"text/json") { status = 'error' }
		}
		else {
			def invalidQuestionsHtml = ''
			def incompleteSectionsHtml = ''

			if (currentSection == null) {
				sessionFactory.currentSession.clear()
				location = DataLocation.get(params.int('location'))
				currentProgram = SurveyProgram.get(params.int('program'))
				
				def completeSurveyPage = surveyPageService.getSurveyPage(location, currentProgram)
				invalidQuestionsHtml = g.render(template: '/survey/invalidQuestions', model: [surveyPage: completeSurveyPage])
				incompleteSectionsHtml = g.render(template: '/survey/incompleteSections', model: [surveyPage: completeSurveyPage])
			}

			render(contentType:"text/json") {
				status = 'success'

				invalidQuestions = invalidQuestionsHtml
				incompleteSections = incompleteSectionsHtml
				programs = array {  
					surveyPage.enteredPrograms.each { program, enteredProgram -> 
						obj (
							id: program.id,
							status: enteredProgram.displayedStatus
						)
					}
				}
				sections = array {
					surveyPage.enteredSections.each { section, enteredSection ->
						sec (
							id: section.id,
							programId: section.program.id,
							invalid: enteredSection.invalid,
							complete: enteredSection.complete,
							status: enteredSection.displayedStatus
						)
					}
				}
				questions = array { 
					surveyPage.enteredQuestions.each { question, enteredQuestion ->
						ques (
							id: question.id,
							sectionId: question.section.id,
							complete: enteredQuestion.complete,
							invalid: enteredQuestion.invalid,
							skipped: enteredQuestion.skipped,
						)
					}
				}
				elements = array {
					surveyPage.elements.each { surveyElement, enteredValue ->
						elem (
							id: surveyElement.id,
							questionId: surveyElement.surveyQuestion.id,
							skipped: array {
								enteredValue.validatable.skippedPrefixes.each { prefix -> element prefix }
							},
							invalid: array {
								enteredValue.validatable.invalidPrefixes.each { invalidPrefix ->
									pre (
										prefix: invalidPrefix,
										errors: g.renderUserErrors(element: surveyElement, validatable: enteredValue.validatable, suffix: invalidPrefix, location: enteredValue.dataLocation)
									)
								}
							},
							nullPrefixes: array {
								enteredValue.validatable.nullPrefixes.each { prefix -> element prefix }
							}
						)
					}
				}

			}
		}
	}

	def save = {
		if (log.isDebugEnabled()) log.debug("survey.save, params:"+params)

		DataLocation currentLocation = DataLocation.get(params.int('location'))
		def currentSection = SurveySection.get(params.int('section'));

		if (validateParameters(currentLocation, Utils.split(currentSection?.typeCodeString))) {
			def surveyElements = getSurveyElements()
			surveyPageService.modify(currentLocation, currentSection.program, surveyElements, params);
			def sectionPage = surveyPageService.getSurveyPage(currentLocation, currentSection)

			def action
			def params = [location: sectionPage.location.id]
			if (!sectionPage.sections[currentSection].invalid) {
				if (sectionPage.isLastSection(currentSection)) {
					// we go to the next program
					action = 'programPage'
					params << [program: sectionPage.program.id]
				}
				else {
					// we get the next section
					action = 'sectionPage'
					params << [section: sectionPage.getNextSection(currentSection).id]
				}
			}
			else {
				flash.message = "survey.section.invalid";
				flash.default = "This section is invalid, please check your answers."

				action = 'sectionPage'
				params << [section: sectionPage.section.id]
			}

			redirect (action: action, params: params)
		}
	}

	def print = {
		Survey survey = Survey.get(params.int('survey'));
		DataLocation location = DataLocation.get(params.int('location'))

		SurveyPage surveyPage = surveyPageService.getSurveyPagePrint(location, survey);

		render (view: '/survey/print/surveyPrint', model:[surveyPage: surveyPage])
	}

	
	private def getSurveyElements() {
		def result = []
		// TODO test this
		params.list('surveyElements').each { id ->
			result.add(SurveyElement.get(id))
		}
		return result
	}


}
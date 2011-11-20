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

import java.util.zip.ZipOutputStream
import org.apache.shiro.SecurityUtils
import org.chai.kevin.AbstractController
import org.chai.kevin.Organisation
import org.chai.kevin.OrganisationService
import org.chai.kevin.ValueService
import org.chai.kevin.security.User
import org.chai.kevin.util.Utils
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

class EditSurveyController extends AbstractController {

	SurveyPageService surveyPageService;
	SurveyExportService surveyExportService;
	ValidationService validationService;
	ValueService valueService;

	def index = {
		redirect (action: 'view', params: params)
	}

	def view = {
		// this action redirects to the current survey if a SurveyUser logs in
		// or to a survey summary page if an admin logs in
		if (log.isDebugEnabled()) log.debug("survey.view, params:"+params)
		User user = User.findByUuid(SecurityUtils.subject.principal)

		if (user.hasProperty('organisationUnitId') != null) {
			Survey survey = Survey.get(params.int('survey'))

			if (survey == null) {
				survey = surveyPageService.getDefaultSurvey()
			}
			if (survey == null) {
				log.info("no default survey - redirecting to 404")
				response.sendError(404)
			}
			else {
				Organisation organisation = organisationService.getOrganisation(user.organisationUnitId)
	
				redirect (controller:'editSurvey', action: 'surveyPage', params: [survey: survey?.id, organisation: organisation.id])
			}
		}
		else {
			redirect (controller: 'summary', action: 'summaryPage')
		}
	}

	def validateParameters(def organisation, def groups) {
		def valid = true;

		if (organisation == null) valid = false
		else {
			int level = organisationService.loadLevel(organisation);
			if (level != organisationService.getFacilityLevel()) valid = false

			organisationService.loadGroup(organisation)
			if (groups != null && !groups.contains(organisation.organisationUnitGroup.uuid)) valid = false
		}

		if (!valid) {
			response.sendError(404)
		}
		return valid
	}

	def sectionPage = {
		if (log.isDebugEnabled()) log.debug("survey.section, params:"+params)

		// TODO make sure this is a facility
		Organisation currentOrganisation = getOrganisation(false)
		SurveySection currentSection =  SurveySection.get(params.int('section'));

		if (validateParameters(currentOrganisation, Utils.split(currentSection?.groupUuidString))) {
			def surveyPage = surveyPageService.getSurveyPage(currentOrganisation,currentSection)

			render (view: '/survey/sectionPage', model: [surveyPage: surveyPage])
		}
	}

	def objectivePage = {
		if (log.isDebugEnabled()) log.debug("survey.objective, params:"+params)

		// TODO make sure this is a facility
		Organisation currentOrganisation = getOrganisation(false)
		SurveyObjective currentObjective = SurveyObjective.get(params.int('objective'));

		if (validateParameters(currentOrganisation, Utils.split(currentObjective?.groupUuidString))) {
			def surveyPage = surveyPageService.getSurveyPage(currentOrganisation,currentObjective)

			render (view: '/survey/objectivePage', model: [surveyPage: surveyPage])
		}
	}

	def surveyPage = {
		if (log.isDebugEnabled()) log.debug("survey.survey, params:"+params)

		// TODO make sure this is a facility
		Organisation currentOrganisation = getOrganisation(false)

		if (validateParameters(currentOrganisation, null)) {
			Survey survey = Survey.get(params.int('survey'))

			def surveyPage = surveyPageService.getSurveyPage(currentOrganisation, survey)

			render (view: '/survey/surveyPage', model: [surveyPage: surveyPage])
		}
	}

	def refresh = {
		if (log.isDebugEnabled()) log.debug("survey.refresh, params:"+params)

		// TODO make sure this is a facility
		Organisation currentOrganisation = getOrganisation(false)

		Survey survey = Survey.get(params.int('survey'))
		surveyPageService.refresh(currentOrganisation, survey, params.boolean('closeIfComplete')==null?false:params.boolean('closeIfComplete'));

		redirect (action: "surveyPage", params: [organisation: currentOrganisation.id, survey: survey.id])
	}

	def reopen = {
		if (log.isDebugEnabled()) log.debug("survey.submit, params:"+params)

		Organisation currentOrganisation = getOrganisation(false)
		SurveyObjective currentObjective = SurveyObjective.get(params.int('objective'));

		if (validateParameters(currentOrganisation, Utils.split(currentObjective?.groupUuidString))) {
			surveyPageService.reopen(currentOrganisation, currentObjective);

			redirect (action: "objectivePage", params: [organisation: currentOrganisation.id, objective: currentObjective.id])
		}
	}

	def submit = {
		if (log.isDebugEnabled()) log.debug("survey.submit, params:"+params)

		Organisation currentOrganisation = getOrganisation(false)
		SurveyObjective currentObjective = SurveyObjective.get(params.int('objective'));

		if (validateParameters(currentOrganisation, Utils.split(currentObjective?.groupUuidString))) {
			boolean success = surveyPageService.submit(currentOrganisation, currentObjective);

			if (success) {
				flash.message = message(code: "survey.objective.submitted", default: "Thanks for submitting.");
			}
			else {
				flash.message = message(code: "survey.objective.review", default: "The survey could not be submitted, please review the sections.");
			}

			redirect (action: "objectivePage", params: [organisation: currentOrganisation.id, objective: currentObjective.id])
		}
	}

	def saveValue = {
		if (log.isDebugEnabled()) log.debug("survey.saveValue, params:"+params)

		Organisation currentOrganisation = getOrganisation(false)

		def currentSection = SurveySection.get(params.int('section'))
		def currentObjective = SurveyObjective.get(params.int('objective'))
		def surveyElements = [
			SurveyElement.get(params.int('element'))
		]

		def surveyPage = surveyPageService.modify(currentOrganisation, currentObjective, surveyElements, params);

		if (surveyPage == null) {
			render(contentType:"text/json") { status = 'error' }
		}
		else {
			def invalidQuestionsHtml = ''
			def incompleteSectionsHtml = ''

			if (currentSection == null) {
				def completeSurveyPage = surveyPageService.getSurveyPage(currentOrganisation, currentObjective)
				invalidQuestionsHtml = g.render(template: '/survey/invalidQuestions', model: [surveyPage: completeSurveyPage])
				incompleteSectionsHtml = g.render(template: '/survey/incompleteSections', model: [surveyPage: completeSurveyPage])
			}

			render(contentType:"text/json") {
				status = 'success'

				invalidQuestions = invalidQuestionsHtml
				incompleteSections = incompleteSectionsHtml
				objectives = array {  
					surveyPage.enteredObjectives.each { objective, enteredObjective -> 
						obj (
								id: objective.id,
								status: enteredObjective.displayedStatus
								)
					}
				}
				sections = array {
					surveyPage.enteredSections.each { section, enteredSection ->
						sec (
								id: section.id,
								objectiveId: section.objective.id,
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
									enteredValue.skippedPrefixes.each { prefix -> element prefix }
								},
								invalid: array {
									enteredValue.invalidPrefixes.each { invalidPrefix ->
										pre (
												prefix: invalidPrefix,
												valid: enteredValue.isValid(invalidPrefix),
												errors: g.renderUserErrors(element: enteredValue, suffix: invalidPrefix)
												)
									}
								}
								)
					}
				}

			}
		}
	}

	def save = {
		if (log.isDebugEnabled()) log.debug("survey.save, params:"+params)

		Organisation currentOrganisation = getOrganisation(false)
		def currentSection = SurveySection.get(params.int('section'));

		if (validateParameters(currentOrganisation, Utils.split(currentSection?.groupUuidString))) {
			def surveyElements = getSurveyElements()
			surveyPageService.modify(currentOrganisation, currentSection.objective, surveyElements, params);
			def sectionPage = surveyPageService.getSurveyPage(currentOrganisation, currentSection)

			def action
			def params = [organisation: sectionPage.organisation.id]
			if (!sectionPage.sections[currentSection].invalid) {
				if (sectionPage.isLastSection(currentSection)) {
					// we go to the next objective
					action = 'objectivePage'
					params << [objective: sectionPage.objective.id]
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
		Organisation organisation = getOrganisation(false)

		SurveyPage surveyPage = surveyPageService.getSurveyPagePrint(organisation,survey);

		render (view: '/survey/print/surveyPrint', model:[
					surveyPage: surveyPage
				])
	}

	def export = {
		Organisation organisation = getOrganisation(false)
		SurveySection section = SurveySection.get(params.int('section'))
		SurveyObjective objective = SurveyObjective.get(params.int('objective'))
		Survey survey = Survey.get(params.int('survey'))	

		File zipFile = null;
		if(section != null)
			zipFile = surveyExportService.getSurveyExportZipFile(organisation, section, null, null);
		else if(objective != null)
			zipFile = surveyExportService.getSurveyExportZipFile(organisation, null, objective, null);
		else if(survey != null)
			zipFile = surveyExportService.getSurveyExportZipFile(organisation, null, null, survey);						
			
		if(zipFile.exists()){
			response.setHeader("Content-disposition", "attachment; filename=" + zipFile.getName());
			render(contentType: "application/zip");						
		}
		
		redirect(action: 'summaryPage', params: params);
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
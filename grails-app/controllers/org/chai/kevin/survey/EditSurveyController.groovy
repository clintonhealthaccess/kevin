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

import grails.validation.ValidationException;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion.QuestionStatus;
import java.util.List;
import org.apache.shiro.SecurityUtils;
import org.chai.kevin.AbstractReportController;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.ValueService;
import org.hisp.dhis.period.Period;
import org.chai.kevin.security.User;
import org.chai.kevin.survey.SurveyPageService;
import org.chai.kevin.survey.validation.SurveyEnteredObjective.ObjectiveStatus;
import org.chai.kevin.survey.validation.SurveyEnteredSection.SectionStatus;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.codehaus.groovy.grails.web.servlet.FlashScope;
import org.chai.kevin.util.Utils

class EditSurveyController extends AbstractReportController {
	
	SurveyPageService surveyPageService;
	ValidationService validationService;
	ValueService valueService;
	SurveyElementService surveyElementService;
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		// this action redirects to the current survey if a SurveyUser logs in
		// or to a survey summary page if an admin logs in
		if (log.isDebugEnabled()) log.debug("survey.view, params:"+params)
		User user = User.findByUsername(SecurityUtils.subject.principal)
		
		if (user.hasProperty('organisationUnit') != null) {
			Survey survey = Survey.get(params.int('survey'))
			Organisation organisation = organisationService.getOrganisation(user.organisationUnit.id)
			
			redirect (action: 'surveyPage', params: [survey: survey.id, organisation: organisation.id])
		}
		else {
			redirect (action: 'summaryPage')
		}
	}
	
	def validateParameters(def organisation, def groups) {
		def valid = true;
		
		if (organisation == null) valid = false
		
		int level = organisationService.getLevel(organisation);
		if (level != organisationService.getFacilityLevel()) valid = false

		organisationService.loadGroup(organisation)
		if (groups != null && !groups.contains(organisation.organisationUnitGroup.uuid)) valid = false
		
		if (!valid) {
			response.sendError(404)
		}
		return valid
	}
	
	def summaryPage = {
		Organisation currentOrganisation = getOrganisation(false)
		Survey currentSurvey = Survey.get(params.int('survey'))
		
		SummaryPage summaryPage = surveyPageService.getSummaryPage(currentOrganisation, currentSurvey);
		
		Integer organisationLevel = ConfigurationHolder.config.facility.level;
		def organisationTree = organisationService.getOrganisationTreeUntilLevel(organisationLevel)

//		if (currentSurvey !=null && currentOrganisation != null) {
//			redirect (action: 'surveyPage', params: [organisation: currentOrganisation.id, survey: currentSurvey.id])
//		}
			
		render (view: '/survey/summaryPage', model: [
			summaryPage: summaryPage,
			surveys: Survey.list(),
			organisationTree: organisationTree
		])
	}

	def objectiveTable = {
		Organisation currentOrganisation = getOrganisation(false)
		Survey currentSurvey = Survey.get(params.int('survey'))
		
		SummaryPage summaryPage = surveyPageService.getObjectiveTable(currentOrganisation, currentSurvey)
		
		render (view: '/survey/objectiveTable', model: [
			summaryPage: summaryPage
		])
	}
	
	def sectionTable = {
		Organisation currentOrganisation = getOrganisation(false)
		SurveyObjective currentObjective = SurveyObjective.get(params.int('objective'))
		
		SummaryPage summaryPage = surveyPageService.getSectionTable(currentOrganisation, currentObjective)
		
		render (view: '/survey/sectionTable', model: [
			summaryPage: summaryPage
		])
	}
	
	def sectionPage = {
		if (log.isDebugEnabled()) log.debug("survey.section, params:"+params)
		
		// TODO make sure this is a facility
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))
		SurveySection currentSection =  SurveySection.get(params.int('section'));
		
		if (validateParameters(currentOrganisation, Utils.split(currentSection.groupUuidString))) {
			def surveyPage = surveyPageService.getSurveyPage(currentOrganisation,currentSection)
				
//			if (surveyPage.firstTimeOpened(currentSection)) surveyPage.persistState(surveyElementService)
			
			render (view: '/survey/sectionPage', model: [surveyPage: surveyPage])
		}
	}
	
	def objectivePage = {
		if (log.isDebugEnabled()) log.debug("survey.objective, params:"+params)
		
		// TODO make sure this is a facility
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))
		SurveyObjective currentObjective = SurveyObjective.get(params.int('objective'));
		
		if (validateParameters(currentOrganisation, Utils.split(currentObjective.groupUuidString))) {
			def surveyPage = surveyPageService.getSurveyPage(currentOrganisation,currentObjective)
			
//			currentObjective.getSections(currentOrganisation.organisationUnitGroup).each { section ->
//				if (surveyPage.firstTimeOpened(section)) surveyPage.persistState(surveyElementService)
//			}
			
			render (view: '/survey/objectivePage', model: [surveyPage: surveyPage])
		}
	}
	
	
	def surveyPage = {
		if (log.isDebugEnabled()) log.debug("survey.survey, params:"+params)
		
		// TODO make sure this is a facility
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))
		
		if (validateParameters(currentOrganisation, null)) {
			Survey survey = Survey.get(params.int('survey'))
			
			def surveyPage = surveyPageService.getSurveyPage(currentOrganisation, survey)
			
			render (view: '/survey/surveyPage', model: [surveyPage: surveyPage])
		}
	}
	
	def reopen = {
		if (log.isDebugEnabled()) log.debug("survey.submit, params:"+params)
		
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))
		SurveyObjective currentObjective = SurveyObjective.get(params.int('objective'));
		
		if (validateParameters(currentOrganisation, Utils.split(currentObjective.groupUuidString))) {
			def surveyPage = surveyPageService.getSurveyPage(currentOrganisation, currentObjective);
			
			if (surveyPage.canReopen()) {
				surveyPage.reopen(surveyElementService)
				redirect (action: "objectivePage", params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.id])
			}
		}
	}
	
	def submit = {
		if (log.isDebugEnabled()) log.debug("survey.submit, params:"+params)
		
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))
		SurveyObjective currentObjective = SurveyObjective.get(params.int('objective'));
		
		if (validateParameters(currentOrganisation, Utils.split(currentObjective.groupUuidString))) {
			def surveyPage = surveyPageService.getSurveyPage(currentOrganisation, currentObjective);
			surveyPage.userValidation(validationService, surveyElementService);
			
			if (surveyPage.canSubmit()) {
				surveyPage.submit(surveyElementService, valueService)
				flash.message = "survey.objective.submitted";
				flash.default = "Thanks for submitting";
			}
			else {
				flash.message = "survey.objective.review";
				flash.default = "The survey is not yet complete, please review all the sections."
			}
			
			redirect (action: "objectivePage", params: [organisation: surveyPage.organisation.id, objective: surveyPage.objective.id])
		}
	}
	
	def saveValue = {
		if (log.isDebugEnabled()) log.debug("survey.saveValue, params:"+params)
		
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))
		
		def surveyQuestion = surveyElementService.getSurveyQuestion(Long.parseLong(params['question']))
		def surveyElements = [SurveyElement.get(params.int('element'))]
		def surveyPage = surveyPageService.modify(currentOrganisation, surveyElements, params);
		
		def questionPage = surveyPageService.getSurveyPage(currentOrganisation, surveyQuestion)	
		
		render(contentType:"text/json") {
			result = "success"
			
			question (
				id: surveyQuestion.id,
				status: questionPage.questions[surveyQuestion].status.name(),
				html: g.render(template:'/survey/question', model: [surveyPage: questionPage, question: surveyQuestion])
			)
			
			objectives = array {  
				surveyPage.objectives.each { objective, enteredObjective -> 
					obj (
						id: objective.id,
						status: enteredObjective.status.name()
					)
				}
			}
			sections = array {
				surveyPage.sections.each { section, enteredSection ->
					sec (
						id: section.id,
						status: enteredSection.status.name(),
					)
				}	
			}
			questions = array { 
				surveyPage.questions.each { question, enteredQuestion ->
					ques (
						id: question.id,
						status: enteredQuestion.status.name(),
						skipped: enteredQuestion.skipped,
						html: enteredQuestion.status==QuestionStatus.INVALID
							?g.render(template:'/survey/question', model: [
								surveyPage: surveyPageService.getSurveyPage(currentOrganisation, question), 
								question: question
							]):''
					)
				}	
			}
			elements = array { 
				surveyPage.elements.each { surveyElement, enteredValue ->
					elem (
						id: surveyElement.id,
						skipped: array {
							enteredValue.skippedPrefixes.each { prefix ->
								element prefix
							}
						}
//						invalid: array {
//							enteredValue.invalidPrefixes.each { prefix
//								element prefix
//							}
//						}
					)
				}
			}
			
//			status = statusString
//			html = g.render(template:'/survey/question', model: [surveyPage: surveyPage, question: surveyQuestion])
//			objective (
//				id: surveyPage.objective.id,
//				status: surveyPage.getStatus(surveyPage.objective).name()
//			)
//			sections = array {
//				surveyPage.objective.getSections(surveyPage.organisation.organisationUnitGroup).each { section ->
//					sec (
//						id: section.id,
//						name: g.i18n(field: section.names),
//						status: surveyPage.getStatus(section)?.name()
//					)
//				}
//			}
//			invalidQuestions = array {
//				invalidSectionMap[surveyPage.section].each{ question ->
//					quest (
//						id: question.id
////								html: g.render(template:'/survey/question', model: [surveyPage: surveyPage, question: question])
//					)
//				}
//			}
//			skippedElements = array {
//				surveyPage.skippedElements.each{ elem ->
//					element elem.id
//				}
//			}
//			skippedQuestions = array {
//				surveyPage.skippedQuestions.each{ question ->
//					element question.id
//				}
//			}
//			invalidSectionsHtml = invalidSectionsHtmlString
		}
		
	}
	
	def save = {
		if (log.isDebugEnabled()) log.debug("survey.save, params:"+params)
		
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))
		SurveySection currentSection = SurveySection.get(params.int('section'));
		
		if (validateParameters(currentOrganisation, Utils.split(currentSection.groupUuidString))) {
			def surveyElements = getSurveyElements()
			
			def surveyPage = surveyPageService.getSurveyPage(currentOrganisation, currentSection, surveyElements);
			saveSurvey(surveyPage, surveyElements)
			
			def action
			def params = [organisation: surveyPage.organisation.id]
			if (surveyPage.getStatus(surveyPage.section) != SectionStatus.INVALID) {
				if (surveyPage.isLastSection()) {
					// we go to the next objective
					action = 'objectivePage'
					params << [objective: surveyPage.objective.id]
				}
				else {
					// we get the next section
					def sections = surveyPage.objective.getSections(surveyPage.organisation.organisationUnitGroup)
					def index = sections.indexOf(surveyPage.section)
					action = 'sectionPage'
					params << [section: sections[index+1].id]
				}
			}
			else {
				action = 'sectionPage'
				params << [section: surveyPage.section.id]
			}
			
			redirect (action: action, params: params)
		}
	}
	
	
	private def getSurveyElements() {
		def result = []
		params.surveyElements.each { id ->
			result.add(SurveyElement.get(id))
		}
		return result
	}
	

}
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
package org.chai.kevin.survey;
/**
 * @author JeanKahigiso
 *
 */

import java.util.List;
import org.chai.kevin.AbstractReportController;
import org.chai.kevin.Organisation;
import org.chai.kevin.ValueService;
import org.hisp.dhis.period.Period;
import org.chai.kevin.survey.SurveyPage.SectionStatus;
import org.chai.kevin.survey.SurveyPageService;
import org.chai.kevin.survey.validation.SurveyEnteredObjective.ObjectiveStatus;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.codehaus.groovy.grails.web.servlet.FlashScope;

class SurveyController extends AbstractReportController {
	
	SurveyPageService surveyPageService;
	ValidationService validationService;
	ValueService valueService;
	SurveyElementService surveyElementService;
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def sectionPage = {
		if (log.isDebugEnabled()) log.debug("survey.section, params:"+params)
		
		Organisation currentOrganisation = getOrganisation(false)
		SurveySection currentSection = getSurveySection()
		
		def surveyPage = surveyPageService.getSurveyPage(currentOrganisation,currentSection)
		surveyPage.createEnteredValues(surveyElementService)
		surveyPage.userValidation(validationService, surveyElementService)
			
		return [surveyPage: surveyPage]
	}
	
	def objectivePage = {
		if (log.isDebugEnabled()) log.debug("survey.objective, params:"+params)
		
		Organisation currentOrganisation = getOrganisation(false)
		SurveyObjective currentObjective = getSurveyObjective()
		
		def surveyPage = surveyPageService.getSurveyPage(currentOrganisation,currentObjective)
		surveyPage.createEnteredValues(surveyElementService)
		surveyPage.userValidation(validationService, surveyElementService)
		
		return [surveyPage: surveyPage]
	}
	
	def surveyPage = {
		if (log.isDebugEnabled()) log.debug("survey.survey, params:"+params)
		
		Organisation currentOrganisation = getOrganisation(false)
		Survey survey = getSurvey()
		
		def surveyPage = surveyPageService.getSurveyPage(currentOrganisation, survey)
		
		return [surveyPage: surveyPage]
	}
	
	def submit = {
		if (log.isDebugEnabled()) log.debug("survey.submit, params:"+params)
		
		Organisation currentOrganisation = getOrganisation(false)
		SurveyObjective currentObjective = getSurveyObjective()
		
		def surveyPage = surveyPageService.getSurveyPage(currentOrganisation, currentObjective);
		surveyPage.createEnteredValues(surveyElementService)
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
	
	def saveValue = {
		if (log.isDebugEnabled()) log.debug("survey.saveValue, params:"+params)
		
		def surveyPage = getSurveyPageFromParams()
		saveSurvey(surveyPage)
		
		def statusString;
		def surveyQuestion = getSurveyQuestion()
		if (!surveyPage.isValid(surveyQuestion)) statusString = 'invalid'
		else statusString = 'valid'
		
		def invalidSectionsHtmlString = ''
		def invalidSectionMap = surveyPage.getInvalidQuestions()
		if (surveyPage.section == null) invalidSectionsHtmlString = g.render(template:'invalidSections', model: [invalidSectionMap: invalidSectionMap, surveyPage: surveyPage]) 
		
		render(contentType:"text/json") {
			result = 'success'
			status = statusString
			html = g.render(template:'question', model: [surveyPage: surveyPage, question: surveyElement.surveyQuestion])
			objective (
				id: surveyPage.objective.id,
				status: surveyPage.getStatus(surveyPage.objective).name()
			)
			sections = array {
				surveyPage.objective.getSections(surveyPage.organisation.organisationUnitGroup).each { section ->
					sec (
						id: section.id,
						name: g.i18n(field: section.names),
						status: surveyPage.getStatus(section).name()
					)
				}
			}
			invalidQuestions = array {
				invalidSectionMap[surveyPage.section].each{ question ->
					quest (
						id: question.id,
						html: g.render(template:'question', model: [surveyPage: surveyPage, question: question])
					)
				}
			}
			skippedElements = array {
				surveyPage.skippedElements.each{ elem ->
					element elem.id
				}
			}
			skippedQuestions = array {
				surveyPage.skippedQuestions.each{ question ->
					element question.id
				}
			}
			invalidSectionsHtml = invalidSectionsHtmlString
		}
	}
	
	def save = {
		if (log.isDebugEnabled()) log.debug("survey.save, params:"+params)
		
		def surveyPage = getSurveyPageFromParams()
		saveSurvey(surveyPage)

		def action = surveyPage.section == null?'objectivePage':'sectionPage'
		def params = [organisation: surveyPage.organisation.id]
		if (surveyPage.section == null) params << [objective: surveyPage.objective.id]
		else params << [section: surveyPage.section.id]
		
		redirect (action: action, params: params)
	}
	
	private def getSurveyPageFromParams() {
		def surveyPage;

		Organisation currentOrganisation = getOrganisation(false)
		def surveyElements = getSurveyElements()
		
		def currentObjective;
		SurveySection currentSection = getSurveySection()
		if (currentSection == null) {
			currentObjective = getSurveyObjective()
			surveyPage = surveyPageService.getSurveyPage(currentOrganisation, currentObjective, surveyElements);
		}
		else {
			surveyPage = surveyPageService.getSurveyPage(currentOrganisation, currentSection, surveyElements);
		}
					
		if (log.isDebugEnabled()) log.debug("survey page: "+surveyPage)
		
		return surveyPage
	}
	
	private def getSurveyElements() {
		def result = []
		log.debug(params.surveyElements)
		params.surveyElements.each { id ->
			result.add(SurveyElement.get(id))
		}
		return result
	}
	
	private def saveSurvey(def surveyPage) {
		bindData(surveyPage, params)
		surveyPage.createEnteredValues(surveyElementService)
		surveyPage.transferValues(validationService, surveyElementService)
		surveyPage.userValidation(validationService, surveyElementService)
		surveyPage.persistState(surveyElementService)
	}

}
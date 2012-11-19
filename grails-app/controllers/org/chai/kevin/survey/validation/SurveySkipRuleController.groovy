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
package org.chai.kevin.survey.validation

import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.AbstractEntityController
import org.chai.kevin.form.FormElement;
import org.chai.kevin.survey.Survey
import org.chai.kevin.survey.SurveyElement
import org.chai.kevin.survey.SurveyQuestion
import org.chai.kevin.survey.SurveySkipRule
import org.codehaus.groovy.grails.commons.ConfigurationHolder
/**
 * @author Jean Kahigiso M.
 *
 */
class SurveySkipRuleController  extends AbstractEntityController {

	def surveyService
	def languageService

	def getLabel() {
		return 'survey.skiprule.label'
	}
	
	def getEntity(def id) {
		return SurveySkipRule.get(id)
	}
	def createEntity() {
		return new SurveySkipRule()
	}

	def getTemplate() {
		return "/survey/admin/createSkipRule";
	}

	def getModel(def entity) {
		def skippedSurveyQuestions = new ArrayList(entity.skippedSurveyQuestions)
		[
			skip: entity,
			skippedSurveyQuestions: skippedSurveyQuestions,
			languageService: languageService
		]
	}

	def getEntityClass(){
		//TODO return SurveySkipRule.class;
		return null;
	}
	
	def bindParams(def entity) {
		entity.properties = params

		// binding skipped elements
		entity.skippedFormElements.clear()
		int i = 0;
		params.skipped?.element?.each { skipped ->
			def element = FormElement.get(skipped)
			if (element != null) {
				def prefix = params.skipped.prefix[i]
				entity.skippedFormElements.put(element, prefix)
			}
			i++;
		}
				
		// we do this because automatic data binding does not work with polymorphic elements		
		List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();
		params.list('skippedSurveyQuestions').each { id -> 
			if (NumberUtils.isDigits(id)) {
				def question = surveyService.getSurveyQuestion(Long.parseLong(id))
				if (question != null) questions.add(question);
			}
		}
		entity.skippedSurveyQuestions = questions
	}
	
	def list = {
		adaptParamsForList()
		
		Survey survey = Survey.get(params.int('survey.id'))
		if (survey == null) {
			response.sendError(404)
		}
		else {
			def skipRules = SurveySkipRule.createCriteria().list(params) {eq ('survey', survey)}
			
			render(view: '/entity/list', model:[
				template: "survey/skipRuleList",
				entities: skipRules,
				entityCount: skipRules.totalCount,
				code: getLabel(),
				entityClass: getEntityClass()
			])
		}
	}

}

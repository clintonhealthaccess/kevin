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
package org.chai.kevin.survey

import org.chai.kevin.AbstractController;
import org.chai.kevin.util.Utils
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * @author Jean Kahigiso M.
 *
 */

class QuestionController extends AbstractController {

	def surveyService
	
	def index = {
		redirect (action: "list", params: params)
	}
	
	def search = {
		adaptParamsForList()
		
		Survey survey = Survey.get(params.int('survey'))
		List<SurveyQuestion> questions = surveyService.searchSurveyQuestions(params['q'], survey, params);
		
		render (view: '/survey/admin/list', model:[
			template:"questionList",
			survey: survey,
			entities: questions,
			entityCount: surveyService.countSurveyQuestions(params['q'], survey),
			code: 'survey.question.label',
			search: true
		])
	}
	
	def exportEntity(){
		return SurveyQuestion.class;
	}
	
	def list = {
		adaptParamsForList()
		
		SurveySection section = SurveySection.get(params.int('section.id'))
		if (section == null) {
			response.sendError(404)
		}
		else {
			List<SurveyQuestion> questions = section.questions;
			Collections.sort(questions)
			
			def max = Math.min(params['offset']+params['max'], questions.size())
			
			render (view: '/survey/admin/list', model:[
				template:"questionList",
				survey: section.program.survey,
				program: section.program,
				section: section,
				entities: questions.subList(params['offset'], max),
				entityCount: questions.size(),
				code: 'survey.question.label',
				addTemplate: '/survey/admin/addQuestion',
				entityClass: exportEntity()
			])
		}
	}
	
	def getAjaxData = {
		Survey survey = Survey.get(params.int('survey'));
		Set<SurveyQuestion> surveyQuestions = surveyService.searchSurveyQuestions(params['term'], survey);

		render(contentType:"text/json") {
			elements = array {
				surveyQuestions.each { question ->
					quest (
						key: question.id,
						value: Utils.stripHtml(g.i18n(field: question.names).toString(), 35)+' - '+ g.i18n(field: question.section.names)
					)
				}
			}
		}
	}
}

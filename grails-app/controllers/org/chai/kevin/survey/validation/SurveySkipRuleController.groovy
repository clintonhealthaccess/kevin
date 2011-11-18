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

import org.chai.kevin.AbstractEntityController
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

	def getLabel() {
		return 'survey.skiprule.label'
	}
	
	def getEntity(def id) {
		return SurveySkipRule.get(id)
	}
	def createEntity() {
		def entity = new SurveySkipRule()
		//FIXME find a better to do this
		if (!params['survey.id']) entity.survey = Survey.get(params.surveyId);
		return entity;
	}

	def getTemplate() {
		return "/survey/admin/createSkipRule";
	}

	def getModel(def entity) {
		[ skip: entity ]
	}

	def bindParams(def entity) {
		entity.properties = params
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.descriptions!=null) entity.descriptions = params.descriptions
		// binding skipped elements
		entity.skippedSurveyElements.clear()
		int i = 0;
		params.skipped?.element?.each { skipped ->
			def element = SurveyElement.get(skipped)
			if (element != null) {
				def prefix = params.skipped.prefix[i]
				entity.skippedSurveyElements.put(element, prefix)
			}
			i++;
		}
						
		List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();
		params.list('skippedSurveyQuestions').each { id -> 
			def question = surveyService.getSurveyQuestion(Long.parseLong(id))
			if (question != null) questions.add(question);
		}
		entity.skippedSurveyQuestions = questions
	}
	
	def list = {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		Survey survey = Survey.get(params.surveyId)
		Set<SurveySkipRule> skipRules = survey.skipRules;

		render(view: '/survey/admin/list', model:[
			template: "skipRuleList",
			entities: skipRules,
			entityCount: skipRules.size(),
			code: getLabel()
		])
	}

}

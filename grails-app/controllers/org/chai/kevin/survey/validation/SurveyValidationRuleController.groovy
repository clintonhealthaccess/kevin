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

import org.chai.kevin.AbstractController;
import org.chai.kevin.AbstractEntityController
import org.chai.kevin.form.FormCloner;
import org.chai.kevin.form.FormValidationRule;
import org.chai.location.DataLocationType;
import org.chai.kevin.survey.Survey
import org.chai.kevin.survey.SurveyElement
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * @author Jean Kahigiso M.
 *
 */
class SurveyValidationRuleController extends AbstractController {

	def surveyService
	
	def getEntityClass(){
		//TODO return FormValidationRule.class;
		return null;
	}
	
	def list = {
		adaptParamsForList()

		SurveyElement surveyElement = SurveyElement.get(params.int('formElement.id'))
		Survey survey = Survey.get(params.int('survey.id'))
		
		if (surveyElement == null && survey == null) {
			response.sendError(404)
		}
		else {
			List<FormValidationRule> validationRules = new ArrayList<FormValidationRule>();
			if (surveyElement != null) {		
				surveyElement = SurveyElement.get(params.int('formElement.id'))
				validationRules.addAll(surveyElement.getValidationRules());
			}
			else {
				Set<SurveyElement> surveyElements = surveyService.getSurveyElements(null, survey)
				surveyElements.each { element ->
					validationRules.addAll(element.getValidationRules())	
				}
			}
			validationRules.sort {it.id}
	
			def max = Math.min(params['offset']+params['max'], validationRules.size())
			
			render (view: '/entity/list', model:[
				template: "form/validationRuleList",
				entities: validationRules.subList(params['offset'], max),
				entityCount: validationRules.size(),
				code: 'formelement.validationrule.label'
			])
		}
	}
	
}

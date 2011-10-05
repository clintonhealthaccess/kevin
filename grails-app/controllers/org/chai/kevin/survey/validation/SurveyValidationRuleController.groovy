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

import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.survey.SurveyElementService;
import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.survey.SurveyValidationRule;
import org.chai.kevin.util.Utils;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

/**
 * @author Jean Kahigiso M.
 *
 */
class SurveyValidationRuleController extends AbstractEntityController {

	def organisationService
	
	def getEntity(def id) {
		return SurveyValidationRule.get(id)
	}
	
	def createEntity() {
		def entity = new SurveyValidationRule()
		//FIXME find a better to do this
		if (!params['surveyElement.id']) entity.surveyElement = SurveyElement.get(params.int('elementId'));
		return entity;
	}

	def getTemplate() {
		return "/survey/admin/createValidationRule";
	}

	def getModel(def entity) {
		[
			validation: entity,
			groupUuids: Utils.split(entity.groupUuidString),
			groups: organisationService.getGroupsForExpression()
		]
	}

	def validateEntity(def entity) {
		return entity.validate()
	}

	def saveEntity(def entity) {
		entity.save()
	}
	def deleteEntity(def entity) {
		entity.surveyElement.validationRules.remove(entity);
		entity.surveyElement.save();
		entity.delete()
	}

	def bindParams(def entity) {
		entity.properties = params
		
		entity.groupUuidString =  params['groupUuids']!=null?Utils.unsplit(params['groupUuids']):''
	}
	
	def list = {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		params.offset = params.offset ? params.int('offset'): 0
		SurveyElement surveyElement = SurveyElement.get(params.int('elementId'))
		List<SurveyValidationRule> validationRules = surveyElement.getValidationRules();

		def max = Math.min(params['offset']+params['max'], validationRules.size())
		
		render (view: '/survey/admin/list', model:[
			template:"validationRuleList",
			surveyElement: surveyElement,
			entities: validationRules.subList(params['offset'], max),
			entityCount: validationRules.size(),
			code: 'survey.validationrule.label'
		])
	}

}

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
import org.chai.kevin.survey.SurveyValidationMessage;
import org.chai.kevin.survey.ValidationMessageService;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

/**
 * @author Jean Kahigiso M.
 *
 */
class ValidationMessageController extends AbstractEntityController {
	
	ValidationMessageService validationMessageService;
	
	def getEntity(def id) {
		return SurveyValidationMessage.get(id)
	}
	def createEntity() {
		return new SurveyValidationMessage();
	}

	def getTemplate() {
		return "/survey/admin/createValidationMessage"
	}

	def getModel(def entity) {
		[message:entity]
	}

	def validateEntity(def entity) {
		return entity.validate()
	}

	def saveEntity(def entity) {
		entity.save()
	}
	def deleteEntity(def entity) {
		if(entity.validationRules.isEmpty())
			entity.delete()
	}

	def bindParams(def entity) {
		entity.properties = params
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.messages!=null) entity.messages = params.messages
	}
	
	def list = {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		List<SurveyValidationMessage> validationMessages = SurveyValidationMessage.list(params);

		render (view: '/survey/admin/list', model:[
			template:"validationMessageList",
			entities: validationMessages,
			entityCount: SurveyValidationMessage.count(),
			code: 'survey.validationmessage.label'
		])
	}

	def getAjaxData={
		def validationMessages = validationMessageService.getSearchValidationMessage(params['term']);
		
		render(contentType:"text/json") {
						
			messages = array {
				validationMessages.each { message ->
					mess (
						id: message.id,
						message: g.i18n(field: message.messages)
					)
				}
			}
			
		}
	}
	
}

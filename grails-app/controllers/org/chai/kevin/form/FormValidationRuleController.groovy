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
package org.chai.kevin.form

import org.chai.kevin.AbstractEntityController
import org.chai.location.DataLocationType

/**
 * @author Jean Kahigiso M.
 *
 */
class FormValidationRuleController extends AbstractEntityController {

	def locationService
	def languageService
	def formElementService
	
	def getLabel() {
		return 'formelement.validationrule.label'
	}
	
	def getEntity(def id) {
		return FormValidationRule.get(id)
	}
	
	def createEntity() {
		return new FormValidationRule()
	}

	def getTemplate() {
		return "/entity/form/createValidationRule";
	}

	def getModel(def entity) {
		def formElements = []
		if (entity.formElement != null) formElements << formElementService.getFormElement(entity.formElement.id)

		def dependencies = new ArrayList(entity.dependencies)		
		[
			dependencies: dependencies,
			formElements: formElements,
			validation: entity,
			types: DataLocationType.list([cache: true]),
			languageService: languageService
		]
	}

	def getEntityClass(){
		//TODO return FormValidationRule.class;
		return null;
	}
	
	def saveEntity(def entity) {
		entity.formElement.validationRules.add(entity)
		entity.save()
		entity.formElement.save()
	}
	
	def deleteEntity(def entity) {
		entity.formElement.validationRules.remove(entity);
		entity.formElement.save();
		entity.delete()
	}

	def bindParams(def entity) {
		entity.properties = params
	}
	
	def copy = {
		def rule = FormValidationRule.get(params.int('id'))
		
		FormCloner cloner = new FormCloner() {};
		FormValidationRule copy = new FormValidationRule();
		rule.deepCopy(copy, cloner);
		copy.save(flush: true)
		
		redirect (uri:getTargetURI())
	}
	
	def list = {
		adaptParamsForList()

		def formElement = formElementService.getFormElement(params.int('formElement.id'))
		if (formElement == null) {
			response.sendError(404)
		}
		else {
			def validationRules = formElement.validationRules.sort {it.id}
			def max = Math.min(params['offset']+params['max'], validationRules.size())
			
			render (view: '/entity/list', model:[
				template:"form/validationRuleList",
				entities: validationRules.subList(params['offset'], max),
				entityCount: validationRules.size(),
				code: getLabel(),
				entityClass: getEntityClass()
			])
		}
	}
}

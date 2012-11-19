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
package org.chai.kevin.planning

import org.chai.kevin.AbstractEntityController
import org.chai.kevin.form.FormElement
/**
 * @author Jean Kahigiso M.
 *
 */
class PlanningSkipRuleController extends AbstractEntityController {

	def languageService
	
	def getLabel() {
		return 'planning.skiprule.label'
	}
	
	def getEntity(def id) {
		return PlanningSkipRule.get(id)
	}
	def createEntity() {
		return new PlanningSkipRule()
	}

	def getTemplate() {
		return "/planning/admin/createSkipRule";
	}

	def getModel(def entity) {
		[skip: entity, languageService: languageService]
	}

	def getEntityClass(){
		//TODO return PlanningSkipRule.class;
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
	}
	
	def list = {
		adaptParamsForList()
		
		Planning planning = Planning.get(params.int('planning.id'))
		if (planning == null) {
			response.sendError(404)
		}
		else {
			def skipRules = PlanningSkipRule.createCriteria().list(params) {eq ('planning', planning)}
			
			render(view: '/planning/admin/list', model:[
				template: "skipRuleList",
				entities: skipRules,
				entityCount: skipRules.totalCount,
				code: getLabel(),
				entityClass: getEntityClass()
			])
		}
	}

}

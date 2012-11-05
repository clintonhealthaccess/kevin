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
import org.chai.kevin.Period
import org.chai.kevin.data.DataElement
import org.chai.location.DataLocationType
/**
 * @author Jean Kahigiso M.
 *
 */
class PlanningOutputController extends AbstractEntityController {
	
	def dataService
	
	def getEntity(def id) {
		return PlanningOutput.get(id)
	}

	def createEntity() {
		return new PlanningOutput()
	}

	def getLabel() {
		return 'planning.planningoutput.label'
	}
	
	def getTemplate() {
		return "/planning/admin/createPlanningOutput"
	}

	def getModel(def entity) {
		def dataElements = []
		if (entity.dataElement != null) dataElements << entity.dataElement
		
		def valuePrefixes = []
		if (entity.dataElement != null) valuePrefixes.addAll entity.dataElement.getValuePrefixes("")
		[
			planningOutput: entity,
			dataElements: dataElements,
			valuePrefixes: valuePrefixes
		]
	}

	def getEntityClass(){
		//TODO return Planning.class;
		return null;
	}
	
	def bindParams(def entity) {
		bindData(entity, params, [exclude:'dataElement.id'])
		if (params.int('dataElement.id')) entity.dataElement = dataService.getData(params.int('dataElement.id'), DataElement.class)
	}

	def list = {
		adaptParamsForList()
		
		Planning planning = Planning.get(params.int('planning.id'))
		if (planning == null) response.sendError(404)
		else {
			def planningOutputs = planning.planningOutputs
			
			render (view: '/planning/admin/list', model:[
				template:"planningOutputList",
				entities: planningOutputs as List,
				entityCount: planningOutputs.size(),
				code: getLabel(),
				entityClass: getEntityClass()
			])
		}
	}
	
}
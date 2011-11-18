package org.chai.kevin.data

/*
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

import org.chai.kevin.AbstractEntityController
import org.chai.kevin.DataService
import org.chai.kevin.ValueService
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class NormalizedDataElementController extends AbstractEntityController {

	DataService dataService
	ValueService valueService
	
	def getEntity(def id) {
		return NormalizedDataElement.get(id)
	}
	
	def createEntity() {
		def normalizedDataElement = new NormalizedDataElement()
		normalizedDataElement.type = new Type()
		return normalizedDataElement
	}

	def getLabel() {
		return "normalizeddataelement.label";
	}
		
	def getTemplate() {
		return "/entity/normalizedDataElement/createNormalizedDataElement";
	}
	
	def getModel(def entity) {
		return [normalizedDataElement: entity]
	}

	def saveEntity(def entity) {
		entity.setTimestamp(new Date());
		entity.save()
	}
	
	def deleteEntity(def entity) {
		// we check if there are calculations
		
		// TODO check this
		if (dataService.getCalculations(entity).isEmpty()) { 
			// we delete all the values
			valueService.deleteValues(entity)
			entity.delete()
		}
		else {
			flash.message = message(code: "normalizeddataelement.delete.hasvalues", default: "Could not delete normalized data element, it still has associated calculations");
		}
	}
	
	def bindParams(def entity) {
		entity.properties = params
		
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = params.descriptions
		
		// TODO bind expressions
	}
	
	def search = {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		params.offset = params.offset ? params.int('offset'): 0
		
		List<NormalizedDataElement> normalizedDataElements = dataService.searchData(NormalizedDataElement.class, params['q'], [], params);
		
		render (view: '/entity/list', model:[
			entities: normalizedDataElements,
			entityCount: dataService.countData(NormalizedDataElement.class, params['q'], []),
			template: "normalizedDataElement/normalizedDataElementList",
			code: getLabel(),
			search: true
		])
	}
	def list = {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		params.offset = params.offset ? params.int('offset'): 0
		
		List<NormalizedDataElement> normalizedDataElements = NormalizedDataElement.list(params);
		
		render (view: '/entity/list' , model:[
			entities: normalizedDataElements, 
			entityCount: NormalizedDataElement.count(),
			template: 'normalizedDataElement/normalizedDataElementList',
			code: getLabel()
		])
	}
	
	def getDescription = {
		def normalizedDataElement = NormalizedDataElement.get(params.int('id'))
		
		if (normalizedDataElement == null) {
			render(contentType:"text/json") {
				result = 'error'
			}
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/entity/normalizedDataElement/normalizedDataElementDescription', model: [normalizedDataElement: normalizedDataElement])
			}
		}
	}
	
}

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

import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.AbstractEntityController
import org.chai.kevin.DataService
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Expression;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class ExpressionController extends AbstractEntityController {

	DataService dataService
	
	def getEntity(def id) {
		return Expression.get(id)
	}
	
	def createEntity() {
		return new Expression()
	}
	
	def getTemplate() {
		return "/entity/expression/createExpression";
	}
	
	def getModel(def entity) {
		return [expression: entity, /*dataSets: dataService.getDataSets()*/]
	}

	def validateEntity(def entity) {
		return entity.validate()
	}
	
	def saveEntity(def entity) {
		entity.setTimestamp(new Date());
		entity.save()
	}
	
	def deleteEntity(def entity) {
		entity.delete()
	}
	
	def bindParams(def entity) {
		entity.properties = params
		
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = params.descriptions
	}
	
	def list = {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		render (view: '/entity/list' , model:[
			entities: Expression.list(params), 
			entityCount: Expression.count(),
			code: 'expression.label',
			template: 'expression/expressionList'
		])
	}
	
	def getDescription = {
		def expression = null;
		if (NumberUtils.isNumber(params['expression'])) {
			expression = Constant.get(params['expression'])
		}
		
		if (expression == null) {
			render(contentType:"text/json") {
				result = 'error'
			}
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/templates/expressionDescription', model: [expression: expression])
			}
		}
	}
	
	//FIXME getData Method has to be relocated

	def getConstantDescription = {
		def constant = null;
		if (NumberUtils.isNumber(params['constant'])) {
			constant = Constant.get(params['constant'])
		}
		
		if (constant == null) {
			render(contentType:"text/json") {
				result = 'error'
			}
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/templates/constantDescription', model: [constant: constant])
			}
		}
	}
	
	def getData = {
//		def dataSet = null
//		if (NumberUtils.isNumber(params['dataSetFilter'])) {
//			dataSet = DataSet.get(params['dataSetFilter'])
//		}
//		if (params['type'] == 'constant') {
//			def constants = dataService.searchConstants(params['searchText']);
//			render(contentType:"text/json") {
//				result = 'success'
//				html = g.render(template:'/templates/constants', model:[constants: constants])
//			}
//		}
//		else {
			def dataElements = dataService.searchDataElements(params['searchText']);
			render(contentType:"text/json") {
				result = 'success'
				html = g.render(template:'/templates/dataElements', model:[dataElements: dataElements])
			}
//		}
	}

	def getDataElementDescription = {
		def dataElement = null;
		if (NumberUtils.isNumber(params['dataElement'])) {
			dataElement = DataElement.get(params['dataElement'])
		}
		def enume = null;
		if (dataElement != null) enume = dataElement.getEnume()
		
		if (dataElement == null) {
			render(contentType:"text/json") {
				result = 'error'
			}
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/templates/dataElementDescription', model: [dataElement: dataElement, enume: enume])
			}
		}
	}
	
}

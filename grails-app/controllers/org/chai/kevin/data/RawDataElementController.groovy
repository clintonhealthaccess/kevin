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
package org.chai.kevin.data
/**
 * @author JeanKahigiso
 *
 */
import org.chai.kevin.AbstractEntityController
import org.chai.kevin.DataService
import org.chai.kevin.OrganisationService
import org.chai.kevin.ValueService
import org.chai.kevin.survey.SurveyElement
import org.chai.kevin.survey.SurveyService
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.hisp.dhis.period.Period


class RawDataElementController extends AbstractEntityController {

	DataService dataService;
	ValueService valueService;
	OrganisationService organisationService;
	SurveyService surveyService;

	def getEntity(def id) {
		return RawDataElement.get(id)
	}

	def createEntity() {
		def entity = new RawDataElement();
		entity.type = new Type();
		return entity;
	}

	def getLabel() {
		return 'rawdataelement.label'
	}
	
	def getTemplate() {
		return "/entity/data/createRawDataElement";
	}

	def getModel(def entity) {
		return [
			rawDataElement: entity,
			hasValues: entity.id != null && valueService.getNumberOfValues(entity) != 0,
			enumes: Enum.list(),
			code: getLabel()
		]
	}

	def validateEntity(def entity) {
		boolean valid = entity.validate()
		
		if (entity.id != null && !entity.getType().equals(new Type(params['type.jsonValue'])) && valueService.getNumberOfValues(entity) != 0) {
			// error if types are different
			entity.errors.rejectValue('type', 'rawDataElement.type.cannotChange', 'Cannot change type because the element has associated values.')
			valid = false
		}
		return valid;
	}

	def deleteEntity(def entity) {
		// we delete the entity only if there are no associated values
		// should we throw an exception in case we can't delete ?
		
		// TODO a data element can have associated survey elements
		if (valueService.getNumberOfValues(entity) == 0) entity.delete(flush: true)
		else {
			flash.message = message(code: "rawdataelement.delete.hasvalues", default: "Could not delete element, it still has values");
		}
	}

	def bindParams(def entity) {
		bindData(entity, params, [exclude:'type.jsonValue'])
		
		// we assign the new type only if there are no associated values
		if (entity.id == null || valueService.getNumberOfValues(entity) == 0) {
			entity.type = new Type()
			bindData(entity, params, [include:'type.jsonValue'])
		}
				
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = params.descriptions
	}
	
	def search = {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		params.offset = params.offset ? params.int('offset'): 0
		
		List<RawDataElement> rawDataElements = dataService.searchData(RawDataElement.class, params['q'], [], params);
		
		render (view: '/entity/list', model:[
			entities: rawDataElements,
			template: "data/rawDataElementList",
			code: getLabel(),
			entityCount: dataService.countData(RawDataElement.class, params['q'], []),
			search: true
		])
	}

	def list = {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		params.offset = params.offset ? params.int('offset'): 0
		
		List<RawDataElement> rawDataElements = RawDataElement.list(params);
		
		render (view: '/entity/list', model:[
			entities: rawDataElements,
			template: "data/rawDataElementList",
			code: getLabel(),
			entityCount: RawDataElement.count()
		])
	}

	def getExplainer = {
		def rawDataElement = RawDataElement.get(params.int('id'))

		if (rawDataElement != null) {
			List<Period> iterations = Period.list();
			Set<SurveyElement> surveyElements = surveyService.getSurveyElements(rawDataElement, null);

			Map<Period, Long> periodValues = new HashMap<Period,Integer>();
			for(Period iteration : iterations) {
				periodValues.put(iteration, valueService.getNumberOfValues(rawDataElement, iteration));
			}

			Map<SurveyElement, Integer> surveyElementMap = new HashMap<SurveyElement,Integer>();
			for(SurveyElement surveyElement: surveyElements) {
				surveyElementMap.put(surveyElement, surveyService.getNumberOfOrganisationUnitApplicable(surveyElement));
			}

			render (view: '/entity/data/explain',  model: [
				rawDataElement: rawDataElement, surveyElements: surveyElementMap, periodValues: periodValues
			])
		}
	}

	def getData = {
		def includeTypes = params.list('include')
		def rawDataElements = dataService.searchData(RawDataElement.class, params['searchText'], includeTypes, [:]);
		
		render(contentType:"text/json") {
			result = 'success'
			html = g.render(template:'/entity/data/rawDataElements', model:[rawDataElements: rawDataElements])
		}
	}

	def getDescription = {
		def rawDataElement = RawDataElement.get(params.int('id'))

		if (rawDataElement == null) {
			render(contentType:"text/json") { result = 'error' }
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/entity/data/rawDataElementDescription', model: [rawDataElement: rawDataElement])
			}
		}
	}
}

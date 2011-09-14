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
import org.apache.commons.lang.math.NumberUtils
import org.chai.kevin.AbstractEntityController
import org.chai.kevin.DataService
import org.chai.kevin.OrganisationService
import org.chai.kevin.PeriodSorter
import org.chai.kevin.ValueService
import org.chai.kevin.data.DataElement
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveyElement
import org.chai.kevin.survey.SurveyElementService
import org.chai.kevin.value.DataValue
import org.hisp.dhis.organisationunit.OrganisationUnit
import org.hisp.dhis.period.Period
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class DataElementController extends AbstractEntityController {

	DataService dataService;
	ValueService valueService;
	OrganisationService organisationService;
	SurveyElementService surveyElementService;

	def getEntity(def id) {
		return DataElement.get(id)
	}

	def createEntity() {
		def entity = new DataElement();
		if (params['enume.id']) entity.enume = Enum.get(params.enumId)
		return entity;
	}

	def getTemplate() {
		return "/entity/data/createDataElement";
	}

	def getModel(def entity) {
		return [
			dataElement: entity,
			enume: entity.enume,
			enumes: Enum.list()
		]
	}

	def validateEntity(def entity) {
		return entity.validate()
	}

	def saveEntity(def entity) {
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
		List<DataElement> dataElements = DataElement.list(params);
		
		render (view: '/entity/list', model:[
			entities: dataElements,
			template: "data/dataElementList",
			code: "dataelement.label",
			entityCount: DataElement.count()
		])
	}

	def getExplainer={
		def dataElement = null;
		if (NumberUtils.isNumber(params['dataElement'])) {
			dataElement = DataElement.get(params['dataElement'])
		}

		if (dataElement == null) {
			render(contentType:"text/json") { result = 'error' }
		}
		else {
			List<Period> iterations = Period.list();
			Map<SurveyElement,Integer> surveyElmnts = new HashMap<SurveyElement,Integer>();
			Map<Period,Integer> periodValues = new HashMap<Period,Integer>();
			Set<SurveyElement> surveyElements = surveyElementService.getSurveyElements(dataElement);


			for(Period iteration : iterations)
				periodValues.put(iteration, valueService.getNumberOfValues(dataElement, iteration));
			for(SurveyElement surveyElement: surveyElements)
				surveyElmnts.put(surveyElement, surveyElementService.getTotalOrgUnitApplicable(surveyElement));

			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/templates/dataElementExplainer', model: [dataElement: dataElement,surveyElements: surveyElmnts,periodValues: periodValues])
			}
		}
	}


	def getData = {
		def dataElements = dataService.searchDataElements(params['searchText']);
		render(contentType:"text/json") {
			result = 'success'
			html = g.render(template:'/templates/dataElements', model:[dataElements: dataElements])
		}
	}

	def getDataElementDescription = {
		def dataElement = null;
		if (NumberUtils.isNumber(params['dataElement'])) {
			dataElement = DataElement.get(params['dataElement'])
		}
		def enume = null;
		if (dataElement != null) enume = dataElement.getEnume()

		if (dataElement == null) {
			render(contentType:"text/json") { result = 'error' }
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/templates/dataElementDescription', model: [dataElement: dataElement, enume: enume])
			}
		}
	}
}

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

import org.chai.kevin.AbstractController;
import org.chai.kevin.AbstractEntityController
import org.chai.kevin.util.Utils
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * @author Jean Kahigiso M.
 *
 */
class CalculationController extends AbstractController {

	def dataService
	
	def index = {
		redirect (action: "list", params: params)
	}
	
	def getLabel() {
		return "calculation.label"
	}
	
	def Class getEntityClass(){
		return Calculation.class;
//		return [Aggregation.class, Mode.class, Sum.class];
	}
	
	def saveEntity(def entity) {
		if (entity.id != null && !params['oldType'].equals(new Type(params['typeString']))) {
			def surveyElements = surveyService.getSurveyElements(entity, null);
			if (log.isDebugEnabled()) log.debug("deleting SurveyEnteredValues for "+surveyElements);
			surveyElements.each { element ->
				surveyValueService.deleteEnteredValues(element)
			}
		}
		entity.save(flush: true)
	}

	def validateEntity(def entity) {
		//TODO check for duplicate code
		boolean valid = entity.validate()
		if (entity.id != null && !params['oldType'].equals(entity.type) && valueService.getNumberOfValues(entity)) {
			// error if types are different
			entity.errors.rejectValue('type', 'rawdataelement.type.cannotChange', 'Cannot change type because the element has associated values.')
			valid = false
		}
		return valid;
	}
	
	def deleteEntity(def entity) {
		// we delete the entity only if there are no associated values
		// should we throw an exception in case we can't delete ?
		if (valueService.getNumberOfValues(entity) != 0) {
			if (log.debugEnabled) log.debug('not deleting data, it still has values')
			flash.message = message(code: "calculation.delete.hasvalues", default: "Could not delete calculation, it still has values.");
		}
		else if (!dataService.getReferencingData(entity).isEmpty()) {
			if (log.debugEnabled) log.debug('not deleting data, it still has referencing data elements')
			flash.message = message(code: "calculation.delete.hasreferencingdata", default: "Could not delete calculation, some other data still reference this calculation.")
		}
		else if (AbstractReportTarget.countByData(entity) > 0) {
			if (log.debugEnabled) log.debug('not deleting data, it still has referencing targets')
			flash.message = message(code: "calculation.delete.hasreporttargets", default: "Could not delete calculation, some reports use this calculation.")
		}
		else {
			if (log.debugEnabled) log.debug('deleting data')
			// delete all survey elements and survey entered values
			surveyService.getSurveyElements(entity, null).each {
				surveyValueService.deleteEnteredValues(it)
				
				it.question.removeSurveyElement(it)
				it.delete(flush: true)
			}
			
			dataService.delete(entity);
		}
	}

	def bindParams(def entity) {
		params['oldType'] = entity.type
		
		bindData(entity, params, [exclude:'typeString'])
		
		// we assign the new type only if there are no associated values
		if (entity.id == null || !valueService.getNumberOfValues(entity)) {
			bindData(entity, params, [include:'typeString'])
		}
	}
	
	def search = {
		adaptParamsForList()
		
		def calculations = dataService.searchData(Calculation.class, params['q'], [], params);
		
		render (view: '/entity/list', model:[
			entities: calculations,
			entityCount: calculations.totalCount,
			entityClass: getEntityClass(),
			template: "data/calculationList",
			code: getLabel(),
			search: true
		])
	}
	
	def list = {
		adaptParamsForList()
		def calculations = Calculation.list(params)		
		
		render (view: '/entity/list', model:[
			entities: calculations,
			template: "data/calculationList",
			entityCount: calculations.totalCount,
			code: 'calculation.label',
			addTemplate: '/entity/data/addCalculation'
		])
	}
	
}

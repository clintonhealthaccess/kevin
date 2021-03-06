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
package org.chai.kevin.survey

import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.location.DataLocationType;

/**
 * @author Jean Kahigiso M.
 *
 */
class TableRowController extends AbstractEntityController {

	def locationService
	def surveyService
	
	def getEntity(def id) {
		return SurveyTableRow.get(id)
	}
	def createEntity() {
		return new SurveyTableRow();
	}

	def getLabel() {
		return "survey.tablequestion.row.label"
	}
	
	def getTemplate() {
		return "/survey/admin/createTableRow"
	}

	def getModel(def entity) {
		[
			row: entity,
			types: DataLocationType.list([cache: true])
		]
	}

	def getEntityClass(){
		return SurveyTableRow.class;
	}
	
	def deleteEntity(def entity) {
		def surveyElements = entity.surveyElements*.value
		entity.question.removeFromRows(entity)
		entity.delete()
		surveyElements.each {surveyElement ->
			surveyService.deleteSurveyElement(surveyElement)
		}
	}
	
	def saveEntity(def entity) {
		if (entity.id == null) entity.question.addToRows(entity)
		entity.question.save()
	}

	def bindParams(def entity) {
		entity.properties = params
		
		def surveyElements = [:]
		params.surveyElement.each { columnId ->
			if (columnId != '_') {
				def column = SurveyTableColumn.get(columnId)
				def dataElement = RawDataElement.get(params.int('surveyElement['+columnId+'].dataElement.id'))
				if (dataElement != null) {
					def surveyElement = SurveyElement.get(params.int('surveyElement['+columnId+'].id'))
					if (surveyElement == null) {
						surveyElement = new SurveyElement();
						entity.question.addToSurveyElements(surveyElement);
					}
					if (log.isInfoEnabled()) log.info ("binding SurveyElement "+surveyElement)
					surveyElement.dataElement = dataElement
					if (log.isInfoEnabled()) log.info ("binding dataElement "+dataElement)
					surveyElements[column] = surveyElement
				}
			}
		}
		entity.surveyElements = surveyElements
	}
}

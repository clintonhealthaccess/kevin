package org.chai.kevin.cost

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

import org.chai.kevin.data.DataElement;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.AbstractEntityController

class CostTargetController extends AbstractEntityController {
	
	def locationService
	def dataService
	
	def getEntity(def id) {
		return CostTarget.get(id)
	}
	
	def createEntity() {
		return new CostTarget()
	}
	
	def getLabel() {
		return "cost.target.label"
	}
	
	def getTemplate() {
		return "/cost/createTarget"
	}
	
	def getModel(def entity) {
		[
			target: entity,
			programs: ReportProgram.list(),
			dataElements: entity.dataElement!=null?[entity.dataElement]:[],
			dataElementsEnd:  entity.dataElementEnd!=null?[entity.dataElementEnd]:[],
			costRampUps: CostRampUp.list(), 
			types: DataEntityType.list()
		]
	}
	
	def bindParams(def entity) {
		entity.properties = params
		if (params.int('dataElement.id')) entity.dataElement = dataService.getData(params.int('dataElement.id'), DataElement.class)
		if (params.int('dataElementEnd.id')) entity.dataElementEnd = dataService.getData(params.int('dataElementEnd.id'), DataElement.class)
		
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = entity.descriptions

	}
	
	
}

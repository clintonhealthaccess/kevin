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
package org.chai.kevin.fct;
/**
 * @author Jean Kahigiso M.
 *
 */

import grails.plugin.springcache.annotations.CacheFlush;

import org.chai.kevin.AbstractEntityController
import org.chai.kevin.data.Sum
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.util.Utils

class FctTargetController extends AbstractEntityController {

	def dataService
	def locationService

	def getEntity(def id) {
		return FctTarget.get(id)
	}

	def createEntity() {
		return new FctTarget()
	}

	def getLabel() {
		return "fct.target.label"
	}
	
	def getTemplate() {
		return "/entity/fct/createTarget"
	}

	def getModel(def entity) {
		[
			target: entity,
			types: DataLocationType.list([cache: true]),
			programs: ReportProgram.list()
		]
	}

	def getEntityClass(){
		return FctTarget.class;
	}
	
//	def validateEntity(def entity) {
//		return entity.validate()
//	}

	def saveEntity(def entity) {
		entity.save();
	}

	def deleteEntity(def entity) {
		if(entity.targetOptions.size() == 0){
			if (log.isInfoEnabled()) log.info("deleting target: "+entity)			
			entity.delete()
		}
		else {
			flash.message = message(code: 'fct.target.haschildren', args: [message(code: getLabel(), default: 'entity'), params.id], default: 'Fct Target {0} still has associated children.')
		}				
	}
	
	def bindParams(def entity) {
		entity.properties = params

		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = params.descriptions
	}
	
	@CacheFlush("fctCache")
	def save = {
		super.save()
	}
	
	@CacheFlush("fctCache")
	def delete = {
		super.delete()
	}
	
	@CacheFlush("fctCache")
	def edit = {
		super.edit()
	}

	def search = {
		adaptParamsForList()
		
		List<FctTarget> targets = dataService.searchData(FctTarget.class, params['q'], [], params);
		
		render (view: '/entity/list', model:[
			entities: targets,
			entityCount: dataService.countData(FctTarget.class, params['q'], []),
			template: "fct/targetList",
			code: getLabel(),
			search: true
		])
	}
	
	def list = {
		adaptParamsForList()		
		List<FctTarget> targets = FctTarget.list(params);
		
		render (view: '/entity/list', model:[
			entities: targets,
			template: "fct/targetList",
			code: getLabel(),
			entityCount: FctTarget.count(),
			entityClass: getEntityClass()
		])
	}
	
}

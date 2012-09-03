package org.chai.kevin.dsr;

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

/**
 * @author Jean Kahigiso M.
 *
 */

import grails.plugin.springcache.annotations.CacheFlush
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.reports.ReportProgram;

import org.chai.kevin.AbstractEntityController

class DsrTargetController extends AbstractEntityController {

	def locationService
	def dataService

	def getEntity(def id) {
		return DsrTarget.get(id)
	}

	def createEntity() {
		return new DsrTarget()
	}

	def getLabel() {
		return "dsr.target.label"
	}
	
	def getTemplate() {
		return "/entity/dsr/createTarget"
	}

	def getModel(def entity) {
		[
			target: entity,
			programs: ReportProgram.list(),
			categories: DsrTargetCategory.list(),
//			dataElements: entity.dataElement!=null?[entity.dataElement]:[],
			data: entity.data!=null?[entity.data]:[]
		]
	}

	def getEntityClass(){
		return DsrTarget.class;
	}
	
	def deleteEntity(def entity) {
		if(entity.category != null){
			entity.category.targets.remove(entity)
			entity.category.save()
		}
		entity.delete()
	}

	def bindParams(def entity) {
		bindData(entity, params, [exclude:'data.id'])
		if (params.int('data.id')) entity.data = dataService.getData(params.int('data.id'), Data.class)
		
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = params.descriptions
	}
	
	def search = {
		adaptParamsForList()
		
		List<DsrTarget> targets = dataService.searchData(DsrTarget.class, params['q'], [], params);
		
		render (view: '/entity/list', model:[
			entities: targets,
			entityCount: dataService.countData(DsrTarget.class, params['q'], []),
			entityClass: getEntityClass(),
			template: "dsr/targetList",
			code: getLabel(),
			search: true
		])
	}
	
	def list = {
		adaptParamsForList()
		
		List<DsrTarget> targets = DsrTarget.list(params);
		
		render (view: '/entity/list', model:[
			entities: targets,
			template: "dsr/targetList",
			code: getLabel(),
			entityCount: DsrTarget.count(),
			entityClass: getEntityClass()
		])
	}
	
	@CacheFlush("dsrCache")
	def save = {
		super.save()
	}
	
	@CacheFlush("dsrCache")
	def delete = {
		super.delete()
	}
	
	@CacheFlush("dsrCache")
	def edit = {
		super.edit()	
	}
}

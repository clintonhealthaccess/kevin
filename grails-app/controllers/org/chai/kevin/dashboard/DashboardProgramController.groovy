package org.chai.kevin.dashboard

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

import grails.plugin.springcache.annotations.CacheFlush

import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.reports.ReportProgram


class DashboardProgramController extends AbstractEntityController {

	def dashboardService
	
	def getEntity(def id) {
		return DashboardProgram.get(id)
	}
	
	def createEntity() {
		def entity = new DashboardProgram()
		return entity
	}
	
	def getLabel() {
		return "dashboard.program.label"
	}
	
	def getTemplate() {
		return '/entity/dashboard/createProgram';
	}
	
	def deleteEntity(def entity) {
		List<DashboardEntity> dashboardEntities = dashboardService.getDashboardEntities(entity.getReportProgram());
		if(dashboardEntities.size() == 0){
			if (log.isInfoEnabled()) log.info("deleting program entity: "+entity)
			entity.delete()
		}
		else {
			flash.message = message(code: 'dashboard.program.haschildren', args: [message(code: getLabel(), default: 'entity'), params.id], default: 'Dashboard obejctive {0} still has associated children.')
		}
	}
	
	def getModel(def entity) {
		
		def reportPrograms = ReportProgram.list()
		def dashboardPrograms = DashboardProgram.list()
		for (program in dashboardPrograms) reportPrograms.remove(program.getProgram())
		if (entity.program != null) reportPrograms.add(entity.program)
		
		return [entity: entity, programs: reportPrograms]
	}
	
	def bindParams(def entity) {		
		entity.properties = params
		
		if (entity.program) {
			entity.names = entity.program.names
			entity.descriptions = entity.program.descriptions
			entity.code = entity.program.code
		}
	}
	
	@CacheFlush("dashboardCache")
	def edit = {
		super.edit()
	}
	
	@CacheFlush("dashboardCache")
	def save = {
		super.save()
	}
	
	@CacheFlush("dashboardCache")
	def delete = {
		super.delete()
	}
	
	def list = {
		adaptParamsForList()
		List<DashboardProgram> programs = DashboardProgram.list(params);
		
		render (view: '/entity/list', model:[
			entities: programs,
			template: "dashboard/programList",
			code: getLabel(),
			entityCount: DashboardProgram.count()
		])
	}
	
}

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
import org.chai.kevin.data.Calculation;
import org.chai.kevin.reports.ReportObjective;


class DashboardTargetController extends AbstractObjectiveController {

	def getEntity(def id) {
		return DashboardTarget.get(id)
	}
	
	def createEntity() {
		return new DashboardTarget()
	}
	
	def getLabel() {
		return 'dashboard.target.label'
	}
	
	def getTemplate() {
		return '/dashboard/createTarget'
	}
	
	def getModel(def entity) {
		def model = super.getModel(entity)
		
		def calculations = []
		if (entity.calculation != null) calculations.add(entity.calculation)
		
		def dashboardObjectives = DashboardObjective.list()
		def reportObjectives = []
		for(objective in dashboardObjectives)
			reportObjectives.add(objective.getObjective())
		
		model << [entity: entity, objectives: reportObjectives, calculations: calculations]
		return model;
	}
	
	def bindParams(def entity) {
		bindData(entity, params, [exclude:'calculation.id'])
		if (params.int('calculation.id') != null) entity.calculation = dataService.getData(params.int('calculation.id'), Calculation.class)
		
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = params.descriptions
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
}

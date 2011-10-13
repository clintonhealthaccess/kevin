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

import grails.plugin.springcache.annotations.CacheFlush;

import org.apache.catalina.util.RequestUtil;
import org.apache.jasper.compiler.Node.ParamsAction;
import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.chai.kevin.data.Average;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Expression;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.JSONUtils;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

class DashboardTargetController extends AbstractObjectiveController {

	def createEntity() {
		def entity = new DashboardObjectiveEntry()
		entity.entry = new DashboardTarget()
		entity.entry.calculation = new Average()
		return entity
	}
	
	def getLabel() {
		return 'dashboard.target.label'
	}
	
	def getTemplate() {
		return '/dashboard/createTarget'
	}
	
	def saveEntity(def entity) {
		entity.entry.calculation.timestamp = new Date()
		// FIXME change this to infer the correct type
		entity.entry.calculation.type = Type.TYPE_NUMBER()
		if (entity.entry.calculation.id == null) entity.entry.calculation.code = UUID.randomUUID().toString();
		entity.entry.calculation.save()
		super.saveEntity(entity)
	}
	
	def bindParams(def objectiveEntry) {
		
		// FIXME GRAILS-6388 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6388
//		objectiveEntry.entry.calculation.expressions.each() { key, value ->
//			value.expression = params['entry.calculation.expressions['+key+'].expression.id'] != 'null'?new Expression():null
//		}
		
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.entry?.names!=null) objectiveEntry.entry.names = params.entry?.names
		if (params.entry?.descriptions!=null) objectiveEntry.entry.descriptions = params.entry?.descriptions
		
		objectiveEntry.properties = params;
		
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

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
import org.chai.kevin.data.DataElement;

import org.chai.kevin.AbstractEntityController
import org.hisp.dhis.dataelement.DataElementService;


class DsrTargetController extends AbstractEntityController {

	def organisationService
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
		return "/dsr/createTarget"
	}

	def getModel(def entity) {
		[
			target: entity,
			objectives: DsrObjective.list(),
			groups: organisationService.getGroupsForExpression(),
			dataElements: dataService.list(DataElement.class),
			categories: DsrTargetCategory.list()
		]
	}

	def deleteEntity(def entity) {
		if(entity.category != null){
			entity.category.targets.remove(entity)
			entity.category.save()
		}
		entity.objective.targets.remove(entity)
		entity.objective.save()
		entity.delete()
	}

	def bindParams(def entity) {
		bindData(entity, params, [exclude:'dataElement.id'])
		entity.dataElement = dataService.getData(params.int('dataElement.id'), DataElement.class)

		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
//		entity.groupUuids = params['groupUuids']
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = params.descriptions
	}
	
	@CacheFlush("dsrCache")
	def save = {
		super.save()
	}
	
	@CacheFlush("dsrCache")
	def delete = {
		super.delete()
	}
}

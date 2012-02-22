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
package org.chai.kevin.planning

import org.chai.kevin.AbstractEntityController
import org.chai.kevin.PeriodSorter
import org.chai.kevin.Translation;
import org.hisp.dhis.period.Period
/**
 * @author Jean Kahigiso M.
 *
 */
class PlanningTypeController extends AbstractEntityController {
	
	def languageService
	
	def getEntity(def id) {
		return PlanningType.get(id)
	}

	def createEntity() {
		PlanningType type = new PlanningType()
	}

	def getLabel() {
		return 'planningType.label'
	}
	
	def getTemplate() {
		return "/planning/admin/createPlanningType"
	}

	def getModel(def entity) {
		def dataElements = []
		if (entity.dataElement != null) dataElements << entity.dataElement
		def sections = []
		if (entity.dataElement != null) sections.addAll entity.sections
		def headerPrefixes = []
		if (entity.dataElement?.headerPrefixes != null) headerPrefixes.addAll entity.dataElement.headerPrefixes
		[
			sections: sections,
			headerPrefixes: headerPrefixes,
			planningType: entity,
			dataElements: dataElements
		]
	}

	def bindParams(def entity) {
		entity.properties = params

		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.namesPlural!=null) entity.namesPlural = params.namesPlural
		
		// headers
		params.list('headerList').each { prefix ->
			Translation translation = new Translation()
			languageService.availableLanguages.each { language ->
				translation[language] = params['headerList['+prefix+'].'+language]
			}
			entity.headers.put(prefix, translation)
		}
		// section description
		params.list('sectionList').each { prefix ->
			Translation translation = new Translation()
			languageService.availableLanguages.each { language ->
				translation[language] = params['sectionList['+prefix+'].'+language]
			}
			entity.sectionDescriptions.put(prefix, translation)
		}
	}

	def list = {
		adaptParamsForList()
		
		Planning planning = Planning.get(params.int('planning.id'))
		if (planning == null) response.sendError(404)
		else {
			List<PlanningType> planningTypes = planning.planningTypes
	
			render (view: '/planning/admin/list', model:[
				template:"planningTypeList",
				entities: planningTypes,
				entityCount: planningTypes.size(),
				code: getLabel()
			])
		}
	}
	
}
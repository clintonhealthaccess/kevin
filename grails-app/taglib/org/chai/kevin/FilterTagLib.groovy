package org.chai.kevin

import org.chai.kevin.LocationService;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
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
import org.hisp.dhis.period.Period;

class FilterTagLib {

	def locationService;

	def createLinkByFilter = {attrs, body ->
		if (attrs['params'] == null) attrs['params'] = [:]
		else{
			Map params = new HashMap(attrs['params'])
			attrs['params'] = updateParamsByFilter(params);
		}
		out << createLink(attrs, body)
	}

	def iterationFilter = {attrs, body ->
		Period.withTransaction {
			out << render(template:'/templates/iterationFilter', model:attrs)
		}
	}
	
	def organisationFilter = {attrs, body ->
		LocationEntity.withTransaction {
			out << render(template:'/templates/organisationFilter', model:attrs)
		}
	}
	
	def levelFilter = {attrs, body ->
		LocationLevel.withTransaction {
			out << render(template:'/templates/levelFilter', model:attrs)
		}
	}
	
	public Map updateParamsByFilter(Map params) {
		if (!params.containsKey("filter")) return params;
		String filter = (String) params.get("filter");

		LocationEntity entity = null;
		if (params.get("organisation") != null) {
			entity = LocationEntity.get(Integer.parseInt(params.get("organisation")))
		}

		LocationLevel level = null;
		if (params.get("level") != null) {
			level = LocationLevel.get(Integer.parseInt(params.get('level')))
		}

		if (entity != null) {
			if (level != null) {
				// TODO use isAfter()
				if (entity.getLevel().getOrder() >= level.getOrder()) {
					// conflict
					if (filter == "level") {
						// adjust organisation to level
						LocationLevel levelBefore = locationService.getLevelBefore(entity.getLevel())
						if (levelBefore == null) entity = locationService.getRootLocation();
						else entity = locationService.getParentOfLevel(entity, levelBefore);
					}
					// conflict
					else {
						// adjust level to organisation
						level = locationService.getLevelAfter(entity.getLevel())
					}
				}
			}
			// conflict
			else {
				// adjust level to organisation
				level = locationService.getLevelAfter(entity.getLevel())
			}
		}
		if (entity != null) params.put("organisation", entity.id);
		if (level != null) params.put("level", level.id);
		return params;
	}

}
package org.chai.kevin

import org.chai.kevin.OrganisationService;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.organisationunit.OrganisationUnitService
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

class FilterTagLib {

	OrganisationService organisationService;
	OrganisationUnitService organisationUnitService;

	def createLinkByFilter = {attrs, body ->
		if (attrs['params'] == null) attrs['params'] = [:]
		else{
			Map params = new HashMap(attrs['params'])
			attrs['params'] = updateParamsByFilter(params);
		}
		out << g.createLink(attrs, body)
	}

	public Map updateParamsByFilter(Map params) {
		if (!params.containsKey("filter")) return params;
		String filter = (String) params.get("filter");

		Organisation organisation = null;
		if (params.get("organisation") != null) {
			Object id = params.get("organisation") 
			if (id instanceof Integer) organisation = organisationService.getOrganisation((Integer) params.get("organisation"));
			else organisation = organisationService.getOrganisation(Integer.parseInt(params.get("organisation")));
		}

		OrganisationUnitLevel orgUnitLevel = null;
		if (params.get("level") != null) {
			Object id = params.get("level")
			if (id instanceof Integer) orgUnitLevel = organisationUnitService.getOrganisationUnitLevel((Integer) params.get("level"));
			else orgUnitLevel = organisationUnitService.getOrganisationUnitLevel(Integer.parseInt(params.get("level")));
		}

		if (organisation != null) {
			organisationService.loadLevel(organisation);

			if (orgUnitLevel != null) {

				if (organisation.getLevel() >= orgUnitLevel.getLevel()) {
					// conflict
					if (filter == "level") {
						// adjust organisation to level
						if (orgUnitLevel.getLevel() == 1)
							organisation = organisationService.getRootOrganisation();
						else
							organisation = organisationService.getParentOfLevel(organisation, orgUnitLevel.getLevel() - 1);
						params.put("organisation", organisation.getId());
					}
					// conflict
					else {
						// adjust level to organisation
						orgUnitLevel = organisationUnitService
								.getOrganisationUnitLevelByLevel(organisation
								.getLevel() + 1);
						params.put("level", orgUnitLevel.getLevel());
					}
				}
				else {
					params.put("level", orgUnitLevel.getLevel());
				}
			}
			// conflict
			else {
				// adjust level to organisation
				orgUnitLevel = organisationUnitService
						.getOrganisationUnitLevelByLevel(organisation
						.getLevel() + 1);
				params.put("level", orgUnitLevel.getLevel());
			}
		}
		return params;
	}

}
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
package org.chai.kevin.dsr;

/**
 * @author Jean Kahigiso M.
 *
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.DataService;
import org.chai.kevin.LanguageService;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.OrganisationSorter;
import org.chai.kevin.ValueService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.ExpressionValue;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

import grails.plugin.springcache.annotations.Cacheable;

public class DsrService {
	
	private OrganisationService organisationService;
	private ValueService valueService;
	private DataService dataService;
	private LanguageService languageService;
	private int groupLevel;
	
	@Cacheable("dsrCache")
	@Transactional(readOnly = true)
	public DsrTable getDsr(Organisation organisation, DsrObjective objective, Period period) {
		
		List<Organisation>  organisations = organisationService.getChildrenOfLevel(organisation, organisationService.getFacilityLevel());
		Map<Organisation, Organisation> orgParentMap = this.getParentOfLevel(organisations,groupLevel);
		OrganisationSorter organisationSorter = new OrganisationSorter(orgParentMap,organisationService);
		Collections.sort(organisations, organisationSorter.BY_FACILITY_TYPE);
		
		List<DsrTarget> targets = objective.getTargets();
		Collections.sort(targets, new DsrTargetSorter());
		
		Map<Organisation, Map<DsrTarget, Dsr>>  dsrMap = new HashMap<Organisation, Map<DsrTarget, Dsr>>();
		Set<OrganisationUnitGroup> facilityTypes = new LinkedHashSet<OrganisationUnitGroup>();
		
		for (Organisation child : organisations) {
			organisationService.loadGroup(child);
			if (child.getOrganisationUnitGroup() != null) {
				facilityTypes.add(child.getOrganisationUnitGroup());
			}
			Map<DsrTarget, Dsr> orgDsr = new HashMap<DsrTarget, Dsr>();
			for (DsrTarget target : targets) {
				boolean applies = Utils.split(target.getGroupUuidString()).contains(child.getOrganisationUnitGroup().getUuid());
				String value = null;
				
				if (applies) {
					ExpressionValue expressionValue = valueService.getValue( target.getExpression(), child.getOrganisationUnit(), period);
					
					if (expressionValue != null && !expressionValue.getValue().isNull()) {
						// TODO put this in templates ?
						switch (expressionValue.getData().getType().getType()) {
						case BOOL:
							if (expressionValue.getValue().getBooleanValue()) value = "&#10003;";
							else value = "";
							break;
						case STRING:
							value = expressionValue.getValue().getStringValue();
							break;
						case NUMBER:
							value = getFormat(target, expressionValue.getValue().getNumberValue().doubleValue());
							break;
						case ENUM:
							String code = expressionValue.getData().getType().getEnumCode();
							Enum enume = dataService.findEnumByCode(code);
							if (enume != null) {
								EnumOption option = enume.getOptionForValue(expressionValue.getValue().getEnumValue());
								value = languageService.getText(option.getNames());
							}
							else value = "";
							break;
						default:
							value = "";
							break;
						}
					}
					
				}
				orgDsr.put(target, new Dsr(value, applies));
			}
			dsrMap.put(child, orgDsr);
		}

		return new DsrTable(organisations, targets, facilityTypes, dsrMap,orgParentMap);
	}

	private static String getFormat(DsrTarget target, Double value) {
		String format = target.getFormat();
		if (format == null) format = "#";
		
		DecimalFormat frmt = new DecimalFormat(format);
		return frmt.format(value).toString();
	}
	
	public Map<Organisation,Organisation> getParentOfLevel(List<Organisation> organisations,Integer level){
		Map<Organisation,Organisation> organisationMap = new HashMap<Organisation, Organisation>();
		for(Organisation organisation : organisations){
			organisationMap.put(organisation, organisationService.getParentOfLevel(organisation, level));
		}
		return organisationMap;
	}
	
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}

	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}
	
	public void setGroupLevel(int groupLevel) {
		this.groupLevel = groupLevel;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}
	
}

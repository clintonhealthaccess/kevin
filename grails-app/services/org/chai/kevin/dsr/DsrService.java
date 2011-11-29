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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

<<<<<<< HEAD
=======
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.DataService;
>>>>>>> dsr_refactor
import org.chai.kevin.LanguageService;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.OrganisationSorter;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

import grails.plugin.springcache.annotations.Cacheable;

public class DsrService {	
	private static final Log log = LogFactory.getLog(DsrService.class);
	
	private OrganisationService organisationService;
	private ValueService valueService;
	private DataService dataService;
	private LanguageService languageService;
	private int groupLevel;
	
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
	
	@Cacheable("dsrCache")
	@Transactional(readOnly = true)
	public DsrTable getDsr(Organisation organisation, DsrObjective objective, Period period, Set<String> groupUuids) {
		
		List<Organisation>  organisations = new ArrayList<Organisation>();
		for (String groupUuid : groupUuids) {
			organisations.addAll(organisationService.getFacilitiesOfGroup(organisation, organisationService.getOrganisationUnitGroup(groupUuid)));
		}
		
<<<<<<< HEAD
		Map<Organisation, Organisation> orgParentMap = this.getParentOfLevel(organisations,groupLevel);
		OrganisationSorter organisationSorter = new OrganisationSorter(orgParentMap,organisationService);
		Collections.sort(organisations, organisationSorter.BY_FACILITY_TYPE);
=======
		if (log.isDebugEnabled()) 
			log.debug("getDsr(period="+period+",organisation="+organisation+",objective="+objective+")");
>>>>>>> dsr_refactor
		
		List<Organisation> facilities = organisationService.getChildrenOfLevel(organisation, organisationService.getFacilityLevel());		
		
		Map<Organisation, List<Organisation>> orgParentMap = getParents(facilities, groupLevel);		
		List<DsrTarget> targets = objective.getTargets();		
		
		Map<Organisation, Map<DsrTarget, Dsr>> dsrMap = new HashMap<Organisation, Map<DsrTarget, Dsr>>();
		Set<OrganisationUnitGroup> facilityTypes = new LinkedHashSet<OrganisationUnitGroup>();
		
		for (Organisation facility : facilities) {
			organisationService.loadGroup(facility);
			if (facility.getOrganisationUnitGroup() != null) {
				facilityTypes.add(facility.getOrganisationUnitGroup());
			}
			Map<DsrTarget, Dsr> orgDsr = new HashMap<DsrTarget, Dsr>();
			for (DsrTarget target : targets) {
				boolean applies = Utils.split(target.getGroupUuidString()).contains(facility.getOrganisationUnitGroup().getUuid());
				String value = null;
				
				if (applies) {
<<<<<<< HEAD
					DataValue expressionValue = valueService.getDataElementValue( target.getDataElement(), child.getOrganisationUnit(), period);
=======
					ExpressionValue expressionValue = valueService.getValue(target.getExpression(), facility.getOrganisationUnit(), period);
>>>>>>> dsr_refactor
					
					if (expressionValue != null && !expressionValue.getValue().isNull()) {
						// TODO put this in templates ?
						switch (target.getDataElement().getType().getType()) {
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
							String code = target.getDataElement().getType().getEnumCode();
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
				orgDsr.put(target, new Dsr(value));
			}
			dsrMap.put(facility, orgDsr);
		}

		return new DsrTable(facilities, targets, dsrMap, orgParentMap);
	}

	private static String getFormat(DsrTarget target, Double value) {
		String format = target.getFormat();
		if (format == null) format = "#";
		
		DecimalFormat frmt = new DecimalFormat(format);
		return frmt.format(value).toString();
	}
	
	public Map<Organisation, List<Organisation>> getParents(List<Organisation> organisations, Integer level){
		
		Map<Organisation, List<Organisation>> organisationMap = new HashMap<Organisation, List<Organisation>>();
		
		for (Organisation org : organisations){
			organisationService.loadLevel(org);
			organisationService.loadGroup(org);
			Organisation parentOrg = organisationService.getParentOfLevel(org, level);			
			if(!organisationMap.containsKey(parentOrg))
				organisationMap.put(parentOrg, new ArrayList<Organisation>());
			organisationMap.get(parentOrg).add(org);
		}
				
		//sort organisation map keys
		List<Organisation> sortedOrganisations = new ArrayList<Organisation>(organisationMap.keySet());
		Collections.sort(sortedOrganisations, OrganisationSorter.BY_LEVEL);
		
		//sort organisation map values
		LinkedHashMap<Organisation, List<Organisation>> sortedOrganisationMap = new LinkedHashMap<Organisation, List<Organisation>>();		
		for (Organisation org : sortedOrganisations){
			List<Organisation> sortedList = organisationMap.get(org);
			Collections.sort(sortedList, OrganisationSorter.BY_FACILITY_TYPE);
			sortedOrganisationMap.put(org, sortedList);
		}
		
		return sortedOrganisationMap;
	}

}

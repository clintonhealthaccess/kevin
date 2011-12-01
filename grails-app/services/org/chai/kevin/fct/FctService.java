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
package org.chai.kevin.fct;

/**
 * @author Jean Kahigiso M.
 *
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.OrganisationSorter;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;

public class FctService {
	private static final Log log = LogFactory.getLog(FctService.class);
	
	private OrganisationService organisationService;
	private ValueService valueService;
	
	public FctTable getFct(Organisation organisation, FctObjective objective, Period period, OrganisationUnitLevel orgUnitLevel, Set<String> groupUuids) {
		if (log.isDebugEnabled())  log.debug("getFct(period="+period+",organisation="+organisation+",objective="+objective+",orgUnitlevel="+orgUnitLevel.getLevel()+")");		

		organisationService.loadParent(organisation);
		organisationService.loadLevel(organisation);
		
		// TODO get organisations from group only
		List<Organisation> organisations = organisationService.getChildrenOfLevel(organisation, orgUnitLevel.getLevel());				
		
		Map<Organisation, List<Organisation>> orgParentMap = getParents(organisation, organisations);
		List<FctTarget> targets = objective.getTargets();
		
		Map<FctTarget, Map<Organisation, Fct>> fctMap = new HashMap<FctTarget, Map<Organisation, Fct>>();
		Map<FctTarget, Fct> totalMap = new HashMap<FctTarget, Fct>();
		Set<OrganisationUnitGroup> facilityTypes = new LinkedHashSet<OrganisationUnitGroup>();
		
		for(FctTarget target: targets) {
			Map<Organisation, Fct> orgFct = new HashMap<Organisation, Fct>();
			totalMap.put(target, getFctValue(target, organisation, period, groupUuids));
			
			for (Organisation child : organisations) {
				organisationService.loadLevel(child);
				organisationService.loadGroup(child);
				
				OrganisationUnitGroup orgFacilityType = child.getOrganisationUnitGroup();
				if (orgFacilityType != null) {
					facilityTypes.add(orgFacilityType);
				}	
				
				if (orgFacilityType != null) {
					
					Set<String> targetFacilityTypes = Utils.split(target.getGroupUuidString());
					String orgFacilityTypeUuid = orgFacilityType.getUuid();					
					
					if(!targetFacilityTypes.contains(orgFacilityTypeUuid))
						continue;
				}					
				
				if (log.isDebugEnabled()) log.debug("getting values for sum fct with calculation: "+target.getSum());
				
				orgFct.put(child, getFctValue(target, child, period, groupUuids));
			}											
			fctMap.put(target, orgFct);
		}

		FctTable fctTable = new FctTable(organisations, targets, facilityTypes, fctMap, orgParentMap, totalMap);
		if (log.isDebugEnabled()) log.debug("getFct(...)="+fctTable);
		return fctTable;
	}
	
	private Fct getFctValue(FctTarget target, Organisation organisation, Period period, Set<String> groupUuids) {
		String value = null;
		CalculationValue<?> calculationValue = valueService.getCalculationValue(target.getSum(), organisation.getOrganisationUnit(), period, groupUuids);
		if (calculationValue != null) value = calculationValue.getValue().getNumberValue().toString();
		return new Fct(organisation, period, target, value);
	}
	
	private Map<Organisation, List<Organisation>> getParents(
			Organisation organisation, List<Organisation> organisations) {
		
		Map<Organisation, List<Organisation>> organisationMap = new HashMap<Organisation, List<Organisation>>();										
		
		for (Organisation org : organisations) {
			organisationService.loadParent(org);
			organisationService.loadLevel(org);
			Organisation parentOrg = organisationService.getParentOfLevel(org, org.getLevel()-1);			
			
			if(!organisationMap.containsKey(parentOrg))
				organisationMap.put(parentOrg, new ArrayList<Organisation>());
			organisationMap.get(parentOrg).add(org);
		}
				
		//sort organisation map keys
		List<Organisation> sortedOrganisations = new ArrayList<Organisation>(organisationMap.keySet());
		Collections.sort(sortedOrganisations, OrganisationSorter.BY_LEVEL);
		
		//sort organisation map values
		LinkedHashMap<Organisation, List<Organisation>> sortedOrganisationMap = new LinkedHashMap<Organisation, List<Organisation>>();		
		for (Organisation org : sortedOrganisations)
		{
			List<Organisation> sortedList = organisationMap.get(org);
			Collections.sort(sortedList, OrganisationSorter.BY_LEVEL);
			sortedOrganisationMap.put(org, sortedList);
		}
		
		return sortedOrganisationMap;
	}

	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}

	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}		

}

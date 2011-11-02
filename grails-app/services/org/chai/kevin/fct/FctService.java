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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.OrganisationSorter;
import org.chai.kevin.ValueService;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.CalculationValue;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;

public class FctService {
	private static final Log log = LogFactory.getLog(FctService.class);
	
	private OrganisationService organisationService;
	private ValueService valueService;
	private int groupLevel;
	
	public FctTable getFct(Organisation organisation, FctObjective objective, Period period, OrganisationUnitLevel orgUnitLevel) {
		
		if (log.isDebugEnabled()) 
			log.debug("getFct(period="+period+",organisation="+organisation+",objective="+objective+",orgUnitlevel="+orgUnitLevel.getLevel()+")");		

		List<FctTarget> targets = null;
		Map<Organisation, List<Organisation>> orgParentMap = null;
		Map<FctTarget, Fct> orgFct = null;
		Map<Organisation, Map<FctTarget, Fct>> fctMap = null;
		Set<OrganisationUnitGroup> facilityTypes = null;
		
		List<OrganisationUnitLevel> levels = organisationService.getAllLevels();
		levels.remove(0);		
		if (levels.isEmpty()) {
			// TODO throw exception 
		}
		
		organisationService.loadParent(organisation);
		organisationService.loadLevel(organisation);
		
		List<Organisation> organisations = organisationService.getChildrenOfLevel(organisation, orgUnitLevel.getLevel());				
		
		orgParentMap = this.getParents(organisation, organisations);
		targets = objective.getTargets();
		
		orgFct = new HashMap<FctTarget, Fct>();
		fctMap = new HashMap<Organisation, Map<FctTarget, Fct>>();
		facilityTypes = new LinkedHashSet<OrganisationUnitGroup>();
		
		for (Organisation child : organisations) {
			
			organisationService.loadLevel(child);
			organisationService.loadGroup(child);
			
			OrganisationUnitGroup orgFacilityType = child.getOrganisationUnitGroup();
			if (orgFacilityType != null) {
				facilityTypes.add(orgFacilityType);
			}
			
			String value = null;
			
			for(FctTarget target: targets) {
												
				if (orgFacilityType != null) {
					
					Set<String> targetFacilityTypes = Utils.split(target.getGroupUuidString());
					String orgFacilityTypeUuid = orgFacilityType.getUuid();					
					
					if(!targetFacilityTypes.contains(orgFacilityTypeUuid))
						continue;
				}					
				
				if (log.isDebugEnabled()) log.debug("getting values for sum fct with calculation: "+target.getSum());
				
				CalculationValue calculationValue = valueService.getValue(target.getSum(), child.getOrganisationUnit(), period);
				if (calculationValue != null) 
					value = calculationValue.getValue().getStringValue();				
				orgFct.put(target, new Fct(child, period, target, value));
			}											
			fctMap.put(child, orgFct);
		}

		return new FctTable(organisations, targets, facilityTypes, fctMap, orgParentMap);
	}
	
	private Map<Organisation, List<Organisation>> getParents(
			Organisation organisation, List<Organisation> organisations) {
		
		Map<Organisation, List<Organisation>> organisationMap = new HashMap<Organisation, List<Organisation>>();										
		
		//add "total" organisation
		organisations.add(0, organisation);
		organisationService.loadParent(organisation);
		organisationService.loadLevel(organisation);
		Organisation parentOrg = organisationService.getParentOfLevel(organisation, organisation.getLevel()-1);
		if(parentOrg == null){
			Organisation rootOrg = organisationService.getRootOrganisation();
			if(organisation.equals(rootOrg))
				parentOrg = rootOrg;
		}
		organisationMap.put(parentOrg, new ArrayList<Organisation>());
		organisationMap.get(parentOrg).add(organisation);
		
		for (Organisation org : organisations) {
			//skip "total" organisation
			if(org == organisation) continue;
			
			organisationService.loadParent(org);
			organisationService.loadLevel(org);
			parentOrg = organisationService.getParentOfLevel(org, org.getLevel()-1);			
			
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

	public void setGroupLevel(int groupLevel) {
		this.groupLevel = groupLevel;
	}

}

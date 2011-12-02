package org.chai.kevin;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public class OrganisationService {
	
	private static final Log log = LogFactory.getLog(OrganisationService.class);
	
	private String group;
	private OrganisationUnitService organisationUnitService;
	private OrganisationUnitGroupService organisationUnitGroupService;
	private int facilityLevel;
	
    public Organisation getRootOrganisation() {
		Collection<OrganisationUnit> organisations = organisationUnitService.getRootOrganisationUnits();
		if (organisations.size() != 1) {
			if (log.isErrorEnabled()) log.error("there is no root objective in the system, please create one");
			throw new IllegalStateException("there is no root objective in the system, please create one");
		}
		return createOrganisation(organisations.iterator().next());
    }
    
    // optimization, we cache all the groups
    private GroupCollection groupCollection = null;
    
    // cache all of the levels
    private List<OrganisationUnitLevel> organisationUnitLevels;
    
    private GroupCollection getGroupCollection() {
    	if (groupCollection == null) {
    		groupCollection = new GroupCollection(getGroupsForExpression());
    	}
    	return groupCollection;
    }        
    
	public Set<OrganisationUnitGroup> getGroupsForExpression() {
		Set<OrganisationUnitGroup> result = new HashSet<OrganisationUnitGroup>();
		result.addAll(organisationUnitGroupService.getOrganisationUnitGroupSetByName(group).getOrganisationUnitGroups());
		for (OrganisationUnitGroup organisationUnitGroup : result) {
			for (OrganisationUnit organisationUnit : organisationUnitGroup.getMembers()) {
				// EAGER load		
			}
		}
		return result;
	}
    
    public OrganisationUnitGroup getOrganisationUnitGroup(String uuid) {
    	return getGroupCollection().getGroupByUuid(uuid);
    }
	
	public List<OrganisationUnitLevel> getChildren(int level, Integer... skipLevels) {
		List<Integer> skipLevelList = Arrays.asList(skipLevels);
		List<OrganisationUnitLevel> result = new ArrayList<OrganisationUnitLevel>();
		
		for (OrganisationUnitLevel organisationUnitLevel : organisationUnitService.getOrganisationUnitLevels()) {
			if (organisationUnitLevel.getLevel() > level && !skipLevelList.contains(organisationUnitLevel.getLevel())) {
				result.add(organisationUnitLevel);
			}
		}
		return result;
	}
	
	public List<OrganisationUnitLevel> getAllLevels(Integer... skipLevels) {
		List<Integer> skipLevelList = Arrays.asList(skipLevels);
		if (organisationUnitLevels == null) organisationUnitLevels = organisationUnitService.getOrganisationUnitLevels();

		List<OrganisationUnitLevel> result = new ArrayList<OrganisationUnitLevel>();
		for (OrganisationUnitLevel organisationUnitLevel : organisationUnitLevels) {
			if (!skipLevelList.contains(organisationUnitLevel.getLevel())) {
				result.add(organisationUnitLevel);
			}
		}
		return result;
	}	
	
	public int loadLevel(Organisation organisation) {
		if (organisation.getLevel() != 0) return organisation.getLevel();
		int level = organisationUnitService.getLevelOfOrganisationUnit(organisation.getOrganisationUnit());
		organisation.setLevel(level);
		return level;
	}
	
	public int getNumberOfOrganisationOfGroup(OrganisationUnitGroup organisationUnitGroup){
		return organisationUnitGroup.getMembers().size();
	}
		
	public List<Organisation> getOrganisationsOfLevel(int level) {
		Collection<OrganisationUnit> organisationUnits = organisationUnitService.getOrganisationUnitsAtLevel(level);
		List<Organisation> result = new ArrayList<Organisation>();
		for (OrganisationUnit organisationUnit : organisationUnits) {
			result.add(createOrganisation(organisationUnit));
		}
		return result;
	}
	
	public Integer getNumberOfOrganisationsOfLevel(int level) {
		return getOrganisationsOfLevel(level).size();
	}
	
	public Organisation getOrganisation(int id) {
		OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit(id);
		return createOrganisation(organisationUnit);
	}
	
	private Organisation createOrganisation(OrganisationUnit organisationUnit) {
		if (organisationUnit == null) return null;
		Organisation organisation = new Organisation(organisationUnit);
		return organisation;
	}
	
	public boolean loadParent(Organisation organisation, Integer... skipLevels) {
		if (organisation.getParent() != null) return true;
		OrganisationUnit parent = getParent(organisation.getOrganisationUnit(), skipLevels);
		if (parent == null) return false;
		else {
			organisation.setParent(createOrganisation(parent));
			return true;
		}
	}
	
	private OrganisationUnitGroupSet unitGroupSetCache;
	
	private OrganisationUnitGroupSet getOrganisationUnitGroupSet() {
		if (unitGroupSetCache == null) {
			unitGroupSetCache = organisationUnitGroupService.getOrganisationUnitGroupSetByName(group);
			
			for (@SuppressWarnings("unused") OrganisationUnitGroup group : unitGroupSetCache.getOrganisationUnitGroups()) {
				// load
			}
		}
		return unitGroupSetCache;
	}
		
	public void loadGroup(Organisation organisation) {
		organisation.setOrganisationUnitGroup(organisation.getOrganisationUnit().getGroupInGroupSet(getOrganisationUnitGroupSet()));
	}
	
	public Organisation getOrganisationTreeUntilLevel(int level, Integer... skipLevels) {
		List<Integer> skipLevelList = Arrays.asList(skipLevels);
		Organisation rootOrganisation = getRootOrganisation();
		loadUntilLevel(rootOrganisation, level-skipLevelList.size(), skipLevels);
		return rootOrganisation;
	} 
	
	public void loadUntilLevel(Organisation organisation, int level, Integer... skipLevels) {
		loadLevel(organisation);
		if (organisation.getLevel() < level) {
			loadChildren(organisation, skipLevels);
			for (Organisation child : organisation.getChildren()) {
				loadUntilLevel(child, level, skipLevels);
			}
		}
	}
	
	public void loadChildren(Organisation organisation, Integer... skipLevels) {
		if (organisation.getChildren() != null) return;
		List<Organisation> result = new ArrayList<Organisation>();
		for (OrganisationUnit organisationUnit : getChildren(organisation.getOrganisationUnit(), skipLevels)) {
			Organisation child = createOrganisation(organisationUnit);
			child.setParent(organisation);
			result.add(child);
		}
		organisation.setChildren(result);
	}

	private OrganisationUnit getParent(OrganisationUnit organisationUnit, Integer... skipLevels) {
		List<Integer> skipLevelList = Arrays.asList(skipLevels);
		if (organisationUnit.getParent() == null) return null;
		int level = organisationUnitService.getLevelOfOrganisationUnit(organisationUnit.getParent());
		if (skipLevelList.contains(level)) {
			if (log.isInfoEnabled()) log.info("skipping parent: "+organisationUnit.getParent()+" of level: "+level);
			return getParent(organisationUnit.getParent(), skipLevels);
		}
		return organisationUnit.getParent();
	}
	
	public Organisation getParentOfLevel(Organisation organisation, Integer level) {
		Organisation tmp = organisation;
		this.loadParent(tmp);
		while (tmp.getParent() != null) {
			this.loadLevel(tmp.getParent());
			if (tmp.getParent().getLevel().intValue() == level.intValue())
				return tmp.getParent();
			tmp = tmp.getParent();
			this.loadParent(tmp);
		}
		return null;
	}

	public List<Organisation> getFacilitiesOfGroup(Organisation organisation, OrganisationUnitGroup group) {
		List<Organisation> facilities = getChildrenOfLevel(organisation, getFacilityLevel());
		List<Organisation> result = new ArrayList<Organisation>();
		for (Organisation facility: facilities) {
			loadGroup(facility);
			if (facility.getOrganisationUnitGroup().equals(group)) result.add(facility);
		}
		return result;
	}
	
	public List<Organisation> getChildrenOfLevel(Organisation organisation, int level) {
		List<Organisation> result = new ArrayList<Organisation>();
		collectChildrenOfLevel(organisation, level, result);
		return result;
	}
	
	private void collectChildrenOfLevel(Organisation organisation, int level, List<Organisation> organisations) {
		if (loadLevel(organisation) == level) organisations.add(organisation);
		else {
			loadChildren(organisation);
			for (Organisation child : organisation.getChildren()) {
				collectChildrenOfLevel(child, level, organisations);
			}
		}
	}
	
	private List<OrganisationUnit> getChildren(OrganisationUnit organisation, Integer... skipLevels) {
		List<Integer> skipLevelList = Arrays.asList(skipLevels);
		List<OrganisationUnit> result = new ArrayList<OrganisationUnit>();
		int level = organisationUnitService.getLevelOfOrganisationUnit(organisation);
		for (OrganisationUnit child : organisation.getChildren()) {
			// we optimize by assuming that the level of the children is <level of parent> + 1
			if (skipLevelList.contains(level+1)) {
				if (log.isInfoEnabled()) log.info("skipping child: "+child+" of level: "+level);
				result.addAll(getChildren(child, skipLevels));
			}
			else {
				result.add(child);
			}
		}
		return result;
	}
	
	public int getFacilityLevel() {
		return facilityLevel;
	}
	
	public void setFacilityLevel(int facilityLevel) {
		this.facilityLevel = facilityLevel;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public void setOrganisationUnitGroupService(
			OrganisationUnitGroupService organisationUnitGroupService) {
		this.organisationUnitGroupService = organisationUnitGroupService;
	}
	
	public void setOrganisationUnitService(
			OrganisationUnitService organisationUnitService) {
		this.organisationUnitService = organisationUnitService;
	}

	private static class GroupCollection extends ArrayList<OrganisationUnitGroup> {

		private static final long serialVersionUID = -3757208121878793028L;
		
		private Map<String, OrganisationUnitGroup> groupsByUuid;

		public GroupCollection(Collection<OrganisationUnitGroup> groups) {
			super(groups);
			this.groupsByUuid = new HashMap<String, OrganisationUnitGroup>();
			for (OrganisationUnitGroup group : groups) {
				this.groupsByUuid.put(group.getUuid(), group);
			}
		}
		
		public OrganisationUnitGroup getGroupByUuid(String uuid) {
			return groupsByUuid.get(uuid);
		}
		
	}

}

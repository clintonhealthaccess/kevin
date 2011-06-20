package org.chai.kevin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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
	
	public List<OrganisationUnitGroup> getGroupsForExpression() {
		List<OrganisationUnitGroup> result = new ArrayList<OrganisationUnitGroup>();
		result.addAll(organisationUnitGroupService.getOrganisationUnitGroupSetByName(group).getOrganisationUnitGroups());
		return result;
	}
	
//	public List<OrganisationUnitGroup> getGroupsForExpression(Organisation organisation) {
//		List<OrganisationUnitGroup> result = new ArrayList<OrganisationUnitGroup>();
//		List<OrganisationUnitGroupSet> groupSets = new ArrayList<OrganisationUnitGroupSet>();
//		for (String name : groups) {
//			groupSets.add(organisationUnitGroupService.getOrganisationUnitGroupSetByName(name));
//		}
//		for (OrganisationUnitGroupSet organisationUnitGroupSet : groupSets) {
//			OrganisationUnitGroup group = organisation.getOrganisationUnit().getGroupInGroupSet(organisationUnitGroupSet);
//			if (group != null) result.add(group);
//		}
//		return result;
//	}
	
//	public boolean isAtFacilityLevel(Organisation organisation) {
//		return getLevel(organisation) == facilityLevel;
//	}
	
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
		List<OrganisationUnitLevel> result = new ArrayList<OrganisationUnitLevel>();
		
		for (OrganisationUnitLevel organisationUnitLevel : organisationUnitService.getOrganisationUnitLevels()) {
			if (!skipLevelList.contains(organisationUnitLevel.getLevel())) {
				result.add(organisationUnitLevel);
			}
		}
		return result;
	}
	
	public int getLevel(Organisation organisation) {
		if (organisation.getLevel() != 0) return organisation.getLevel();
		int level = organisationUnitService.getLevelOfOrganisationUnit(organisation.getOrganisationUnit());
		organisation.setLevel(level);
		return level;
	}
	
	public List<Organisation> getOrganisationsOfLevel(int level) {
		Collection<OrganisationUnit> organisationUnits = organisationUnitService.getOrganisationUnitsAtLevel(level);
		List<Organisation> result = new ArrayList<Organisation>();
		for (OrganisationUnit organisationUnit : organisationUnits) {
			result.add(createOrganisation(organisationUnit));
		}
		return result;
	}
	
//	public OrganisationUnitGroup getGroupByName(String name) {
//		return organisationUnitGroupService.getOrganisationUnitGroupByName(name);
//	}
	
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
	
	public void loadGroup(Organisation organisation) {
//		loadGroups();
		organisation.setOrganisationUnitGroup(organisation.getOrganisationUnit().getGroupInGroupSet(organisationUnitGroupService.getOrganisationUnitGroupSetByName(group)));
	}
	
	public Organisation getOrganisationTreeUntilLevel(int level, Integer... skipLevels) {
		List<Integer> skipLevelList = Arrays.asList(skipLevels);
		Organisation rootOrganisation = getRootOrganisation();
		loadUntilLevel(rootOrganisation, level-skipLevelList.size(), skipLevels);
		return rootOrganisation;
	} 
	
	private void loadUntilLevel(Organisation organisation, int level, Integer... skipLevels) {
		getLevel(organisation);
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
	
	public List<Organisation> getChildrenOfLevel(Organisation organisation, int level) {
		List<OrganisationUnit> children = getChildrenOfLevel(organisation.getOrganisationUnit(), level);
		List<Organisation> result = new ArrayList<Organisation>();
		for (OrganisationUnit child : children) {
			result.add(createOrganisation(child));
		}
		return result;
	}
	
	private List<OrganisationUnit> getChildrenOfLevel(OrganisationUnit organisation, final int level) {
		List<OrganisationUnit> result = new ArrayList<OrganisationUnit>();
		if (organisationUnitService.getLevelOfOrganisationUnit(organisation) == level) {
			result.add(organisation);
		}
		else {
			int childLevel = organisationUnitService.getLevelOfOrganisationUnit(organisation);
			// we optimize by assuming that the level of the children is <level of parent> + 1
			for (OrganisationUnit child : organisation.getChildren()) {
				if (level == childLevel+1) {
					result.add(child);
				}
				else {
					result.addAll(getChildrenOfLevel(child, level));
				}
			}
		}
		return result;
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
	
//	public List<String> getSkipLevels() {
//		return Arrays.asList(StringUtils.split(skipLevels, ','));
//	}
//	
//	public void setSkipLevels(String skipLevels) {
//		this.skipLevels = skipLevels;
//	}
	
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

	
}

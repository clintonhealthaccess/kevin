package org.chai.kevin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
	
	private Set<Integer> skipLevels;
	private Set<String> groups;
	 
	private OrganisationUnitService organisationUnitService;
	private OrganisationUnitGroupService organisationUnitGroupService;
	
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
		Collection<OrganisationUnitGroupSet> groupSets = organisationUnitGroupService.getAllOrganisationUnitGroupSets();
		for (OrganisationUnitGroupSet groupSet : groupSets) {
			if (groups.contains(groupSet.getName())) {
				result.addAll(groupSet.getOrganisationUnitGroups());
			}
		}
		return result;
	}
	
	public List<OrganisationUnitLevel> getChildren(OrganisationUnitLevel level) {
		List<OrganisationUnitLevel> result = new ArrayList<OrganisationUnitLevel>();
		
		for (OrganisationUnitLevel organisationUnitLevel : organisationUnitService.getOrganisationUnitLevels()) {
			if (organisationUnitLevel.getLevel() > level.getLevel() && !skipLevels.contains(organisationUnitLevel.getLevel())) {
				result.add(organisationUnitLevel);
			}
		}
		return result;
	}
	
	public OrganisationUnitLevel getLevel(Organisation organisation) {
		int level = organisationUnitService.getLevelOfOrganisationUnit(organisation.getOrganisationUnit());
		OrganisationUnitLevel organisationUnitLevel = organisationUnitService.getOrganisationUnitLevelByLevel(level);
		organisation.setLevel(organisationUnitLevel);
		return organisationUnitLevel;
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
	
	public boolean loadParent(Organisation organisation) {
		if (organisation.getParent() != null) return true;
		OrganisationUnit parent = getParent(organisation.getOrganisationUnit());
		if (parent == null) return false;
		else {
			organisation.setParent(createOrganisation(parent));
			return true;
		}
	}
	
	public Organisation getOrganisationTreeUntilLevel(int level) {
		Organisation rootOrganisation = getRootOrganisation();
		loadUntilLevel(rootOrganisation, level-skipLevels.size());
		return rootOrganisation;
	} 
	
	private void loadUntilLevel(Organisation organisation, int level) {
		getLevel(organisation);
		if (organisation.getLevel().getLevel() < level) {
			loadChildren(organisation);
			for (Organisation child : organisation.getChildren()) {
				loadUntilLevel(child, level);
			}
		}
	}

	
	public void loadChildren(Organisation organisation) {
		if (organisation.getChildren() != null) return;
		List<Organisation> result = new ArrayList<Organisation>();
		for (OrganisationUnit organisationUnit : getChildren(organisation.getOrganisationUnit())) {
			Organisation child = createOrganisation(organisationUnit);
			child.setParent(organisation);
			result.add(child);
		}
		organisation.setChildren(result);
	}

	public OrganisationUnit getParent(OrganisationUnit organisationUnit) {
		if (organisationUnit.getParent() == null) return null;
		int level = organisationUnitService.getLevelOfOrganisationUnit(organisationUnit.getParent());
		if (skipLevels.contains(level)) {
			if (log.isInfoEnabled()) log.info("skipping parent: "+organisationUnit.getParent()+" of level: "+level);
			return getParent(organisationUnit.getParent());
		}
		return organisationUnit.getParent();
	}
	
	public List<Organisation> getChildrenOfLevel(Organisation organisation, OrganisationUnitLevel level) {
		List<OrganisationUnit> children = getChildrenOfLevel(organisation.getOrganisationUnit(), level.getLevel());
		List<Organisation> result = new ArrayList<Organisation>();
		for (OrganisationUnit child : children) {
			result.add(createOrganisation(child));
		}
		return result;
	}
	
	private List<OrganisationUnit> getChildrenOfLevel(OrganisationUnit organisation, int level) {
		List<OrganisationUnit> result = new ArrayList<OrganisationUnit>();
		for (OrganisationUnit child : organisation.getChildren()) {
			int childLevel = organisationUnitService.getLevelOfOrganisationUnit(child);
			if (level == childLevel) {
				result.add(child);
			}
			else {
				result.addAll(getChildrenOfLevel(child, level));
			}
		}
		return result;
	}
	
	private List<OrganisationUnit> getChildren(OrganisationUnit organisation) {
		List<OrganisationUnit> result = new ArrayList<OrganisationUnit>();
		for (OrganisationUnit child : organisation.getChildren()) {
			int level = organisationUnitService.getLevelOfOrganisationUnit(child);
			if (skipLevels.contains(level)) {
				if (log.isInfoEnabled()) log.info("skipping child: "+child+" of level: "+level);
				result.addAll(getChildren(child));
			}
			else {
				result.add(child);
			}
		}
		return result;
	}
	
	public void setSkipLevels(Set<Integer> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public void setGroups(Set<String> groups) {
		this.groups = groups;
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

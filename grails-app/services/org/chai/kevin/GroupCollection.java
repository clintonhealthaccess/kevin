package org.chai.kevin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

public class GroupCollection extends ArrayList<OrganisationUnitGroup> {

	private static final long serialVersionUID = -3757208121878793028L;
	
	private Map<String, OrganisationUnitGroup> groupsByUuid;

	public GroupCollection(Collection<OrganisationUnitGroup> groups) {
		super(groups);
		this.groupsByUuid = new HashMap<String, OrganisationUnitGroup>();
		for (OrganisationUnitGroup group : groups) {
			this.groupsByUuid.put(group.getUuid(), group);
		}
	}
	
	public Map<String, OrganisationUnitGroup> getGroups() {
		return groupsByUuid;
	}
	
	public OrganisationUnitGroup getGroupByUuid(String uuid) {
		return groupsByUuid.get(uuid);
	}
	
}

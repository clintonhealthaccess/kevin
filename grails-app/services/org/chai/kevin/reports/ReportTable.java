package org.chai.kevin.reports;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.Organisation;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

public abstract class ReportTable<T> {

	protected List<T> targets;
	protected Map<Organisation, Map<T, Report>> values;
	protected Map<Organisation, List<Organisation>> organisationMap;
	
	public List<Organisation> getOrganisations() {
		return new ArrayList<Organisation>(organisationMap.keySet());
	}
	
	public List<T> getTargets(){
		return targets;
	}
	
	public Report getReport(Organisation organisation, T target){
		return values.get(organisation).get(target);
	}
	
	public Map<Organisation, List<Organisation>> getOrganisationMap() {
		return organisationMap;
	}
	
	public Set<OrganisationUnitGroup> getFacilityTypes() {
		Set<OrganisationUnitGroup> facilityTypes = new HashSet<OrganisationUnitGroup>();
		for (List<Organisation> organisations : getOrganisationMap().values()) {
			for(Organisation organisation : organisations){
				OrganisationUnitGroup facilityType = organisation.getOrganisationUnitGroup();
				if(facilityType != null) facilityTypes.add(facilityType);	
			}
		}
		return facilityTypes;
	}


}

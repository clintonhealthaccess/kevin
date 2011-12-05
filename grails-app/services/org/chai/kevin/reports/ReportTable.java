package org.chai.kevin.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chai.kevin.Organisation;

public abstract class ReportTable<T> {

	protected Map<Organisation, Map<T, ReportValue>> valueMap;
	protected List<T> targets;
	protected Map<Organisation, List<Organisation>> organisationMap;
	
	public List<Organisation> getOrganisations() {
		return new ArrayList<Organisation>(organisationMap.keySet());
	}
	
	public List<T> getTargets(){
		return targets;
	}
	
	public ReportValue getReportValue(Organisation organisation, T target){
		return valueMap.get(organisation).get(target);
	}
	
	public Map<Organisation, List<Organisation>> getOrganisationMap() {
		return organisationMap;
	}

}

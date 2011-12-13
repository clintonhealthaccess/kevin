package org.chai.kevin.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.LocationEntity;

public abstract class ReportTable<T, S extends CalculationEntity> {

	protected Map<S, Map<T, ReportValue>> valueMap;
	protected List<T> targets;
	protected Map<LocationEntity, List<S>> organisationMap;
	
	public ReportTable(Map<S, Map<T, ReportValue>> valueMap, List<T> targets,
			Map<LocationEntity, List<S>> organisationMap) {
		this.valueMap = valueMap;
		this.targets = targets;
		this.organisationMap = organisationMap;
	}

	public List<CalculationEntity> getOrganisations() {
		return new ArrayList<CalculationEntity>(organisationMap.keySet());
	}
	
	public List<T> getTargets(){
		return targets;
	}
	
	public ReportValue getReportValue(CalculationEntity organisation, T target){
		return valueMap.get(organisation).get(target);
	}
	
	public Map<LocationEntity, List<S>> getOrganisationMap() {
		return organisationMap;
	}

}

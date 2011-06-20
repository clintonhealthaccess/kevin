package org.chai.kevin.maps;

import org.chai.kevin.Organisation;
import org.hisp.dhis.period.Period;

public class MapsExplanation {

	private Organisation organisation;
	private MapsTarget mapsTarget;
	private Period period;
	private Double value;
	
	
	public MapsExplanation(Organisation organisation, MapsTarget mapsTarget,
			Period period, Double value) {
		super();
		this.organisation = organisation;
		this.mapsTarget = mapsTarget;
		this.period = period;
		this.value = value;
	}

	
	public Organisation getOrganisation() {
		return organisation;
	}
	
	public MapsTarget getMapsTarget() {
		return mapsTarget;
	}
	
	public Period getPeriod() {
		return period;
	}
	
	public Double getValue() {
		return value;
	}
	
}

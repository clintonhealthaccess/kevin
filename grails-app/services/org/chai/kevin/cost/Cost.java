package org.chai.kevin.cost;

import java.util.Collection;

import org.chai.kevin.Organisation;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public class Cost {

	private Double value;
	private CostTarget target;
	private Integer year;
	private Period period;
	private Organisation organisation;
	
	private boolean hasMissingValue;

	public Cost(Double value, CostTarget target, Integer year, Period period,
			Organisation organisation, boolean hasMissingValue) {
		super();
		this.value = value;
		this.target = target;
		this.year = year;
		this.period = period;
		this.organisation = organisation;
		this.hasMissingValue = hasMissingValue;
	}
	
	protected Cost(CostTarget target, Integer year, Period period,
			Organisation organisation) {
		super();
		this.value = 0d;
		this.target = target;
		this.year = year;
		this.period = period;
		this.organisation = organisation;
		this.hasMissingValue = false;
	}

	public Double getValue() {
		return value;
	}
	
	public Integer getRoundedValue() {
		return value.intValue();
	}
	
	public boolean isHasMissingValue() {
		return hasMissingValue;
	}
	
	protected void addValue(Double value) {
		this.value += value;
	}
	
	protected void hasMissingValue() {
		this.hasMissingValue = true;
	}
	
}

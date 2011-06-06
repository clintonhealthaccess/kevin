package org.chai.kevin.dsr;

import org.chai.kevin.Organisation;
import org.hisp.dhis.period.Period;

public class Dsr {
	private Organisation organisation;
	private Period period;
	private DsrTarget target;
	private Object value;

	public Dsr(Organisation organisation, Period period, DsrTarget target,Object value) {
		this.organisation = organisation;
		this.period = period;
		this.setTarget(target);
		this.setValue(value);
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setTarget(DsrTarget target) {
		this.target = target;
	}

	public DsrTarget getTarget() {
		return target;
	}


}

package org.chai.kevin.dashboard;

import org.chai.kevin.Info;
import org.chai.kevin.Organisation;

public class DashboardExplanation {

	private Info info;
	private DashboardEntry entry;
	private Organisation organisation;
	
	public DashboardExplanation(Info info, DashboardEntry entry, Organisation organisation) {
		super();
		this.info = info;
		this.entry = entry;
		this.organisation = organisation;
	}
	
	public DashboardEntry getEntry() {
		return entry;
	}

	public Organisation getOrganisation() {
		return organisation;
	}
	
	public Info getInfo() {
		return info;
	}
	
}

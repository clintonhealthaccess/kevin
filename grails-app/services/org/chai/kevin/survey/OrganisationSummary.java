package org.chai.kevin.survey;

import org.chai.kevin.Organisation;

public class OrganisationSummary implements Comparable<OrganisationSummary> {
	
	private Organisation organisation;
	
	private int objectives;
	private int submittedObjectives;
	private int elements;
	private int completedElements;

	
	public OrganisationSummary(Organisation organisation, 
			Integer submittedObjectives, Integer objectives,
			Integer completedElements, Integer elements) {
		super();
		this.organisation = organisation;
		this.objectives = objectives;
		this.submittedObjectives = submittedObjectives;
		this.elements = elements;
		this.completedElements = completedElements;
	}

	public Organisation getOrganisation() {
		return organisation;
	}
	
	public Integer getObjectives() {
		return objectives;
	}
	
	public Integer getSubmittedObjectives() {
		return submittedObjectives;
	}
	
	public Integer getElements() {
		return elements;
	}
	
	public Integer getCompletedElements() {
		return completedElements;
	}

	@Override
	public int compareTo(OrganisationSummary o) {
		if (elements == 0 && o.getElements() == 0) return 0;
		if (elements == 0) return -1;
		if (o.getElements() == 0) return 1;
		
		double d0 = (double)completedElements/(double)elements;
		double d1 = (double)o.getCompletedElements()/(double)o.getElements();
		
		if (d0 > d1) return 1;
		else if (d1 > d0) return -1;
		return 0;
	}
	
}
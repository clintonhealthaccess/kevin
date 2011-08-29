package org.chai.kevin.survey;

public class SectionSummary implements Comparable<SectionSummary> {

	private SurveySection section;
	
	private int elements;
	private int submittedElements;
	
	public SectionSummary(SurveySection section, Integer elements, Integer submittedElements) {
		super();
		this.section = section;
		this.elements = elements;
		this.submittedElements = submittedElements;
	}

	public SurveySection getSection() {
		return section;
	}
	
	public Integer getElements() {
		return elements;
	}
	
	public Integer getSubmittedElements() {
		return submittedElements;
	}
	
	@Override
	public int compareTo(SectionSummary o) {
		if (elements == 0 && o.getElements() == 0) return 0;
		if (elements == 0) return -1;
		if (o.getElements() == 0) return 1;
		
		double d0 = (double)submittedElements/(double)elements;
		double d1 = (double)o.getSubmittedElements()/(double)o.getElements();
		
		if (d0 > d1) return 1;
		else if (d1 > d0) return -1;
		return 0;
	}
}

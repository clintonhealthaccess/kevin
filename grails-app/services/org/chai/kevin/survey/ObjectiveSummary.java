package org.chai.kevin.survey;

import java.util.List;
import java.util.Map;

import org.chai.kevin.Organisation;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;

public class ObjectiveSummary implements Comparable<ObjectiveSummary> {

	private SurveyObjective objective;
	private SurveyEnteredObjective enteredObjective;
	
	private int elements;
	private int submittedElements;
	
	public ObjectiveSummary(SurveyObjective objective,
			SurveyEnteredObjective enteredObjective, Integer elements,
			Integer submittedElements) {
		super();
		this.objective = objective;
		this.enteredObjective = enteredObjective;
		this.elements = elements;
		this.submittedElements = submittedElements;
	}

	public SurveyObjective getObjective() {
		return objective;
	}
	
	public SurveyEnteredObjective getEnteredObjective() {
		return enteredObjective;
	}
	
	public Integer getElements() {
		return elements;
	}
	
	public Integer getSubmittedElements() {
		return submittedElements;
	}

	@Override
	public int compareTo(ObjectiveSummary o) {
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

package org.chai.kevin.survey.summary;

public class ProgramSummary {

	private int programs;
	private int submittedPrograms;
	
	public ProgramSummary(int programs, int submittedPrograms) {
		super();
		this.programs = programs;
		this.submittedPrograms = submittedPrograms;
	}

	public Integer getPrograms() {
		return programs;
	}
	
	public Integer getSubmittedPrograms() {
		return submittedPrograms;
	}
	
}

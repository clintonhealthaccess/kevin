package org.chai.kevin.reports;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ReportTarget extends ReportEntity {

	private ReportProgram program;

	@ManyToOne(targetEntity=ReportProgram.class)
	public ReportProgram getProgram() {
		return program;
	}

	public void setProgram(ReportProgram program) {
		this.program = program;
	}
}

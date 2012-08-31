package org.chai.kevin.reports;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractReportTarget extends ReportEntity implements ReportTarget {

//	private ReportProgram program;
//
//	/* (non-Javadoc)
//	 * @see org.chai.kevin.reports.IReportTarget#getProgram()
//	 */
//	@Override
//	@ManyToOne(targetEntity=ReportProgram.class)
//	public ReportProgram getProgram() {
//		return program;
//	}
//
//	public void setProgram(ReportProgram program) {
//		this.program = program;
//	}
	
}

package org.chai.kevin.reports;

import javax.persistence.ManyToOne;

public interface ReportTarget {

	public abstract ReportProgram getProgram();

}
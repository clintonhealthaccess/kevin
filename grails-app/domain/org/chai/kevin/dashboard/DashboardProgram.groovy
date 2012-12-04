package org.chai.kevin.dashboard;

import org.chai.kevin.Exportable
import org.chai.kevin.Period
import org.chai.kevin.reports.ReportEntity
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.util.Utils
import org.chai.location.CalculationLocation

class DashboardProgram extends ReportEntity implements DashboardEntity, Exportable {

	ReportProgram program;
	Integer weight;

	static mapping = {
		table 'dhsst_dashboard_program'
		program column: 'program'
	}
	
	static constraints =  {
		weight (nullable: false)
		program (nullable: false)
	}
	
	@Override
	public <T> T visit(DashboardVisitor<T> visitor, CalculationLocation location, Period period) {
		return visitor.visitProgram(this, location, period);
	}

	@Override
	public boolean hasChildren() {				
		return true;
	}
	
	@Override
	public boolean isTarget() {
		return false;
	}
	
	@Override
	public ReportProgram getReportProgram() {
		return program;	
	}
	
	@Override
	public String toString() {
		return "DashboardProgram[getId()=" + getId() + ", getCode()=" + getCode() + "]";
	}

}
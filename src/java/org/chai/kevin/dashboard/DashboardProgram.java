package org.chai.kevin.dashboard;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Period;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.reports.ReportProgram;

@Entity(name="DashboardProgram")
@Table(name="dhsst_dashboard_program")
public class DashboardProgram extends DashboardEntity {

	private Long id;
	private ReportProgram program;

	@Override
	@Transient
	public ReportProgram getReportProgram() {
		return getProgram();
	}
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToOne(targetEntity=ReportProgram.class)
	public ReportProgram getProgram() {
		return program;
	}
	
	public void setProgram(ReportProgram program) {
		this.program = program;
	}

	@Override
	public <T> T visit(DashboardVisitor<T> visitor, CalculationEntity entity, Period period) {
		return visitor.visitProgram(this, entity, period);
	}

	@Override
	@Transient
	public boolean hasChildren() {				
		return true;
	}
	
	@Override
	@Transient
	public boolean isTarget() {
		return false;
	}	
	
}

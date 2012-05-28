package org.chai.kevin.dashboard;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.entity.export.Exportable;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.Period;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="DashboardProgram")
@Table(name="dhsst_dashboard_program", uniqueConstraints = {
	@UniqueConstraint(columnNames={"program"})
})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
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
	public <T> T visit(DashboardVisitor<T> visitor, CalculationLocation location, Period period) {
		return visitor.visitProgram(this, location, period);
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
	
	@Override
	public String toString() {
		return "DashboardProgram[getId()=" + getId() + ", getCode()=" + getCode() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}
	
}

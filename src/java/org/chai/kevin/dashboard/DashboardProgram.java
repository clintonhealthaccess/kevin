package org.chai.kevin.dashboard;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Exportable;
import org.chai.kevin.Period;
import org.chai.location.CalculationLocation;
import org.chai.kevin.reports.ReportEntity;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="DashboardProgram")
@Table(name="dhsst_dashboard_program", uniqueConstraints = {
	@UniqueConstraint(columnNames={"program"}),
	@UniqueConstraint(columnNames={"code"})
})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class DashboardProgram extends ReportEntity implements DashboardEntity, Exportable {

	private Long id;
	private ReportProgram program;
	protected Integer weight;

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

	@Basic
	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}


}
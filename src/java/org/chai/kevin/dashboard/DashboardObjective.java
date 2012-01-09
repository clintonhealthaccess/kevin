package org.chai.kevin.dashboard;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Organisation;
import org.chai.kevin.reports.ReportObjective;
import org.chai.kevin.reports.ReportService;
import org.hisp.dhis.period.Period;

@Entity(name="DashboardObjective")
@Table(name="dhsst_dashboard_objective")
public class DashboardObjective extends DashboardEntity {

	private Long id;
	private ReportObjective objective;
	
	private ReportService reportService;
	
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	@Override
	@Transient
	public ReportObjective getReportObjective() {
		ReportObjective reportObjective = getObjective();
		return reportObjective;
	}
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToOne(targetEntity=ReportObjective.class)
	public ReportObjective getObjective() {
		return objective;
	}
	
	public void setObjective(ReportObjective objective) {
		this.objective = objective;
	}
	
	@Override
	public <T> T visit(DashboardVisitor<T> visitor, Organisation organisation, Period period) {
		return visitor.visitObjective(this, organisation, period);
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

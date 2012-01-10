package org.chai.kevin.reports;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ReportTarget extends ReportEntity {

	private ReportObjective objective;

	@ManyToOne(targetEntity=ReportObjective.class)
	public ReportObjective getObjective() {
		return objective;
	}

	public void setObjective(ReportObjective objective) {
		this.objective = objective;
	}
}

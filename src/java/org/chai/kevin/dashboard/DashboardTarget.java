package org.chai.kevin.dashboard;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Calculation;
import org.chai.kevin.Info;
import org.chai.kevin.Organisation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ManyToAny;
import org.hisp.dhis.period.Period;

@Entity(name="StrategicTarget")
@Table(name="dhsst_dashboard_target")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DashboardTarget extends DashboardEntry {

	private Calculation calculation;
	
	
//	@Cascade(value={CascadeType.ALL, CascadeType.DELETE_ORPHAN})
	@ManyToOne(targetEntity=Calculation.class, optional=false)
	@JoinColumn(nullable=false)
	public Calculation getCalculation() {
		return calculation;
	}
	
	public void setCalculation(Calculation calculation) {
		this.calculation = calculation;
	}
	
	@Override
	public DashboardExplanation getExplanation(ExplanationCalculator calculator, Organisation organisation, Period period, boolean isFacility) {
		if (isFacility) return calculator.explainLeafTarget(this, organisation, period);
		else return calculator.explainNonLeafTarget(this, organisation, period);
	}	

	@Override
	public DashboardPercentage getValue(PercentageCalculator calculator, Organisation organisation, Period period, boolean isFacility) {
		if (isFacility) return calculator.getPercentageForLeafTarget(this, organisation, period);
		else return calculator.getPercentageForNonLeafTarget(this, organisation, period);
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public String toString() {
		return "StrategicTarget [code=" + code + ", calculation=" + calculation + "]";
	}

	@Override
	@Transient
	public boolean isTarget() {
		return true;
	}

}

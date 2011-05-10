
package org.chai.kevin.dashboard;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Objective;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

@Entity(name="Percentage")
@Table(name="dhsst_dashboard_percentage",
	uniqueConstraints = {
		@UniqueConstraint(columnNames={"organisationUnit", "entry", "period"})
	}
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DashboardPercentage {
	

	private Integer id;
	
	private Double value;	

	// either it has a status (if child) or a list of percentages
	private Status status;
	// private List<Percentage> percentages;

	private OrganisationUnit organisationUnit;
	private DashboardEntry entry;
	private Period period;
	
	private Boolean hasMissingValue;
	private Boolean hasMissingExpression;
	
	public enum Status {
		VALID,
		MISSING_EXPRESSION,
		MISSING_VALUE
	}
	
//	public static final Percentage MISSING_VALUE = new Percentage(Status.MISSING_VALUE);
//	public static final Percentage MISSING_INDICATOR = new Percentage(Status.MISSING_INDICATOR);
	
	public DashboardPercentage() {}
	
	public DashboardPercentage(Status status, OrganisationUnit organisationUnit, DashboardEntry entry, Period period) {
		this.value = null;
		this.status = status;

		this.organisationUnit = organisationUnit;
		this.entry = entry;
		this.period = period;
		
		this.hasMissingValue = status == Status.MISSING_VALUE;
		this.hasMissingExpression = status == Status.MISSING_EXPRESSION;
//		this.percentages = null;
	}
	
	public DashboardPercentage(Double value, OrganisationUnit organisationUnit, DashboardEntry entry, Period period) {
		assert value != null;
		
		this.value = value;
		this.status = Status.VALID;

		this.organisationUnit = organisationUnit;
		this.entry = entry;
		this.period = period;
		
		this.hasMissingValue = false;
		this.hasMissingExpression = false;
//		this.percentages = null;
	}
	
	public DashboardPercentage(Double value, OrganisationUnit organisationUnit, DashboardEntry entry, Period period, Boolean hasMissingValueStatus, Boolean hasMissingExpressionStatus) {
		assert value != null;
		
		this.value = value;
		this.status = Status.VALID;

		this.organisationUnit = organisationUnit;
		this.entry = entry;
		this.period = period;
		
		this.hasMissingValue = hasMissingValueStatus;
		this.hasMissingExpression = hasMissingExpressionStatus;
//		this.percentages = null;
	}
	
//	public Percentage(Double value, List<Percentage> percentages, Organisation organisation, Objective objective, Period period) {
//		this.value = value;
//		this.status = null;
//		
//		this.organisation = organisation;
//		this.objective = objective;
//		this.period = period;
//		
//		this.percentages = percentages;
//	}
	
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	@ManyToOne(targetEntity=DashboardEntry.class, optional=false)
	public DashboardEntry getEntry() {
		return entry;
	}
	
	@ManyToOne(targetEntity=Period.class, optional=false)
	public Period getPeriod() {
		return period;
	}
	
	@ManyToOne(targetEntity=OrganisationUnit.class, optional=false)
	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public Status getStatus() {
		return status;
	}
	
	@Basic(optional=true)
	@Column(nullable=true)
	public Double getValue() {
		return value;
	}
	
	@Basic(optional=false)
	@Column(nullable=false)
	public Boolean isHasMissingExpression() {
		return hasMissingExpression;
	}
	
	@Basic(optional=false)
	@Column(nullable=false)
	public Boolean isHasMissingValue() {
		return hasMissingValue;
	}

	
	@Transient
	public Integer getRoundedValue() {
		return new Double(value * 100).intValue();
	}
	
	@Transient
	public Integer getValueClass() {
		return new Double(value / 0.2d).intValue();
	}

	@Transient
	public boolean isValidPercentage() {
		return value != null && !value.isNaN() && value != -1;
	}
	
//	@Transient
//	public boolean isLeaf() {
//		return organisation.getChildren().size() == 0;
//	}

	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setPeriod(Period period) {
		this.period = period;
	}
	
	public void setEntry(DashboardEntry entry) {
		this.entry = entry;
	}
	
	public void setOrganisationUnit(OrganisationUnit organisationUnit) {
		this.organisationUnit = organisationUnit;
	}
	
	public void setHasMissingExpression(boolean hasMissingExpressionStatus) {
		this.hasMissingExpression = hasMissingExpressionStatus;
	}
	
	public void setHasMissingValue(boolean hasMissingValueStatus) {
		this.hasMissingValue = hasMissingValueStatus;
	}
	
//	private boolean hasStatus(Status status) {
//		if (percentages != null) {
//			for (Percentage percentage : percentages) {
//				if (percentage.hasStatus(status)) return true;
//			}
//			return false;
//		}
//		else return this.status == status;
//	}
	
	@Override
	public String toString() {
		return "Percentage [id=" + id + ", value=" + value + ", status="
				+ status + ", organisationUnit=" + organisationUnit
				+ ", objective=" + entry + ", period=" + period + "]";
	}
	
}
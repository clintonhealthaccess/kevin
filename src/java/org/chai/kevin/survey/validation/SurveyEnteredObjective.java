package org.chai.kevin.survey.validation;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.survey.SurveyObjective;
import org.hibernate.annotations.NaturalId;
import org.hisp.dhis.organisationunit.OrganisationUnit;

@Entity(name="SurveyValidObjective")
@Table(name="dhsst_survey_valid_objective", uniqueConstraints=@UniqueConstraint(
		columnNames={"objective", "organisationUnit"})
)
public class SurveyEnteredObjective {

	public static enum ObjectiveStatus {UNAVAILABLE, CLOSED, INVALID, COMPLETE, INCOMPLETE}
	
	private Long id;
	private SurveyObjective objective;
	private OrganisationUnit organisationUnit;
	private ObjectiveStatus status;
	
	public SurveyEnteredObjective() {}
	
	public SurveyEnteredObjective(SurveyObjective objective, OrganisationUnit organisationUnit) {
		super();
		this.objective = objective;
		this.organisationUnit = organisationUnit;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@NaturalId
	@ManyToOne(targetEntity=SurveyObjective.class, optional=false)
	public SurveyObjective getObjective() {
		return objective;
	}
	
	public void setObjective(SurveyObjective objective) {
		this.objective = objective;
	}

	@NaturalId
	@ManyToOne(targetEntity=OrganisationUnit.class, optional=false)
	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
	}
	
	public void setOrganisationUnit(OrganisationUnit organisationUnit) {
		this.organisationUnit = organisationUnit;
	}
	
	@Basic
	@Enumerated(EnumType.STRING)
	public ObjectiveStatus getStatus() {
		return status;
	}
	
	public void setStatus(ObjectiveStatus status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((objective == null) ? 0 : objective.hashCode());
		result = prime
				* result
				+ ((organisationUnit == null) ? 0 : organisationUnit.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SurveyEnteredObjective other = (SurveyEnteredObjective) obj;
		if (objective == null) {
			if (other.objective != null)
				return false;
		} else if (!objective.equals(other.objective))
			return false;
		if (organisationUnit == null) {
			if (other.organisationUnit != null)
				return false;
		} else if (!organisationUnit.equals(other.organisationUnit))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SurveyEnteredObjective [objective=" + objective
				+ ", organisationUnit=" + organisationUnit + ", status="
				+ status + "]";
	}
	
}

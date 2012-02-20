package org.chai.kevin.survey.validation;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.survey.SurveyObjective;
import org.hibernate.annotations.NaturalId;

@Entity(name="SurveyValidObjective")
@Table(name="dhsst_survey_entered_objective", uniqueConstraints=@UniqueConstraint(
		columnNames={"objective", "entity"})
)
public class SurveyEnteredObjective extends SurveyEnteredEntity {

	private Long id;
	private SurveyObjective objective;
	private DataLocationEntity entity;
	
	private Boolean complete;
	private Boolean invalid;
	private Boolean closed;
	
	public SurveyEnteredObjective() {}
	
	public SurveyEnteredObjective(SurveyObjective objective, DataLocationEntity entity, Boolean invalid, Boolean complete, Boolean closed) {
		super();
		this.objective = objective;
		this.entity = entity;
		this.complete = complete;
		this.invalid = invalid;
		this.closed = closed;
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
	@ManyToOne(targetEntity=SurveyObjective.class, fetch=FetchType.LAZY)
	public SurveyObjective getObjective() {
		return objective;
	}
	
	public void setObjective(SurveyObjective objective) {
		this.objective = objective;
	}

	@NaturalId
	@ManyToOne(targetEntity=DataLocationEntity.class, fetch=FetchType.LAZY)
	public DataLocationEntity getEntity() {
		return entity;
	}
	
	public void setEntity(DataLocationEntity entity) {
		this.entity = entity;
	}
	
	@Basic
	public Boolean isInvalid() {
		return invalid;
	}
	
	public void setInvalid(Boolean invalid) {
		this.invalid = invalid;
	}
	
	@Basic
	public Boolean isComplete() {
		return complete;
	}
	
	public void setComplete(Boolean complete) {
		this.complete = complete;
	}
	
	@Basic
	public Boolean isClosed() {
		return closed;
	}
	
	public void setClosed(Boolean closed) {
		this.closed = closed;
	}
	
	@Transient
	public String getDisplayedStatus() {
		String status = null;
		if (closed) status = "closed";
		else if (invalid) status = "invalid";
		else if (!complete) status = "incomplete";
		else status = "complete";
		return status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((objective == null) ? 0 : objective.hashCode());
		result = prime
				* result
				+ ((entity == null) ? 0 : entity.hashCode());
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
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SurveyEnteredObjective [complete=" + complete + ", invalid=" + invalid + ", closed=" + closed + "]";
	}
	
}

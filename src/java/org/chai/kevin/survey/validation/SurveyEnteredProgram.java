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

import org.chai.kevin.form.EnteredEntity;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.survey.SurveyProgram;
import org.hibernate.annotations.NaturalId;

@Entity(name="SurveyValidProgram")
@Table(name="dhsst_survey_entered_program", uniqueConstraints=@UniqueConstraint(
		columnNames={"program", "entity"})
)
public class SurveyEnteredProgram extends EnteredEntity {

	private Long id;
	private SurveyProgram program;
	private DataLocationEntity entity;
	
	private Boolean complete;
	private Boolean invalid;
	private Boolean closed;
	
	public SurveyEnteredProgram() {}
	
	public SurveyEnteredProgram(SurveyProgram program, DataLocationEntity entity, Boolean invalid, Boolean complete, Boolean closed) {
		super();
		this.program = program;
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
	@ManyToOne(targetEntity=SurveyProgram.class, fetch=FetchType.LAZY)
	public SurveyProgram getProgram() {
		return program;
	}
	
	public void setProgram(SurveyProgram program) {
		this.program = program;
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
				+ ((program == null) ? 0 : program.hashCode());
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
		SurveyEnteredProgram other = (SurveyEnteredProgram) obj;
		if (program == null) {
			if (other.program != null)
				return false;
		} else if (!program.equals(other.program))
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
		return "SurveyEnteredProgram [complete=" + complete + ", invalid=" + invalid + ", closed=" + closed + "]";
	}
	
}
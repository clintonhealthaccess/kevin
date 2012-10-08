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
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.security.User;
import org.chai.kevin.survey.SurveySection;
import org.hibernate.annotations.NaturalId;

@Entity(name="SurveyValidSection")
@Table(name="dhsst_survey_entered_section", uniqueConstraints=@UniqueConstraint(
		columnNames={"section", "dataLocation"})
)
public class SurveyEnteredSection extends EnteredEntity {
	
	private Long id;
	private SurveySection section;
	private DataLocation dataLocation;
	private Boolean invalid;
	private Boolean complete;
	
	private Integer totalQuestions;
	private Integer completedQuestions;
	
	public SurveyEnteredSection() {}
	
	public SurveyEnteredSection(SurveySection section, DataLocation dataLocation, Boolean invalid, Boolean complete) {
		super();
		this.section = section;
		this.dataLocation = dataLocation;
		this.invalid = invalid;
		this.complete = complete;
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
	@ManyToOne(targetEntity=SurveySection.class, fetch=FetchType.LAZY)
	public SurveySection getSection() {
		return section;
	}
	
	public void setSection(SurveySection section) {
		this.section = section;
	}

	@NaturalId
	@ManyToOne(targetEntity=DataLocation.class, fetch=FetchType.LAZY)
	public DataLocation getDataLocation() {
		return dataLocation;
	}
	
	public void setDataLocation(DataLocation dataLocation) {
		this.dataLocation = dataLocation;
	}
	
	@Basic
	public Integer getTotalQuestions() {
		return totalQuestions;
	}
	
	public void setTotalQuestions(Integer totalQuestions) {
		this.totalQuestions = totalQuestions;
	}
	
	@Basic
	public Integer getCompletedQuestions() {
		return completedQuestions;
	}
	
	public void setCompletedQuestions(Integer completedQuestions) {
		this.completedQuestions = completedQuestions;
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
	
	@Transient
	public String getDisplayedStatus() {
		String status = null;
		if (isInvalid()) status = "invalid";
		else if (!isComplete()) status = "incomplete";
		else status = "complete";
		return status;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((section == null) ? 0 : section.hashCode());
		result = prime
				* result
				+ ((dataLocation == null) ? 0 : dataLocation.hashCode());
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
		SurveyEnteredSection other = (SurveyEnteredSection) obj;
		if (section == null) {
			if (other.section != null)
				return false;
		} else if (!section.equals(other.section))
			return false;
		if (dataLocation == null) {
			if (other.dataLocation != null)
				return false;
		} else if (!dataLocation.equals(other.dataLocation))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SurveyEnteredSection [invalid=" + invalid + ", complete=" + complete + "]";
	}

}

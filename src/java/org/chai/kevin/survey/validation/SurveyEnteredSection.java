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

import org.chai.kevin.survey.SurveySection;
import org.hibernate.annotations.NaturalId;
import org.hisp.dhis.organisationunit.OrganisationUnit;

@Entity(name="SurveyValidSection")
@Table(name="dhsst_survey_entered_section", uniqueConstraints=@UniqueConstraint(
		columnNames={"section", "organisationUnit"})
)
public class SurveyEnteredSection extends SurveyEnteredEntity {
	
	private Long id;
	private SurveySection section;
	private OrganisationUnit organisationUnit;
	private Boolean invalid;
	private Boolean complete;
	
	public SurveyEnteredSection() {}
	
	public SurveyEnteredSection(SurveySection section, OrganisationUnit organisationUnit, Boolean invalid, Boolean complete) {
		super();
		this.section = section;
		this.organisationUnit = organisationUnit;
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
	@ManyToOne(targetEntity=OrganisationUnit.class, fetch=FetchType.LAZY)
	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
	}
	
	public void setOrganisationUnit(OrganisationUnit organisationUnit) {
		this.organisationUnit = organisationUnit;
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
		SurveyEnteredSection other = (SurveyEnteredSection) obj;
		if (section == null) {
			if (other.section != null)
				return false;
		} else if (!section.equals(other.section))
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
		return "SurveyEnteredSection [invalid=" + invalid + ", complete=" + complete + "]";
	}

}

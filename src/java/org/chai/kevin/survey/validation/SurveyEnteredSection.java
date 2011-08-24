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

import org.chai.kevin.survey.SurveySection;
import org.hibernate.annotations.NaturalId;
import org.hisp.dhis.organisationunit.OrganisationUnit;

@Entity(name="SurveyValidSection")
@Table(name="dhsst_survey_valid_section", uniqueConstraints=@UniqueConstraint(
		columnNames={"section", "organisationUnit"})
)
public class SurveyEnteredSection {
	
	public static enum SectionStatus {CLOSED, UNAVAILABLE, COMPLETE, INVALID, INCOMPLETE} 
	
	private Long id;
	private SurveySection section;
	private OrganisationUnit organisationUnit;
	private SectionStatus status;
	
	public SurveyEnteredSection() {}
	
	public SurveyEnteredSection(SurveySection section, OrganisationUnit organisationUnit, SectionStatus status) {
		super();
		this.section = section;
		this.organisationUnit = organisationUnit;
		this.status = status;
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
	@ManyToOne(targetEntity=SurveySection.class, optional=false)
	public SurveySection getSection() {
		return section;
	}
	
	public void setSection(SurveySection section) {
		this.section = section;
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
	public SectionStatus getStatus() {
		return status;
	}
	
	public void setStatus(SectionStatus status) {
		this.status = status;
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
		return "SurveyEnteredSection [section=" + section
				+ ", organisationUnit=" + organisationUnit + ", status="
				+ status + "]";
	}
	
}

package org.chai.kevin.survey.validation;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveyElement;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.NaturalId;
import org.hisp.dhis.organisationunit.OrganisationUnit;

@Entity(name="SurveyEnteredValue")
@Table(name="dhsst_survey_entered_value", 
		uniqueConstraints=@UniqueConstraint(columnNames={"surveyElement", "organisationUnit"}
))
public class SurveyEnteredValue {

	private Long id;
	private SurveyElement surveyElement;
	private String value;
	private OrganisationUnit organisationUnit;
	
	private Boolean skipped;
	private Boolean valid;
	private List<Long> acceptedWarnings = new ArrayList<Long>();
	
	public SurveyEnteredValue() {}
	
	public SurveyEnteredValue(SurveyElement surveyElement,
			OrganisationUnit organisationUnit, String value) {
		this.surveyElement = surveyElement;
		this.organisationUnit = organisationUnit;
		this.value = value;
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
	@OneToOne(targetEntity=SurveyElement.class)
	public SurveyElement getSurveyElement() {
		return surveyElement;
	}
	
	public void setSurveyElement(SurveyElement surveyElement) {
		this.surveyElement = surveyElement;
	}
	
	@Basic
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	@NaturalId
	@ManyToOne(targetEntity=OrganisationUnit.class, optional=false)
	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
	}
	
	public void setOrganisationUnit(OrganisationUnit organisationUnit) {
		this.organisationUnit = organisationUnit;
	}
	

	@CollectionOfElements(targetElement=Long.class)
	@JoinTable(name="dhsst_survey_accepted_warnings")
//	@Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
//	@Fetch(FetchMode.SELECT)
	public List<Long> getAcceptedWarnings() {
		return acceptedWarnings;
	}
	
	public void setAcceptedWarnings(List<Long> acceptedWarnings) {
		this.acceptedWarnings = acceptedWarnings;
	}
	
	@Basic
	public Boolean getSkipped() {
		return skipped;
	}
	
	public void setSkipped(Boolean skipped) {
		this.skipped = skipped;
	}
	
	@Basic
	public Boolean getValid() {
		return valid;
	}
	
	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	@Override
	public String toString() {
		return "SurveyEnteredValue [surveyElement=" + surveyElement
				+ ", value=" + value + ", organisationUnit=" + organisationUnit
				+ ", acceptedWarnings=" + acceptedWarnings.size() + "]";
	}
	
	@Transient
	public Survey getSurvey() {
		return surveyElement.getSurvey();
	}
	
}

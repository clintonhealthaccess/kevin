package org.chai.kevin.survey;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.chai.kevin.data.DataElement;
import org.chai.kevin.survey.validation.SurveyValidationRule;

@Entity(name = "SurveyElement")
@Table(name = "dhsst_survey_element")
public class SurveyElement {

	private Long id;
	private DataElement dataElement;
	private List<SurveyValidationRule> validationRules = new ArrayList<SurveyValidationRule>();
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(optional=false, targetEntity=DataElement.class)
	@JoinColumn(nullable=false)
	public DataElement getDataElement() {
		return dataElement;
	}
	
	public void setDataElement(DataElement dataElement) {
		this.dataElement = dataElement;
	}

	@OneToMany(mappedBy="surveyElement", targetEntity=SurveyValidationRule.class)
	public List<SurveyValidationRule> getValidationRules() {
		return validationRules;
	}
	
	public void setValidationRules(List<SurveyValidationRule> validationRules) {
		this.validationRules = validationRules;
	}
	
	public void addValidationRule(SurveyValidationRule validationRule) {
		validationRule.setSurveyElement(this);
		validationRules.add(validationRule);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		SurveyElement other = (SurveyElement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}

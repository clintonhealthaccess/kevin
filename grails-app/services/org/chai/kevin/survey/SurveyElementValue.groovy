package org.chai.kevin.survey;

import org.chai.kevin.Organisation;
import org.chai.kevin.survey.validation.SurveyValidationRule;
import org.chai.kevin.value.DataValue;
import org.codehaus.groovy.grails.validation.Validateable;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

@Validateable
public class SurveyElementValue {
	
	SurveyPage surveyPage;
	SurveyElement surveyElement;
	DataValue dataValue;
	
	List<SurveyValidationRule> invalidRules = [];
	
	public SurveyElementValue() {}
	
	public SurveyElementValue(SurveyPage surveyPage, SurveyElement surveyElement,
			DataValue dataValue) {
		this.surveyPage = surveyPage;
		this.surveyElement = surveyElement;
		this.dataValue = dataValue;
	}
	
	boolean isValid() {
		return invalidRules.isEmpty()
	}
			
	def userValidation(ValidationService validationService, OrganisationUnit organisationUnit, Period period) {
		surveyElement.validationRules.each { rule ->
			if (!validationService.validate(this, rule, organisationUnit, period)) {
				invalidRules.add(rule);
			}
		} 
	}
			
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((surveyElement == null) ? 0 : surveyElement.hashCode());
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
		SurveyElementValue other = (SurveyElementValue) obj;
		if (surveyElement == null) {
			if (other.surveyElement != null)
				return false;
		} else if (!surveyElement.equals(other.surveyElement))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SurveyElementValue [surveyElement=" + surveyElement + ", dataValue=" + dataValue + "]";
	}

	
}
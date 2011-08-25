package org.chai.kevin.survey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Organisation;
import org.chai.kevin.data.ValueType;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public class SurveyElementValue {
	
	private static Log log = LogFactory.getLog(SurveyElementValue.class)
	
//	SurveyEnteredValue surveyEnteredValue;
	SurveyElement surveyElement;
	Organisation organisation;
	String value;
	List<Long> acceptedWarnings = [];
	
	String lastValue;
	
//	Boolean skipped = false;
	Set<SurveyValidationRule> invalidErrors = [];
	Set<SurveyValidationRule> invalidWarnings = [];
	
	
	public SurveyElementValue() {}
	
	public SurveyElementValue(SurveyElement surveyElement, Organisation organisation, String lastValue) {
		this.surveyElement = surveyElement;
		this.organisation = organisation;
		this.lastValue = lastValue;
	}
				
	void sanitizeValues(SurveyPage page) {
		switch (surveyElement.dataElement.type) {
		case ValueType.BOOL:
			if (value != null && value != "0") value = "1"
			break;
		case ValueType.ENUM:
			if (value == "null") value = null
			break;
		case ValueType.STRING:
		case ValueType.VALUE:
			if (value != null && value.trim() == "") value = null
		default:
			break;
		}
		
		// this is a bit of a HACK, sanitize value when question is checkbox
		// if none of the values are checked, we set all of them to null
		// in order to reset the whole question
		// on the other if one of them is checked we set all the non-checked to 0
		if (surveyElement.surveyQuestion instanceof SurveyCheckboxQuestion) {
			boolean reset = true;
			for (SurveyElement element : surveyElement.surveyQuestion.getSurveyElements(organisation.getOrganisationUnitGroup())) {
				SurveyElementValue elementValue = page.getSurveyElements()[element.id];
				if (elementValue != null) {
					if (elementValue.value != null && elementValue.value != "0") reset = false;
				}
				else {
					SurveyEnteredValue enteredvalue = page.getEnteredValues()[element];
					if (enteredvalue.value != null && enteredvalue.value != "0") reset = false;
					
					elementValue = new SurveyElementValue(element, page.organisation)
					elementValue.value = enteredvalue.value
					page.getSurveyElements().put(element.id, elementValue)
				} 
			}
			for (SurveyElement element : surveyElement.surveyQuestion.getSurveyElements(organisation.getOrganisationUnitGroup())) {
				SurveyElementValue elementValue = page.getSurveyElements()[element.id];
				
				if (reset) elementValue.value = null;
				else if (elementValue.value == null) elementValue.value = "0"
			}
		}
	}

	def getDisplayedErrors() {
		if (!invalidErrors.isEmpty()) return invalidErrors;
		return invalidWarnings;
	}
			
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((surveyElement == null) ? 0 : surveyElement.hashCode());
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
		return "SurveyElementValue [surveyElement=" + surveyElement + ", value=" + value + ", acceptedWarnings=" + acceptedWarnings + "]";
	}
	
}
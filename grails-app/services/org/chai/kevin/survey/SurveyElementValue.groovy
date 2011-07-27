package org.chai.kevin.survey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Organisation;
import org.chai.kevin.data.ValueType;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.survey.validation.SurveyValidationRule;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public class SurveyElementValue {
	
	private static Log log = LogFactory.getLog(SurveyElementValue.class)
	
	SurveyElement surveyElement;
	SurveyEnteredValue surveyEnteredValue;
	Organisation organisation;
	String value;
	
	List<SurveyValidationRule> invalidErrors = [];
	List<SurveyValidationRule> invalidWarnings = [];
	
	public SurveyElementValue() {}
	
	public SurveyElementValue(SurveyElement surveyElement, SurveyEnteredValue surveyEnteredValue, Organisation organisation, String value) {
		this.surveyElement = surveyElement;
		this.surveyEnteredValue = surveyEnteredValue;
		this.organisation = organisation;
		this.value = value;
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
		if (surveyElement.surveyQuestion instanceof SurveyCheckboxQuestion) {
			boolean reset = true;
			for (SurveyElement element : surveyElement.surveyQuestion.getSurveyElements(organisation.getOrganisationUnitGroup())) {
				SurveyElementValue elementValue = page.getSurveyElementValue(element.id);
				if (elementValue.value != null && elementValue.value != "0") reset = false; 
			}
			if (reset) {
				for (SurveyElement element : surveyElement.surveyQuestion.getSurveyElements(organisation.getOrganisationUnitGroup())) {
					SurveyElementValue elementValue = page.getSurveyElementValue(element.id);
					elementValue.value = null;
				}
			}
		}
	}

	
	void transferValuesAndValidate(SurveyPage page, ValidationService validationService) {
		log.debug("new value="+value+", present value="+surveyEnteredValue?.value)
		
		if (value == null && surveyEnteredValue != null) {
			surveyEnteredValue.delete();
			surveyEnteredValue = null;	
		} 
		if (value != null) {
			if (surveyEnteredValue == null) surveyEnteredValue = new SurveyEnteredValue(surveyElement, organisation.getOrganisationUnit(), value)
			if (value != surveyEnteredValue.value) surveyEnteredValue.acceptedWarnings = []
			surveyEnteredValue.value = value;
			userValidation(page, validationService);
			surveyEnteredValue.valid = isValid();
		}
	}
	
	void skipPatterns(SurveyPage surveyPage, ValidationService validationService) {
		if (log.isDebugEnabled()) log.debug("userValidation(...)");
		if (surveyEnteredValue != null) {
			surveyElement.skipRules.each { rule ->
				if (!validationService.skipPattern(surveyPage, this, rule)) {
					
				}
			}
		}
	}
	
	void userValidation(SurveyPage surveyPage, ValidationService validationService) {
		if (log.isDebugEnabled()) log.debug("userValidation(...)");
		if (surveyEnteredValue != null) { 
			// TODO do type validation
	//		if (!dataValue.validate()) return false;
			
			surveyElement.validationRules.each { rule ->
				if (!validationService.validate(surveyPage, surveyElement, rule)) {
					if (!rule.allowOutlier) invalidErrors.add(rule);
					else {
						if (!surveyEnteredValue.acceptedWarnings.contains(rule.id)) {
							invalidWarnings.add(rule);
						}
					}
				}
			}
		}
		if (log.isDebugEnabled()) log.debug("validation done, invalidErrors: "+invalidErrors+", invalidWarnings: "+invalidWarnings)
	}
	
	boolean isValid() {
		return invalidErrors.isEmpty() && invalidWarnings.isEmpty() 
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
		return "SurveyElementValue [surveyElement=" + surveyElement + ", enteredValue=" + surveyEnteredValue + "]";
	}
	
}
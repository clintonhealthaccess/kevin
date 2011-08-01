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
	
//	SurveyEnteredValue surveyEnteredValue;
	SurveyElement surveyElement;
	Organisation organisation;
	String value;
	List<Long> acceptedWarnings = [];
	
//	Boolean skipped = false;
	Set<SurveyValidationRule> invalidErrors = [];
	Set<SurveyValidationRule> invalidWarnings = [];
	
	
	public SurveyElementValue() {}
	
	public SurveyElementValue(SurveyElement surveyElement, Organisation organisation) {
		this.surveyElement = surveyElement;
		this.organisation = organisation;
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
				SurveyElementValue elementValue = page.getSurveyElements()[element.id];
				if (elementValue.value != null && elementValue.value != "0") reset = false; 
			}
			if (reset) {
				for (SurveyElement element : surveyElement.surveyQuestion.getSurveyElements(organisation.getOrganisationUnitGroup())) {
					SurveyElementValue elementValue = page.getSurveyElements()[element.id];
					elementValue.value = null;
				}
			}
		}
	}

	
//	void transferValue(SurveyPage page, SurveyEnteredValue enteredValue, ValidationService validationService) {
//		log.debug("new value="+value+", present value="+surveyEnteredValue?.value)
//		
//		if (value == null && enteredValue != null) {
//			enteredValue.delete();
//		} 
//		if (value != null) {
//			if (enteredValue == null) surveyEnteredValue = new SurveyEnteredValue(enteredValue.surveyElement, organisation.getOrganisationUnit(), value)
//			if (value != surveyEnteredValue.value) surveyEnteredValue.acceptedWarnings = []
//			surveyEnteredValue.value = value;
//		}
//	}
	
//	void userValidation(SurveyPage surveyPage, SurveyEnteredValue enteredValue, ValidationService validationService) {
//		if (log.isDebugEnabled()) log.debug("userValidation(...)");
//		
//		if (surveyPage.isSkipped(enteredValue.surveyElement.surveyQuestion) 
//			|| validationService.isSkipped(surveyPage, enteredValue.surveyElement)) {
//			skipped = true;
//		}
//		else if (enteredValue != null) { 
//			enteredValue.surveyElement.validationRules.each { rule ->
//				if (!validationService.validate(surveyPage, enteredValue.surveyElement, rule)) {
//					if (!rule.allowOutlier) invalidErrors.add(rule);
//					else {
//						if (!enteredValue.acceptedWarnings.contains(rule.id)) {
//							invalidWarnings.add(rule);
//						}
//					}
//				}
//			}
//		}
//		
//		if (log.isDebugEnabled()) log.debug("validation done, invalidErrors: "+invalidErrors+", invalidWarnings: "+invalidWarnings)
//	}
	
//	boolean isSkipped() {
//		return skipped;
//	}
	
//	boolean isValid() {
//		return invalidErrors.isEmpty() && invalidWarnings.isEmpty() 
//	}
	
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
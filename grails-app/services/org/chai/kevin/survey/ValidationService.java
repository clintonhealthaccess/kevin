package org.chai.kevin.survey;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.Organisation;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.springframework.transaction.annotation.Transactional;

public class ValidationService {
	private static final Log log = LogFactory.getLog(ValidationService.class);
	
	private SurveyElementService surveyElementService;
	
	@Transactional(readOnly=true)
	public boolean isSkipped(SurveyElement surveyElement, Organisation organisation, Collection<SurveyEnteredValue> values) {
		if (log.isDebugEnabled()) log.debug("isSkipped(surveyElement="+surveyElement+")");
		
		boolean result = false;
		Set<SurveySkipRule> skipRules = surveyElementService.getSkipRules(surveyElement);
		for (SurveySkipRule skipRule : skipRules) {
			result = result | evaluate(skipRule.getExpression(), organisation, values);
		}
		
		if (log.isDebugEnabled()) log.debug("isSkipped(...)="+result);
		return result;		
	}
	
	@Transactional(readOnly=true)
	public boolean isSkipped(SurveyQuestion surveyQuestion, Organisation organisation, Collection<SurveyEnteredValue> values) {
		if (log.isDebugEnabled()) log.debug("isSkipped(surveyQuestion="+surveyQuestion+")");
		
		boolean result = false;
		Set<SurveySkipRule> skipRules = surveyElementService.getSkipRules(surveyQuestion);
		for (SurveySkipRule skipRule : skipRules) {
			result = result | evaluate(skipRule.getExpression(), organisation, values);
		}
		
		if (log.isDebugEnabled()) log.debug("isSkipped(...)="+result);
		return result;		
	}
	
	@Transactional(readOnly=true)
	public boolean validate(SurveyElement surveyElement, SurveyValidationRule validationRule, Organisation organisation, Collection<SurveyEnteredValue> values) {
		if (log.isDebugEnabled()) log.debug("validate(value="+surveyElement+", validationRule="+validationRule+")");
		boolean result = evaluate(validationRule.getExpression(), organisation, values);
		if (log.isDebugEnabled()) log.debug("validate(...)="+result);
		return result;
	}
	
	private boolean evaluate(String expression, Organisation organisation, Collection<SurveyEnteredValue> values) {
		if (log.isDebugEnabled()) log.debug("evaluate(expression="+expression+")");
		Set<String> placeholders = ExpressionService.getPlaceholders(expression);
		
		Map<String, String> replace = new HashMap<String, String>();
		for (String placeholder : placeholders) {
			Long id = Long.parseLong(placeholder);
			SurveyElement surveyElement = surveyElementService.getSurveyElement(id);
			String value = null;
			if (surveyElement != null) {
				SurveyEnteredValue surveyEnteredValue = getValue(values, surveyElement);
				if (surveyEnteredValue != null) {
					// we check something on the same section
					value = surveyEnteredValue.getValue();
				}
				else {
					SurveyEnteredValue enteredValue = surveyElementService.getSurveyEnteredValue(surveyElement, organisation.getOrganisationUnit());
					if (enteredValue != null) value = enteredValue.getValue();
				}
			}
			else if (log.isErrorEnabled()) log.error("expression "+expression+" refers to unknown survey element: "+id);
			String replacement = String.valueOf(value);
			replace.put(placeholder, replacement);
		}
		
		String toEvaluate = ExpressionService.convertStringExpression(expression, replace);
		Object evaluation = ExpressionService.evaluate(toEvaluate);
		
		boolean result;
		if (evaluation == null || evaluation.equals(0d)) {
			result =  false;
		}
		else result = true;
		
		if (log.isDebugEnabled()) log.debug("evaluate(...)="+result);
		return result;
	}
	
	private SurveyEnteredValue getValue(Collection<SurveyEnteredValue> values, SurveyElement element) {
		for (SurveyEnteredValue surveyEnteredValue : values) {
			if (surveyEnteredValue.getSurveyElement().equals(element)) return surveyEnteredValue;
		}
		return null;
	}

	public void setSurveyElementService(SurveyElementService surveyElementService) {
		this.surveyElementService = surveyElementService;
	}
}

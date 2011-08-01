package org.chai.kevin.survey;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.survey.validation.SurveySkipRule;
import org.chai.kevin.survey.validation.SurveyValidationRule;
import org.springframework.transaction.annotation.Transactional;

public class ValidationService {
	private static final Log log = LogFactory.getLog(ValidationService.class);
	
	private SurveyElementService surveyElementService;
	
	@Transactional(readOnly=true)
	public boolean isSkipped(SurveyPage surveyPage, SurveyElement surveyElement) {
		if (log.isDebugEnabled()) log.debug("isSkipped(surveyPage="+surveyPage+", surveyElement="+surveyElement+")");
		
		boolean result = false;
		Set<SurveySkipRule> skipRules = surveyElementService.getSkipRules(surveyElement);
		for (SurveySkipRule skipRule : skipRules) {
			result = result | evaluate(surveyPage, skipRule.getExpression());
		}
		
		if (log.isDebugEnabled()) log.debug("isSkipped(...)="+result);
		return result;		
	}
	
	@Transactional(readOnly=true)
	public boolean isSkipped(SurveyPage surveyPage, SurveyQuestion surveyQuestion) {
		if (log.isDebugEnabled()) log.debug("isSkipped(surveyPage="+surveyPage+", surveyQuestion="+surveyQuestion+")");
		
		boolean result = false;
		Set<SurveySkipRule> skipRules = surveyElementService.getSkipRules(surveyQuestion);
		for (SurveySkipRule skipRule : skipRules) {
			result = result | evaluate(surveyPage, skipRule.getExpression());
		}
		
		if (log.isDebugEnabled()) log.debug("isSkipped(...)="+result);
		return result;		
	}
	
	@Transactional(readOnly=true)
	public boolean validate(SurveyPage surveyPage, SurveyElement surveyElement, SurveyValidationRule validationRule) {
		if (log.isDebugEnabled()) log.debug("validate(value="+surveyElement+", validationRule="+validationRule+")");
		boolean result = evaluate(surveyPage, validationRule.getExpression());
		if (log.isDebugEnabled()) log.debug("validate(...)="+result);
		return result;
	}
	
	private boolean evaluate(SurveyPage surveyPage, String expression) {
		if (log.isDebugEnabled()) log.debug("evaluate(surveyPage="+surveyPage+", expression="+expression+")");
		Set<String> placeholders = ExpressionService.getPlaceholders(expression);
		
		Map<String, String> replace = new HashMap<String, String>();
		for (String placeholder : placeholders) {
			Long id = Long.parseLong(placeholder);
			SurveyElement surveyElement = surveyElementService.getSurveyElement(id);
			String value = null;
			if (surveyElement != null) {
				SurveyEnteredValue surveyEnteredValue = surveyPage.getEnteredValues().get(surveyElement);
				if (surveyEnteredValue != null) {
					// we check something on the same section
					value = surveyEnteredValue.getValue();
				}
				else {
					SurveyEnteredValue enteredValue = surveyElementService.getSurveyEnteredValue(surveyElement, surveyPage.getOrganisation().getOrganisationUnit());
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

	public void setSurveyElementService(SurveyElementService surveyElementService) {
		this.surveyElementService = surveyElementService;
	}
}

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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.transaction.annotation.Transactional;

public class ValidationService {
	private static final Log log = LogFactory.getLog(ValidationService.class);
	
	private SurveyElementService surveyElementService;
	
	@Transactional(readOnly=true)
	public boolean isSkipped(SurveyPage surveyPage, SurveyElement surveyElement) {
		Set<SurveySkipRule> skipRules = surveyElementService.getSkipRules(surveyElement);
		for (SurveySkipRule skipRule : skipRules) {
			boolean result = evaluate(surveyPage, surveyElementValueToCheck, skipRule.getExpression());
		}
		
		if (log.isDebugEnabled()) log.debug("skipPattern(...)="+result);
		return result;		
	}
	
	@Transactional(readOnly=true)
	public boolean validate(SurveyPage surveyPage, SurveyElementValue surveyElementValueToCheck, SurveyValidationRule validationRule) {
		if (log.isDebugEnabled()) log.debug("validate(value="+surveyElementValueToCheck+", validationRule="+validationRule+")");
		boolean result = evaluate(surveyPage, surveyElementValueToCheck, validationRule.getExpression());
		if (log.isDebugEnabled()) log.debug("validate(...)="+result);
		return result;
	}
	
	private boolean evaluate(SurveyPage surveyPage, SurveyElement surveyElementToCheck, String expression) {
		Set<String> placeholders = ExpressionService.getPlaceholders(expression);
		
		Map<String, String> replace = new HashMap<String, String>();
		for (String placeholder : placeholders) {
			Long id = Long.parseLong(placeholder);
			SurveyElementValue surveyElementValue = surveyPage.getSurveyElementValue(Long.parseLong(placeholder));
			
			String value = null;
			if (surveyElementValue != null) {
				value = surveyElementValue.getSurveyEnteredValue().getValue();
			}
			else {
				SurveyElement surveyElement = surveyElementService.getSurveyElement(id);
				if (surveyElement != null) {
					SurveyEnteredValue enteredValue = surveyElementService.getSurveyEnteredValue(surveyElement, surveyElementValueToCheck.getSurveyEnteredValue().getOrganisationUnit());
					if (enteredValue != null) value = enteredValue.getValue();
				}
				else if (log.isErrorEnabled()) log.error("expression "+expression+" refers to unknown survey element: "+id);
			}
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
		
		return result;
	}

	public void setSurveyElementService(SurveyElementService surveyElementService) {
		this.surveyElementService = surveyElementService;
	}
}

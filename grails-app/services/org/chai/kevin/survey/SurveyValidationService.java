package org.chai.kevin.survey;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormSkipRule;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.ValidationService;
import org.springframework.transaction.annotation.Transactional;

public class SurveyValidationService {
	
	private static final Log log = LogFactory.getLog(SurveyValidationService.class);
	
	private ValidationService validationService;
	
	@Transactional(readOnly=true)
	public Set<String> getSkippedPrefix(FormElement element, FormSkipRule rule, DataLocationEntity entity, ValidatableLocator locator) {
		if (log.isDebugEnabled()) log.debug("getSkippedPrefix(surveyElement="+element+", rule="+rule+", entity="+entity+")");
		
		ValidatableValue validatable = locator.getValidatable(element.getId(), entity);

		Set<String> prefixes = rule.getSkippedPrefixes(element);
		String expression = rule.getExpression();
		
		Set<String> result = new HashSet<String>();
		if (validatable!= null) {
			result.addAll(validationService.getPrefixes(expression, prefixes, validatable, entity, locator, true));
		}
		
		if (log.isDebugEnabled()) log.debug("getSkippedPrefix(...)="+result);
		return result;		
	}
	
	@Transactional(readOnly=true)
	public boolean isSkipped(FormSkipRule skipRule, DataLocationEntity entity, ValidatableLocator locator) {
		if (log.isDebugEnabled()) log.debug("isSkipped(surveyQuestion="+skipRule+", entity="+entity+")");
		
		boolean result = false;
		if (!ValidationService.isWildcard(skipRule.getExpression())) {
			Boolean eval = validationService.evaluate(skipRule.getExpression(), entity, locator);
			if (eval != null) result = eval;
		}
		
		if (log.isDebugEnabled()) log.debug("isSkipped(...)="+result);
		return result;		
	}
	
	@Transactional(readOnly=true)
	public Set<String> getInvalidPrefix(FormValidationRule validationRule, DataLocationEntity entity, ValidatableLocator locator) {
		if (log.isDebugEnabled()) log.debug("getInvalidPrefix(validationRule="+validationRule+", entity="+entity+")");

		Set<String> result = new HashSet<String>();
		if (Utils.split(validationRule.getTypeCodeString()).contains(entity.getType().getCode())) {
			// we validate only if that rule applies to the group
			ValidatableValue validatable = locator.getValidatable(validationRule.getFormElement().getId(), entity);
	
			String prefix = validationRule.getPrefix();
			String expression = validationRule.getExpression();
			
			Set<String> prefixes = new HashSet<String>();
			prefixes.add(prefix);
			if (validatable != null) result.addAll(validationService.getPrefixes(expression, prefixes, validatable, entity, locator, false));
		}
		if (log.isDebugEnabled()) log.debug("getInvalidPrefix(...)="+result);
		return result;
	}

	
	public interface ValidatableLocator {
		public ValidatableValue getValidatable(Long id, DataLocationEntity location);
	}
	
	
	public void setValidationService(ValidationService validationService) {
		this.validationService = validationService;
	}
	
}

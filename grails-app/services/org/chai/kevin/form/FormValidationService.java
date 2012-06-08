package org.chai.kevin.form;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormSkipRule;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.ValidationService;
import org.springframework.transaction.annotation.Transactional;

public class FormValidationService {
	
	private static final Log log = LogFactory.getLog(FormValidationService.class);
	
	private ValidationService validationService;
	
	@Transactional(readOnly=true)
	public Set<String> getSkippedPrefix(FormElement element, FormSkipRule rule, DataLocation dataLocation, ValidatableLocator locator) {
		if (log.isDebugEnabled()) log.debug("getSkippedPrefix(surveyElement="+element+", rule="+rule+", dataLocation="+dataLocation+")");
		
		ValidatableValue validatable = locator.getValidatable(element.getId(), dataLocation);

		Set<String> prefixes = rule.getSkippedPrefixes(element);
		String expression = rule.getExpression();
		
		Set<String> result = new HashSet<String>();
		if (validatable!= null) {
			result.addAll(validationService.getPrefixes("$"+element.getId(), expression, prefixes, validatable, dataLocation, locator, true));
		}
		
		if (log.isDebugEnabled()) log.debug("getSkippedPrefix(...)="+result);
		return result;		
	}
	
	@Transactional(readOnly=true)
	public boolean isSkipped(FormSkipRule skipRule, DataLocation dataLocation, ValidatableLocator locator) {
		if (log.isDebugEnabled()) log.debug("isSkipped(surveyQuestion="+skipRule+", dataLocation="+dataLocation+")");
		
		boolean result = false;
		if (!ValidationService.isWildcard(skipRule.getExpression())) {
			Boolean eval = validationService.evaluate(skipRule.getExpression(), dataLocation, locator);
			if (eval != null) result = eval;
		}
		
		if (log.isDebugEnabled()) log.debug("isSkipped(...)="+result);
		return result;		
	}
	
	@Transactional(readOnly=true)
	public Set<String> getInvalidPrefix(FormValidationRule validationRule, DataLocation dataLocation, ValidatableLocator locator) {
		if (log.isDebugEnabled()) log.debug("getInvalidPrefix(validationRule="+validationRule+", dataLocation="+dataLocation+")");

		Set<String> result = new HashSet<String>();
		if (Utils.split(validationRule.getTypeCodeString()).contains(dataLocation.getType().getCode())) {
			// we validate only if that rule applies to the group
			ValidatableValue validatable = locator.getValidatable(validationRule.getFormElement().getId(), dataLocation);
	
			String prefix = validationRule.getPrefix();
			String expression = validationRule.getExpression();
			
			Set<String> prefixes = new HashSet<String>();
			prefixes.add(prefix);
			if (validatable != null) result.addAll(validationService.getPrefixes("$"+validationRule.getFormElement().getId(), expression, prefixes, validatable, dataLocation, locator, false));
		}
		if (log.isDebugEnabled()) log.debug("getInvalidPrefix(...)="+result);
		return result;
	}

	public interface ValidatableLocator {
		public ValidatableValue getValidatable(Long id, DataLocation location);
	}
	
	public void setValidationService(ValidationService validationService) {
		this.validationService = validationService;
	}
	
}

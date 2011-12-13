package org.chai.kevin.survey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.JaqlService;
import org.chai.kevin.LocationService;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.ExpressionService;
import org.chai.kevin.value.Value;
import org.springframework.transaction.annotation.Transactional;

public class ValidationService {
	private static final Log log = LogFactory.getLog(ValidationService.class);
	
	private LocationService locationService;
	private SurveyService surveyService;
	private SurveyValueService surveyValueService;
	private JaqlService jaqlService;
	
	@Transactional(readOnly=true)
	public Set<String> getSkippedPrefix(SurveyElement element, SurveySkipRule rule, DataEntity entity) {
		if (log.isDebugEnabled()) log.debug("getSkippedPrefix(surveyElement="+element+", rule="+rule+", entity="+entity+")");
		
		SurveyEnteredValue enteredValue = surveyValueService.getSurveyEnteredValue(element, entity);

		Set<String> prefixes = rule.getSkippedPrefixes(element);
		String expression = rule.getExpression();
		
		Set<String> result = new HashSet<String>();
		if (enteredValue!= null) {
			result.addAll(getPrefixes(expression, prefixes, enteredValue, true));
		}
		
		if (log.isDebugEnabled()) log.debug("getSkippedPrefix(...)="+result);
		return result;		
	}
	
	@Transactional(readOnly=true)
	public boolean isSkipped(SurveySkipRule skipRule, DataEntity entity) {
		if (log.isDebugEnabled()) log.debug("isSkipped(surveyQuestion="+skipRule+", entity="+entity+")");
		
		boolean result = false;
		if (!isWildcard(skipRule.getExpression())) {
			Boolean eval = evaluate(skipRule.getExpression(), entity);
			if (eval != null) result = eval;
		}
		
		if (log.isDebugEnabled()) log.debug("isSkipped(...)="+result);
		return result;		
	}
	
	@Transactional(readOnly=true)
	public Set<String> getInvalidPrefix(SurveyValidationRule validationRule, DataEntity entity) {
		if (log.isDebugEnabled()) log.debug("getInvalidPrefix(validationRule="+validationRule+", entity="+entity+")");

		Set<String> result = new HashSet<String>();
		if (Utils.split(validationRule.getGroupUuidString()).contains(entity.getType().getCode())) {
			// we validate only if that rule applies to the group
			SurveyEnteredValue enteredValue = surveyValueService.getSurveyEnteredValue(validationRule.getSurveyElement(), entity);
	
			String prefix = validationRule.getPrefix();
			String expression = validationRule.getExpression();
			
			Set<String> prefixes = new HashSet<String>();
			prefixes.add(prefix);
			if (enteredValue != null) result.addAll(getPrefixes(expression, prefixes, enteredValue, false));
		}
		if (log.isDebugEnabled()) log.debug("getInvalidPrefix(...)="+result);
		return result;
	}

	private Set<String> getPrefixes(String expression, Set<String> prefixes, SurveyEnteredValue enteredValue, Boolean evaluateTo) {
		Set<String> result = new HashSet<String>();
		Set<List<String>> combinations = new HashSet<List<String>>();
		
		List<String> toCombine = new ArrayList<String>();
		toCombine.add(expression);
		toCombine.addAll(prefixes);
		
		enteredValue.getSurveyElement().getDataElement().getType().getCombinations(
			enteredValue.getValue(), 
			toCombine, 
			combinations, 
			""
		);
		
		for (List<String> list : combinations) {
			if (!isWildcard(list)) {
				if (evaluateTo.equals(evaluate(list.get(0), enteredValue.getEntity()))) result.addAll(list.subList(1, list.size()));
			}
		}
		return result;
	}
	
	private boolean isWildcard(List<String> strings) {
		for (String string : strings) {
			if (isWildcard(string)) return true;
		}
		return false;
	}
	
	private boolean isWildcard(String string) {
		return string.contains("[_]");
	}
	
	private Boolean evaluate(String expression, DataEntity entity) {
		if (log.isDebugEnabled()) log.debug("evaluate(expression="+expression+")");
		
		Map<String, Value> valueMap = new HashMap<String, Value>();
		Map<String, Type> typeMap = new HashMap<String, Type>();
		
		Map<String, SurveyElement> elements = getSurveyElementInExpression(expression);
		for (SurveyElement element : elements.values()) {
			Value value = null;
			if (element != null) {
				SurveyEnteredValue enteredValue = surveyValueService.getSurveyEnteredValue(element, entity);
				if (enteredValue != null) value = enteredValue.getValue();
			}
			else if (log.isErrorEnabled()) log.error("expression "+expression+" refers to unknown survey element");
			
			valueMap.put(element.getId().toString(), value);
			typeMap.put(element.getId().toString(), element.getDataElement().getType());
		}
		
		Value value = null;
		try {
			value = jaqlService.evaluate(expression, Type.TYPE_BOOL(), valueMap, typeMap);
		} catch (IllegalArgumentException e) {}
		
		Boolean result;
		if (value == null) result = null;
		else result = value.getBooleanValue();
		
		if (log.isDebugEnabled()) log.debug("evaluate(...)="+result);
		return result;
	}
	
	public Map<String, SurveyElement> getSurveyElementInExpression(String expression) {
        Map<String, SurveyElement> dataInExpression = new HashMap<String, SurveyElement>();
    	Set<String> placeholders = ExpressionService.getVariables(expression);

    	for (String placeholder : placeholders) {
            SurveyElement surveyElement = null;
            try {
            	surveyElement = surveyService.getSurveyElement(Long.parseLong(placeholder.replace("$", "")));
            }
            catch (NumberFormatException e) {
            	log.error("wrong format for dataelement: "+placeholder);
            }
            dataInExpression.put(placeholder, surveyElement);
        }

    	return dataInExpression;
    }
	
	public void setJaqlService(JaqlService jaqlService) {
		this.jaqlService = jaqlService;
	}
	
	public void setSurveyService(SurveyService surveyService) {
		this.surveyService = surveyService;
	}
	
	public void setSurveyValueService(SurveyValueService surveyValueService) {
		this.surveyValueService = surveyValueService;
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
}

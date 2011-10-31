package org.chai.kevin.survey;

import java.util.HashMap;
import java.util.Map;

public class SurveyCopy<T> {

	private T copy;
	private Map<SurveyValidationRule, Long> unchangedValidationRules = new HashMap<SurveyValidationRule, Long>();
	private Map<SurveySkipRule, Long> unchangedSkipRules = new HashMap<SurveySkipRule, Long>();
	
	public SurveyCopy(T copy) {
		this.copy = copy;
	}
	
	public SurveyCopy(T copy, Map<SurveyValidationRule, Long> unchangedValidationRules,
			Map<SurveySkipRule, Long> unchangedSkipRules) {
		this.copy = copy;
		this.unchangedValidationRules = unchangedValidationRules;
		this.unchangedSkipRules = unchangedSkipRules;
	}
	
	public T getCopy() {
		return copy;
	}
	
	public Map<SurveyValidationRule, Long> getUnchangedValidationRules() {
		return unchangedValidationRules;
	}
	
	public Map<SurveySkipRule, Long> getUnchangedSkipRules() {
		return unchangedSkipRules;
	}
	
}

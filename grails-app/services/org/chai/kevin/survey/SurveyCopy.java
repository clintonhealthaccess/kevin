package org.chai.kevin.survey;

import java.util.HashMap;
import java.util.Map;

import org.chai.kevin.form.FormSkipRule;
import org.chai.kevin.form.FormValidationRule;

public class SurveyCopy<T> {

	private T copy;
	private Map<FormValidationRule, Long> unchangedValidationRules = new HashMap<FormValidationRule, Long>();
	private Map<FormSkipRule, Long> unchangedSkipRules = new HashMap<FormSkipRule, Long>();
	
	public SurveyCopy(T copy) {
		this.copy = copy;
	}
	
	public SurveyCopy(T copy, Map<FormValidationRule, Long> unchangedValidationRules, Map<FormSkipRule, Long> unchangedSkipRules) {
		this.copy = copy;
		this.unchangedValidationRules = unchangedValidationRules;
		this.unchangedSkipRules = unchangedSkipRules;
	}
	
	public T getCopy() {
		return copy;
	}
	
	public Map<FormValidationRule, Long> getUnchangedValidationRules() {
		return unchangedValidationRules;
	}
	
	public Map<FormSkipRule, Long> getUnchangedSkipRules() {
		return unchangedSkipRules;
	}
	
}

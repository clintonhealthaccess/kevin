package org.chai.kevin.form;

import groovy.transform.EqualsAndHashCode;
import i18nfields.I18nFields

import java.util.HashSet
import java.util.Map
import java.util.Set
import java.util.Map.Entry

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.chai.kevin.form.FormElement.ElementCalculator
import org.chai.kevin.survey.Survey
import org.chai.kevin.survey.SurveyQuestion
import org.chai.kevin.util.Utils
import org.chai.location.DataLocation

@I18nFields
@EqualsAndHashCode(includes='code')
public class FormSkipRule {

	private final static Log log = LogFactory.getLog(FormSkipRule.class);
	
	// deprecated
	Long id
	
	String code;
	String expression;
	
	String descriptions
	
	// deprecated
	String jsonDescriptions
	
	static i18nFields = ['descriptions']
	
	static hasMany = [
		skippedSurveyQuestions: SurveyQuestion, 
		formSkipRuleElementMaps: FormSkipRuleElementMap
	]
	
	static transients = ['skippedFormElements']
	
	static mapping = {
		table 'dhsst_form_skip_rule'
		tablePerHierarchy false
		skippedSurveyQuestions joinTable: [
			name: 'dhsst_survey_skipped_survey_questions',
			key: 'dhsst_survey_skip_rule',
			column: 'skippedSurveyQuestions'
		]
		survey column: 'survey'
		code unique: true
		formSkipRuleElementMaps cascade: "all-delete-orphan"
	}
	
	static constraints = {
		code (nullable: false, blank: false, unique: true)
		expression (nullable: false, blank: false)
		descriptions (nullable: true)
		jsonDescriptions (nullable: true)
	}
	
	public FormSkipRule() {
		super();
	}
	
	public Map<FormElement, String> getSkippedFormElements() {
		Map result = [:]
		formSkipRuleElementMaps?.each {
			result.put(it.formElement, it.skippedFormElements)
		}
		return result
	}

	public void setSkippedFormElements(Map<FormElement, String> skippedFormElements) {
		formSkipRuleElementMaps?.clear()
		skippedFormElements.each {
			addToFormSkipRuleElementMaps(new FormSkipRuleElementMap(
				formElement: it.key,
				skippedFormElements: it.value
			))
		}
	}

	public Set<String> getSkippedPrefixes(FormElement element) {
		Set<String> result = new HashSet<String>();
		if (skippedFormElements.containsKey(element)) {
			String text = skippedFormElements.get(element);
			if (text.isEmpty()) result.add(text);
			result.addAll(Utils.split(text, FormElement.FIELD_DELIMITER));
		}
		return result;
	}

	public void deepCopy(FormSkipRule copy, FormCloner formCloner) {
		copy.setCode(getCode() + ' clone')
		copy.setExpression(formCloner.getExpression(getExpression(), copy));
		def skippedElementsCopy = [:]
		for (Entry<FormElement, String> entry : getSkippedFormElements().entrySet()) {
			skippedElementsCopy.put(formCloner.getElement(entry.getKey()), entry.getValue());
		}
		copy.setSkippedFormElements(skippedElementsCopy)
	}
	
	public void evaluate(DataLocation dataLocation, ElementCalculator calculator) {
		if (log.isDebugEnabled()) log.debug("evaluate(dataLocation="+dataLocation+") on "+this);
		
		for (FormElement formElement : getSkippedFormElements().keySet()) {
			Set<String> prefixes = calculator.getFormValidationService().getSkippedPrefix(formElement, this, dataLocation, calculator.getValidatableLocator());
	
			FormEnteredValue enteredValue = calculator.getFormElementService().getOrCreateFormEnteredValue(dataLocation, formElement);
			enteredValue.getValidatable().setSkipped(this, prefixes);
			
			calculator.addAffectedValue(enteredValue);
		}
	}
	
}
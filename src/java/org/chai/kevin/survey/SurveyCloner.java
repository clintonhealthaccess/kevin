package org.chai.kevin.survey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.ExpressionService;
import org.chai.kevin.util.LanguageUtils;


public class SurveyCloner {

	private Survey survey;
	private Survey copy;

	private List<SurveyElement> oldElements = new ArrayList<SurveyElement>();
	private Map<Long, SurveyObjective> objectives = new HashMap<Long, SurveyObjective>();
	private Map<Long, SurveySection> sections = new HashMap<Long, SurveySection>();
	private Map<Long, SurveyQuestion> questions = new HashMap<Long, SurveyQuestion>();
	private Map<Long, SurveyElement> elements = new HashMap<Long, SurveyElement>();
	private Map<Long, SurveySkipRule> skipRules = new HashMap<Long, SurveySkipRule>();
	private Map<Long, SurveyValidationRule> validationRules = new HashMap<Long, SurveyValidationRule>();

	private Map<SurveyValidationRule, Long> unchangedValidationRules = new HashMap<SurveyValidationRule, Long>();
	private Map<SurveySkipRule, Long> unchangedSkipRules = new HashMap<SurveySkipRule, Long>();
	
	public SurveyCloner(Survey survey) {
		this.survey = survey;
	}

	public Survey getSurvey() {
		return copy;
	}
	
	public Map<SurveyValidationRule, Long> getUnchangedValidationRules() {
		return unchangedValidationRules;
	}

	public Map<SurveySkipRule, Long> getUnchangedSkipRules() {
		return unchangedSkipRules;
	}

	public void cloneTree() {
		this.getSurvey(survey);
	}
	
	public void cloneRules() {
		if (copy.getId() == null) throw new IllegalStateException();
		survey.copyRules(copy, this);
		for (SurveyElement element : oldElements) {
			element.copyRules(elements.get(element.getId()), this);
		}
	}

	protected String getExpression(String expression, SurveyValidationRule rule) {
		Set<String> placeholders = ExpressionService.getVariables(expression);
		Map<String, String> mapping = new HashMap<String, String>();
		for (String placeholder : placeholders) {
			Long id = Long.parseLong(placeholder.replace("$", ""));
			if (elements.containsKey(id)) {
				mapping.put(placeholder, "$"+elements.get(id).getId().toString());
			}
			else {
				unchangedValidationRules.put(rule, id);
			}
		}
		return ExpressionService.convertStringExpression(expression, mapping);
	}
	
	protected String getExpression(String expression, SurveySkipRule rule) {
		Set<String> placeholders = ExpressionService.getVariables(expression);
		Map<String, String> mapping = new HashMap<String, String>();
		for (String placeholder : placeholders) {
			Long id = Long.parseLong(placeholder.replace("$", ""));
			if (elements.containsKey(id)) {
				mapping.put(placeholder, "$"+elements.get(id).getId().toString());
			}
			else {
				unchangedSkipRules.put(rule, id);
			}
		}
		return ExpressionService.convertStringExpression(expression, mapping);
	}
	
	protected Survey getSurvey(Survey survey) {
		if (!survey.equals(this.survey)) throw new IllegalArgumentException();
		if (copy == null) {
			copy = new Survey(); 
			survey.deepCopy(copy, this);
			for (String language : LanguageUtils.getAvailableLanguages()) {
				// TODO localize "copy"
				copy.getNames().put(language, survey.getNames().get(language) + " (copy)");
			}
		}
		return copy;
	}
	
	protected SurveyObjective getObjective(SurveyObjective objective) {
		if (!objectives.containsKey(objective.getId())) {
			SurveyObjective copy = new SurveyObjective(); 
			objectives.put(objective.getId(), copy);
			objective.deepCopy(copy, this);
		}
		return objectives.get(objective.getId());
	}
	
	protected SurveySection getSection(SurveySection section) {
		if (!sections.containsKey(section.getId())) {
			SurveySection copy = new SurveySection();
			sections.put(section.getId(), copy);
			section.deepCopy(copy, this);
		}
		return sections.get(section.getId());
	}

	protected SurveyQuestion getQuestion(SurveyQuestion question) {
		if (!questions.containsKey(question.getId())) {
			SurveyQuestion copy = question.newInstance();
			questions.put(question.getId(), copy);
			question.deepCopy(copy, this);
		}
		return questions.get(question.getId());

	}

	protected SurveyElement getElement(SurveyElement element) {
		if (element == null) return null;
		
		if (!elements.containsKey(element.getId())) {
			SurveyElement copy = new SurveyElement(); 
			elements.put(element.getId(), copy);
			element.deepCopy(copy, this);
			oldElements.add(element);
		}
		
		return elements.get(element.getId());
	}


	protected SurveySkipRule getSkipRule(SurveySkipRule skipRule) {
		if (!skipRules.containsKey(skipRule.getId())) {
			SurveySkipRule copy = new SurveySkipRule();
			skipRules.put(skipRule.getId(), copy);
			skipRule.deepCopy(copy, this);
		}
		return skipRules.get(skipRule.getId());
	}

	protected SurveyValidationRule getValidationRule(SurveyValidationRule validationRule) {
		if (!validationRules.containsKey(validationRule.getId())) {
			SurveyValidationRule copy = new SurveyValidationRule(); 
			validationRules.put(validationRule.getId(), copy);
			validationRule.deepCopy(copy, this);
		}
		return validationRules.get(validationRule.getId());
	}
	
}

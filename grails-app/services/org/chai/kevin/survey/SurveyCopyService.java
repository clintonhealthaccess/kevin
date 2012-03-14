package org.chai.kevin.survey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.LanguageService;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormSkipRule;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.value.ExpressionService;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

public class SurveyCopyService {

	private SessionFactory sessionFactory;
	private LanguageService languageService;
	
	@Transactional(readOnly=false)
	public SurveyCopy<FormValidationRule> copyValidationRule(FormValidationRule rule) {
		SurveyCloner cloner = new SurveyCloner() {};
		FormValidationRule copy = new FormValidationRule();
		rule.deepCopy(copy, cloner);
		
		sessionFactory.getCurrentSession().save(copy);
		return new SurveyCopy<FormValidationRule>(copy);
	}
	
	@Transactional(readOnly=false)
	public SurveyCopy<Survey> copySurvey(Survey survey) {
		CompleteSurveyCloner cloner = new CompleteSurveyCloner(survey);
		cloner.cloneTree();
		sessionFactory.getCurrentSession().save(cloner.getSurvey());
		
		cloner.cloneRules();
		for (FormValidationRule validationRule : cloner.getValidationRules()) {
			sessionFactory.getCurrentSession().save(validationRule);
		}
		for (FormSkipRule skipRule : cloner.getSkipRules()) {
			sessionFactory.getCurrentSession().save(skipRule);
		}
		
		return new SurveyCopy<Survey>(cloner.getSurvey(), cloner.getUnchangedValidationRules(), cloner.getUnchangedSkipRules());
	}
	
	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private class CompleteSurveyCloner extends SurveyCloner {

		private Survey survey;
		private Survey copy;

		private List<SurveyElement> oldElements = new ArrayList<SurveyElement>();
		private Map<Long, SurveyProgram> programs = new HashMap<Long, SurveyProgram>();
		private Map<Long, SurveySection> sections = new HashMap<Long, SurveySection>();
		private Map<Long, SurveyQuestion> questions = new HashMap<Long, SurveyQuestion>();
		private Map<Long, SurveyElement> elements = new HashMap<Long, SurveyElement>();
		private Map<Long, SurveySkipRule> skipRules = new HashMap<Long, SurveySkipRule>();
		private Map<Long, FormValidationRule> validationRules = new HashMap<Long, FormValidationRule>();

		private Map<FormValidationRule, Long> unchangedValidationRules = new HashMap<FormValidationRule, Long>();
		private Map<FormSkipRule, Long> unchangedSkipRules = new HashMap<FormSkipRule, Long>();
		
		CompleteSurveyCloner(Survey survey) {
			this.survey = survey;
		}

		Survey getSurvey() {
			return copy;
		}
		
		Map<FormValidationRule, Long> getUnchangedValidationRules() {
			return unchangedValidationRules;
		}

		Map<FormSkipRule, Long> getUnchangedSkipRules() {
			return unchangedSkipRules;
		}

		void cloneTree() {
			this.getSurvey(survey);
		}
		
		void cloneRules() {
			if (copy.getId() == null) throw new IllegalStateException();
			survey.copyRules(copy, this);
			for (FormElement element : oldElements) {
				element.copyRules(elements.get(element.getId()), this);
			}
		}
		
		public Collection<FormValidationRule> getValidationRules() {
			return validationRules.values();
		}
		
		public Collection<SurveySkipRule> getSkipRules() {
			return skipRules.values();
		}
		
		@Override
		public void addUnchangedValidationRule(FormValidationRule rule, Long id) {
			this.unchangedValidationRules.put(rule, id);
		}

		@Override
		public void addUnchangedSkipRule(SurveySkipRule rule, Long id) {
			this.unchangedSkipRules.put(rule, id);
		}

		/* (non-Javadoc)
		 * @see org.chai.kevin.survey.SurveyCloner#getExpression(java.lang.String, org.chai.kevin.survey.FormValidationRule)
		 */
		@Override
		public String getExpression(String expression, FormValidationRule rule) {
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
		
		/* (non-Javadoc)
		 * @see org.chai.kevin.survey.SurveyCloner#getExpression(java.lang.String, org.chai.kevin.survey.SurveySkipRule)
		 */
		@Override
		public String getExpression(String expression, FormSkipRule rule) {
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
		
		/* (non-Javadoc)
		 * @see org.chai.kevin.survey.SurveyCloner#getSurvey(org.chai.kevin.survey.Survey)
		 */
		@Override
		public Survey getSurvey(Survey survey) {
			if (!survey.equals(this.survey)) throw new IllegalArgumentException();
			if (copy == null) {
				copy = new Survey(); 
				survey.deepCopy(copy, this);
				for (String language : languageService.getAvailableLanguages()) {
					// TODO localize "copy"
					copy.getNames().put(language, survey.getNames().get(language) + " (copy)");
				}
			}
			return copy;
		}
		
		/* (non-Javadoc)
		 * @see org.chai.kevin.survey.SurveyCloner#getProgram(org.chai.kevin.survey.SurveyProgram)
		 */
		@Override
		public SurveyProgram getProgram(SurveyProgram program) {
			if (!programs.containsKey(program.getId())) {
				SurveyProgram copy = new SurveyProgram(); 
				programs.put(program.getId(), copy);
				program.deepCopy(copy, this);
			}
			return programs.get(program.getId());
		}
		
		/* (non-Javadoc)
		 * @see org.chai.kevin.survey.SurveyCloner#getSection(org.chai.kevin.survey.SurveySection)
		 */
		@Override
		public SurveySection getSection(SurveySection section) {
			if (!sections.containsKey(section.getId())) {
				SurveySection copy = new SurveySection();
				sections.put(section.getId(), copy);
				section.deepCopy(copy, this);
			}
			return sections.get(section.getId());
		}

		/* (non-Javadoc)
		 * @see org.chai.kevin.survey.SurveyCloner#getQuestion(org.chai.kevin.survey.SurveyQuestion)
		 */
		@Override
		public SurveyQuestion getQuestion(SurveyQuestion question) {
			if (!questions.containsKey(question.getId())) {
				SurveyQuestion copy = question.newInstance();
				questions.put(question.getId(), copy);
				question.deepCopy(copy, this);
			}
			return questions.get(question.getId());

		}

		/* (non-Javadoc)
		 * @see org.chai.kevin.survey.SurveyCloner#getElement(org.chai.kevin.survey.SurveyElement)
		 */
		@Override
		public <T extends FormElement> T getElement(T element) {
			if (element == null) return null;
			
			if (element instanceof SurveyElement) {
				SurveyElement surveyElement = (SurveyElement)element;
				if (!surveyElement.getSurvey().equals(survey)) {
					return element;
				}
				
				if (!elements.containsKey(element.getId())) {
					SurveyElement copy = new SurveyElement(); 
					elements.put(element.getId(), copy);
					element.deepCopy(copy, this);
					oldElements.add(surveyElement);
				}
			}
			
			return (T)elements.get(element.getId());
		}


		/* (non-Javadoc)
		 * @see org.chai.kevin.survey.SurveyCloner#getSkipRule(org.chai.kevin.survey.SurveySkipRule)
		 */
		@Override
		public SurveySkipRule getSkipRule(SurveySkipRule skipRule) {
			if (!skipRules.containsKey(skipRule.getId())) {
				SurveySkipRule copy = new SurveySkipRule();
				skipRules.put(skipRule.getId(), copy);
				skipRule.deepCopy(copy, this);
			}
			return skipRules.get(skipRule.getId());
		}

		/* (non-Javadoc)
		 * @see org.chai.kevin.survey.SurveyCloner#getValidationRule(org.chai.kevin.survey.FormValidationRule)
		 */
		@Override
		public FormValidationRule getValidationRule(FormValidationRule validationRule) {
			if (!validationRules.containsKey(validationRule.getId())) {
				FormValidationRule copy = new FormValidationRule(); 
				validationRules.put(validationRule.getId(), copy);
				validationRule.deepCopy(copy, this);
			}
			return validationRules.get(validationRule.getId());
		}
		
	}

	
}

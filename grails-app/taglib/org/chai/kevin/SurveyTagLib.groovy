package org.chai.kevin

import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveySection;
import org.chai.kevin.survey.SurveyValidationRule;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.survey.SurveyElement;

class SurveyTagLib {

	def renderUserErrors = {attrs, body ->
		def enteredValue = attrs['element']
		def prefix = attrs['suffix']
		
		def rules = getRules(enteredValue?.getErrors(prefix));
		if (enteredValue != null && !rules.empty) {
			boolean hasErrors = hasErrors(rules)

			def errors = []			
			rules.each { rule ->
				def error = [:]
				error.displayed = (hasErrors && !rule.allowOutlier) || (!hasErrors && rule.allowOutlier)
				if (error.displayed) error.message = replacePlaceHolders(g.i18n(field: rule.messages).toString(), rule.dependencies, enteredValue.entity)
				error.rule = rule
				error.suffix = prefix
				error.accepted = enteredValue.isAcceptedWarning(rule, prefix)
				errors.add(error)
			} 
			out << g.render(template: '/survey/errors', model: [errors: errors, surveyElement: enteredValue.surveyElement])
		}
	}
	
	def getRules(def errors) {
		def rules = []
		if (errors != null) errors.each { id ->
			rules.add(SurveyValidationRule.get(id))
		}
		return rules;
	}
	
	boolean hasErrors(def errors) {
		for (def error : errors) {
			if (!error.allowOutlier) return true;
		}
		return false
	}
	
	def replacePlaceHolders(String message, List<SurveyElement> elements, DataEntity entity) {
		String[] placeholders = StringUtils.substringsBetween(message, "{", "}")
		String result = message;
		for (String placeholder : placeholders) {
			Integer id = null
			String text = null
			if (NumberUtils.isNumber(placeholder)) id = Integer.parseInt(placeholder);
			else {
				String[] parts = StringUtils.split(placeholder, ',', 2)
				if (NumberUtils.isNumber(parts[0])) {
					id = Integer.parseInt(parts[0]);
					text = parts[1]
				}
			}
			
			if (id != null) {
				SurveyElement surveyElement = elements[id];
				SurveySection section = surveyElement.surveyQuestion.section
				Survey survey = section.objective.survey 
				String replacement = 
					'<a href="'+createLink(controller: "editSurvey", action: "sectionPage", params: [section: section.id, organisation: entity.id], fragment: 'element-'+surveyElement.id)+'">'+
					(text!=null?text:surveyElement.id)+'</a>'
				result = StringUtils.replace(result, "{"+placeholder+"}", replacement);
			}
		}
		return result
	}
	
}

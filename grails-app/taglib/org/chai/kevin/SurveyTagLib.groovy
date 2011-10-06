package org.chai.kevin

import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveySection;
import org.chai.kevin.survey.SurveyValidationRule;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.survey.SurveyElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;

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
				if (error.displayed) error.message = replacePlaceHolders(g.i18n(field: rule.messages).toString(), rule.dependencies, enteredValue.organisationUnit)
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
	
	def replacePlaceHolders(String message, List<SurveyElement> elements, OrganisationUnit organisationUnit) {
		String[] placeholders = StringUtils.substringsBetween(message, "{", "}")
		String result = message;
		for (String integer : placeholders) {
			if (!NumberUtils.isNumber(integer)) continue;
			SurveyElement surveyElement = elements[Integer.parseInt(integer)];
			SurveySection section = surveyElement.surveyQuestion.section
			Survey survey = section.objective.survey 
			String replacement = 
				'<a href="'+g.createLink(controller: "survey", action: "sectionPage", params: [section: section.id, organisation: organisationUnit.id], fragment: 'element-'+surveyElement.id)+'">'+
				surveyElement.id +
				'</a>'
			result = StringUtils.replace(result, "{"+integer+"}", replacement);
		}
		return result
	}
	
}

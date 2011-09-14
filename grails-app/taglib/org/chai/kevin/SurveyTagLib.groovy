package org.chai.kevin

import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveySection;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.survey.SurveyElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;

class SurveyTagLib {
	
	def renderUserErrors = {attrs, body ->
		def enteredValue = attrs['element']
		def prefix = attrs['suffix']
		
		def errors = enteredValue?.getErrors(prefix)
		if (enteredValue != null && !errors.empty) {
			boolean hasErrors = hasErrors(errors)
			
			out << "<ul>"
			errors.each { rule ->
				if ((hasErrors && !rule.allowOutlier) || (!hasErrors && rule.allowOutlier)) {
					out << "<li>"
					def message = replacePlaceHolders(g.i18n(field: rule.validationMessage.messages).toString(), rule.dependencies, enteredValue.organisationUnit)
					out << g.render(template:"/survey/error", model: [message: message, surveyElement: enteredValue.surveyElement, rule: rule])
					out << "</li>"
				}
			}
			out << "</ul>"
		}
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

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
		def surveyElementValue = attrs['element']
		if (surveyElementValue !=null && !surveyElementValue.displayedErrors.empty) {
			out << "<ul>"
			surveyElementValue.displayedErrors.each { rule ->
				out << "<li>"
				def message = replacePlaceHolders(g.i18n(field: rule.validationMessage.messages).toString(), rule.dependencies, surveyElementValue.organisation.organisationUnit)
				if (!rule.allowOutlier) out << message 
				else out << g.render(template:"/survey/outlier", model: [message: message, surveyElement: surveyElementValue.surveyElement, rule: rule])
				out << "</li>"	
			}
			out << "</ul>"
		}
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

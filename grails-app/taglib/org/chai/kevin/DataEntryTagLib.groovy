package org.chai.kevin

import org.chai.kevin.survey.SurveyElement;
import java.util.Comparator;

import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormValidationRule;
import org.chai.location.DataLocation;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveySection;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;
import org.chai.kevin.LanguageService;

class DataEntryTagLib {

	def languageService	
	
	def eachOption = { attrs, body ->
		if (log.isDebugEnabled()) log.debug('eachOption(attrs='+attrs+',body='+body+')')
		
		def enume = attrs['enum']
		def var = attrs['var']
		
		def options = enume==null?[]:enume.activeEnumOptions?.sort({it.getOrders(languageService.currentLocale)})

		for (option in options) {
			if (var) {
				out << body([(var):option])
			}	
			else {
				out << body(option)
			}
		}
	}
	
	def renderUserErrors = { attrs, body ->
		if (log.isDebugEnabled()) log.debug('renderErrors(attrs='+attrs+',body='+body+')')
		
		def element = attrs['element']
		def validatable = attrs['validatable']
		def prefix = attrs['suffix']
		def location = attrs['location']
		
		if (log.isDebugEnabled()) log.debug('rendering errors for element:'+element+', validatable:'+validatable+', prefix:'+prefix)
		

		def rules = getRules(validatable?.getErrorRules(prefix));
		if (!rules.empty) {
			boolean hasErrors = hasErrors(rules)

			def errors = []
			rules.each { rule ->
				def error = [:]
				error.displayed = (hasErrors && !rule.allowOutlier) || (!hasErrors && rule.allowOutlier)
				if (error.displayed) error.message = replacePlaceHolders(g.i18n(field: rule.messages).toString(), rule.dependencies, location)
				error.rule = rule
				error.suffix = prefix
				error.accepted = validatable.isAcceptedWarning(rule, prefix)
				errors.add(error)
			}
			out << g.render(template: '/tags/dataEntry/errors', model: [errors: errors, element: element])
		}
	}
	
	def getRules(def errors) {
		def rules = []
		if (errors != null) errors.each { id ->
			rules.add(FormValidationRule.get(id))
		}
		return rules;
	}
	
	boolean hasErrors(def errors) {
		for (def error : errors) {
			if (!error.allowOutlier) return true;
		}
		return false
	}
	
	
	def replacePlaceHolders(String message, List<SurveyElement> elements, DataLocation location) {
		if (log.isDebugEnabled()) log.debug('replacePlaceHolders(${message}, ${elements}, ${location})')
		
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
				FormElement element = elements[id];
				String replacement =
					'<a href="'+createLink(controller: "formElement", action: "view", params: [id:element.id,location:location.id])+'">'+(text!=null?text:element.id)+'</a>'
				result = StringUtils.replace(result, "{"+placeholder+"}", replacement);
			}
		}
		return result
	}
	
}

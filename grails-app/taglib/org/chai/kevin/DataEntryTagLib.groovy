package org.chai.kevin

import org.chai.kevin.survey.SurveyElement;
import java.util.Comparator;

import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveySection;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.survey.SurveyElement;

class DataEntryTagLib {

	def languageService
	
	def value = {attrs, body ->
		if (log.isDebugEnabled()) log.debug('value(attrs='+attrs+',body='+body+')')
		
		def type = attrs['type']
		def value = attrs['value']
		def enums = attrs['enums']
		def nullText = attrs['nullText']
		
		def result = null
		if (value != null && !value.isNull()) {
			switch (type.type) {
				case (ValueType.ENUM):
					def enume = enums?.get(type.enumCode)
					if (enume == null) result = value.enumValue
					else {
						def option = enume?.getOptionForValue(value.enumValue)
						if (option == null) result = value.enumValue
						else result = languageService.getText(option.names)
					}
					break;
				case (ValueType.MAP):
					// TODO
				case (ValueType.LIST):
					// TODO
				default:
					result = value.stringValue
			}
		}
		if (result == null && nullText != null) out << nullText
		else out << result
	}
	
	def eachOption = { attrs, body ->
		if (log.isDebugEnabled()) log.debug('eachOption(attrs='+attrs+',body='+body+')')
		
		def enume = attrs['enum']
		def var = attrs['var']
		
		def options = enume==null?[]:enume.activeEnumOptions?.sort(getOrderingComparator())

		for (option in options) {
			if (var) {
				out << body([(var):option])
			}	
			else {
				out << body(option)
			}
		}
	}
	
	private Comparator<Orderable<Ordering>> getOrderingComparator() {
		return Ordering.getOrderableComparator(languageService.currentLanguage, languageService.fallbackLanguage);
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
					'<a href="'+createLink(controller: "formElement", action: "view", params: [id:element.id, dataLocation:location.id])+'">'+(text!=null?text:element.id)+'</a>'
				result = StringUtils.replace(result, "{"+placeholder+"}", replacement);
			}
		}
		return result
	}
	
}

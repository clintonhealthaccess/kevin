package org.chai.kevin

class SurveyTagLib {
	
	def renderUserErrors = {attrs, body ->
		def surveyElementValue = attrs['element']
		if (!surveyElementValue.valid) {
			out << "<ul>"
			surveyElementValue.invalidRules.each { rule ->
				out << "<li>"
				out << replacePlaceHolders(g.i18n(field: rule.validationMessage.messages), rule.dependencies)
				out << "</li>"	
			}
			out << "</ul>"
		}
	}
	
	def replacePlaceHolders(def message, def elements) {
		// TODO 
		return message
	}
	
}

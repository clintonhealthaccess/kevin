package org.chai.kevin

import org.apache.commons.lang.LocaleUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

class LocaleService {

	def getAvailableLanguages() {
		return ["en", "fr", "rw"]
	}
	
	def getCurrentLanguage() {
		return "en";
	}
	
	def getFallbackLanguage() {
		return "en";
	}
	
}

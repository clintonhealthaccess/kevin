package org.chai.kevin

import org.apache.commons.lang.LocaleUtils;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

class LocaleService {

	def getAvailableLanguages() {
		List<String> languages = ConfigurationHolder.config.site.languages;
		return languages;
	}
	
	def getCurrentLanguage() {
		Locale locale = RequestContextUtils.getLocale(RequestContextHolder.currentRequestAttributes().getRequest());
		return locale.getLanguage();
	}
	
	def getFallbackLanguage() {
		return ConfigurationHolder.config.site.fallback.language;
	}
	
}

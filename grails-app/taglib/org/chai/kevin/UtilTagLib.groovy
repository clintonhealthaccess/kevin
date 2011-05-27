package org.chai.kevin

class UtilTagLib {

	def localeService;
	
	def dateFormat = { attrs, body ->
		out << new java.text.SimpleDateFormat(attrs.format).format(attrs.date)
	}
	
	def i18nInput = { attrs, body ->
		if (attrs["type"] == null) attrs["type"] = 'text'
		attrs["locales"] = localeService.getAvailableLanguages();
		out << render(template:"/templates/i18nInput", model: attrs)
	}
	
	def i18nTextarea = { attrs, body ->
		if (attrs["type"] == null) attrs["type"] = 'text'
		if (attrs["rows"] == null) attrs["rows"] = '4'
		attrs["locales"] = localeService.getAvailableLanguages();
		out << render(template:"/templates/i18nTextarea", model: attrs)
	}
	
	def input = { attrs, body ->
		if (attrs["type"] == null) attrs["type"] = 'text'
		out << render(template:"/templates/input", model: attrs)
	}
	
	def selectFromEnum = { attrs, body ->
		out << render(template:"/templates/selectFromEnum", model: attrs)
	}
	
	def textarea = { attrs, body ->
		if (attrs["type"] == null) attrs["type"] = 'text'
		if (attrs["rows"] == null) attrs["rows"] = '1'
		out << render(template:"/templates/textarea", model: attrs)
	}
	
	def locales = { attrs, body ->
		attrs["locales"] = localeService.getAvailableLanguages();
		out << render(template:"/templates/locales", model: attrs)
	}
	
	def i18n = { attrs, body ->
		def text = attrs['field'].get(localeService.getCurrentLanguage())
		if (text == null || text.equals("")) text == attrs['field'].get(localeService.getFallbackLanguage())
		out << text 
	}
	
}

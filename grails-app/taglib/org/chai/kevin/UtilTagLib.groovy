package org.chai.kevin

/*
* Copyright (c) 2011, Clinton Health Access Initiative.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the <organization> nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

class UtilTagLib {

	def localeService;
	
	def toHtml = {attrs, body ->
		out << attrs.value.replaceAll("(\\r\\n|\\n)", "<br/>").replaceAll("( )", "&nbsp;")	
	}
	
	def dateFormat = { attrs, body ->
		out << new java.text.SimpleDateFormat(attrs.format).format(attrs.date)
	}
	
	def input = { attrs, body ->
		if (attrs["type"] == null) attrs["type"] = 'text'
		out << render(template:"/tags/input", model: attrs)
	}
	
	def i18nInput = { attrs, body ->
		if (attrs["type"] == null) attrs["type"] = 'text'
		attrs["locales"] = localeService.getAvailableLanguages();
		out << render(template:"/tags/i18nInput", model: attrs)
	}
	
	def textarea = { attrs, body ->
		if (attrs["type"] == null) attrs["type"] = 'text'
		if (attrs["rows"] == null) attrs["rows"] = '1'
		out << render(template:"/tags/textarea", model: attrs)
	}
	
	def i18nTextarea = { attrs, body ->
		if (attrs["type"] == null) attrs["type"] = 'text'
		if (attrs["rows"] == null) attrs["rows"] = '4'
		if (attrs["width"] == null) attrs["width"] = '300'
		if (attrs["readonly"] == null) attrs["readonly"] = false
		attrs["locales"] = localeService.getAvailableLanguages();
		out << render(template:"/tags/i18nTextarea", model: attrs)
	}
	def i18nRichTextarea = { attrs, body ->
		if (attrs["type"] == null) attrs["type"] = 'text'
		if (attrs["rows"] == null) attrs["rows"] = '4'
		if (attrs["width"] == null) attrs["width"] = '300'
		attrs["locales"] = localeService.getAvailableLanguages();
		out << render(template:"/tags/i18nRichTextarea", model: attrs)
	}
	
	def searchBox = { attrs, body ->
		if (attrs['controller'] == null) attrs['controller'] = controllerName;
		if (attrs['action'] == null) attrs['action'] = actionName;
		attrs['hiddenParams'] = new HashMap(attrs['params']?attrs['params']:params)
		attrs['hiddenParams'].remove('max')
		attrs['hiddenParams'].remove('offset')
		attrs['hiddenParams'].remove('controller')
		attrs['hiddenParams'].remove('action')
		attrs['hiddenParams'].remove('q')
		out << render(template:"/tags/searchBox", model: attrs);
	}
	
	def selectFromEnum = { attrs, body ->
		out << render(template:"/tags/selectFromEnum", model: attrs)
	}
			
	def locales = { attrs, body ->
		attrs["locales"] = localeService.getAvailableLanguages();
		out << render(template:"/tags/locales", model: attrs)
	}
	
	def i18n = { attrs, body ->
		def text = localeService.getText(attrs['field'])
		out << text 
	}
}

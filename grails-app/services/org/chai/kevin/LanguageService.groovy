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

import org.apache.commons.lang.LocaleUtils
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Type
import org.chai.kevin.data.Type.ValueType
import org.chai.kevin.util.Utils
import org.chai.kevin.value.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.support.RequestContextUtils

class LanguageService implements ApplicationContextAware {
	
	ApplicationContext applicationContext
	
	def grailsApplication
	def dataServiceBean
	
	static transactional = false
	
	DataService getDataService() {
		return applicationContext.getBean("dataService")
	}
	
	List<Locale> getAvailableLocales() {
		return getAvailableLanguages().collect {LocaleUtils.toLocale(it)}
	}
	
	List<String> getAvailableLanguages() {
		return grailsApplication.config.i18nFields.locales;
	}
	
	String getFallbackLanguage() {
		return grailsApplication.config.site.fallback.language
	}
	
	Locale getCurrentLocale() {
		return RequestContextUtils.getLocale(RequestContextHolder.currentRequestAttributes().getRequest());
	}
	
	String getCurrentLanguage() {
		return getCurrentLocale().getLanguage();
	}
	
	String getStringValue(Value value, Type type, def enums = null, def format = null, def zero = null, def rounded = null) {
		if (value == null || value.isNull()) return null
		def result;
		switch (type.type) {
			case ValueType.BOOL:
				if (value.booleanValue) result = '&#10003;'
				else result = '&#10007;'
				break;
			case (ValueType.ENUM):
				def enume = null
				if (enums == null) enume = dataService.findEnumByCode(type.enumCode);
				else enume = enums?.get(type.enumCode)
				if (enume == null) result = value.enumValue
				else {
					def option = enume?.getOptionForValue(value.enumValue)
					if (option == null) result = value.enumValue
					else result = Utils.noNull(option.names)
				}
				break;
			case (ValueType.NUMBER):
				if (zero != null && value.numberValue == 0) result = zero
				else result = Utils.formatNumber(format, rounded!=null?value.numberValue.round(Integer.parseInt(rounded)):value.numberValue)
				break;
			case (ValueType.LIST):
				result = []
				if(value.listValue != null && !value.listValue.empty){
					def listValues = value.listValue.sort()
					for(Value listValue : listValues){
						if (log.isDebugEnabled()) log.debug("getStringValue(listType="+type.listType+", listValue="+listValue.value+")");
						def stringValue = getStringValue(listValue, type.listType)
						result.add(stringValue)
					}
					switch(type.listType.type){
						case ValueType.BOOL:
							result = result.join('&nbsp;&nbsp;&nbsp;')
							break;
						case ValueType.ENUM:
						case ValueType.NUMBER:
						case ValueType.STRING:
						case ValueType.TEXT:
							result = result.join(', ')
							break;
					}
				}
				break;
			case (ValueType.MAP):
				// TODO
				break;
			default:
				result = value.stringValue
		}
		if (log.isDebugEnabled()) log.debug("getStringValue(type="+type.type+", result="+result+")");
		return result;
	}	
}
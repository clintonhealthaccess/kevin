package org.chai.kevin;

import org.chai.kevin.value.Value;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.Utils
import com.ibm.jaql.json.type.JsonValue;

class LanguageServiceSpec extends IntegrationTests {

	def languageService

	def "get string value for value with number, percentage format, and rounded to 2 places"() {
		
		when:
		def value = Value.VALUE_NUMBER(0.579)
		def type = Type.TYPE_NUMBER()
		def format = "#%"
		def rounded = '2'
		def stringValue = languageService.getStringValue(value, type, null, format, null, rounded)
		
		then:
		stringValue == "58%"
		
	}
	
	def "get double from string value with percentage format"() {
		when:
		def value = "58%"
		def doubleValue = Double.parseDouble(Utils.parseNumber(value))
		
		then:
		doubleValue == 58
	}
	
}
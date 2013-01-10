package org.chai.kevin;

import org.chai.kevin.value.Value;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.Utils
import com.ibm.jaql.json.type.JsonValue;

class LanguageServiceSpec extends IntegrationTests {

	def languageService
	
	def "get double from string value with percentage format"() {
		when:
		def value = "58%"
		def doubleValue = Double.parseDouble(Utils.parseNumber(value))
		
		then:
		doubleValue == 58
	}
	
}
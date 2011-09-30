package org.chai.kevin;

import org.chai.kevin.value.Value;

import com.ibm.jaql.json.type.JsonValue;

class JaqlServiceSpec extends IntegrationTests {

	def jaqlService 

	def "get json value twice in a row resets query"() {
		
		when:
		jaqlService.getJsonValue("\$1", [:])
		
		then: 
		thrown IllegalArgumentException
		
		expect:
		jaqlService.getJsonValue("\$1", ["\$1": "0"]).toString() == "0"
		
		when:
		jaqlService.getJsonValue("\$1", [:])
		
		then:
		thrown IllegalArgumentException
	}
		
}

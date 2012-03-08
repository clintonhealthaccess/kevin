package org.chai.kevin;

import org.chai.kevin.value.Value;
import org.chai.kevin.data.Type;
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
		
	def "evaluate with null values"() {
		
		expect:
		jaqlService.evaluate("\$1.isNull == true", Type.TYPE_BOOL(), ['1': Value.NULL_INSTANCE()], ['1': Type.TYPE_NUMBER()]).equals( Value.VALUE_BOOL(true) )
		jaqlService.evaluate("\$1.isNull == false", Type.TYPE_BOOL(), ['1': Value.NULL_INSTANCE()], ['1': Type.TYPE_NUMBER()]).equals( Value.VALUE_BOOL(false) )
		
	}
	
}

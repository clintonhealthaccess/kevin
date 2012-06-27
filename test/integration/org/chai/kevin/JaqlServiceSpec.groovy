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
		jaqlService.evaluate("\$1 == \"null\"", Type.TYPE_BOOL(), ['1': Value.NULL_INSTANCE()], ['1': Type.TYPE_NUMBER()]).equals( Value.VALUE_BOOL(true) )
		jaqlService.evaluate("\$1 != \"null\"", Type.TYPE_BOOL(), ['1': Value.NULL_INSTANCE()], ['1': Type.TYPE_NUMBER()]).equals( Value.VALUE_BOOL(false) )
		jaqlService.evaluate("if (\$1 == \"null\") 0 else \$1", Type.TYPE_NUMBER(), ['1': Value.VALUE_NUMBER(1)], ['1': Type.TYPE_NUMBER()]).equals( Value.VALUE_NUMBER(1) )
	}
	
	def "evaluate with end-of-line"() {
		expect:
		jaqlService.evaluate("1==\n1", Type.TYPE_BOOL(), [:], [:]).equals(Value.VALUE_BOOL(true))
	}
	
	def "evaluate roundup"() {
		expect:
		jaqlService.evaluate("roundup(0.1)", Type.TYPE_NUMBER(), [:], [:]).equals(Value.VALUE_NUMBER(1d))
		jaqlService.evaluate("roundup(0.5)", Type.TYPE_NUMBER(), [:], [:]).equals(Value.VALUE_NUMBER(1d))
		jaqlService.evaluate("roundup(1)", Type.TYPE_NUMBER(), [:], [:]).equals(Value.VALUE_NUMBER(1d))
	}
	
}

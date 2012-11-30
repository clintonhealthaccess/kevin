package org.chai.kevin;

import org.chai.kevin.util.JSONUtils;
import grails.plugin.spock.UnitSpec;
import static org.junit.Assert.*;

class JSONUtilsUnitSpec extends UnitSpec {

	def "get json from map with integer returns null"() {
		when:
		def map = [1:'test']
		
		then:
		JSONUtils.getJSONFromMap(map) == null
	}
	
	def "get json from map string"() {
		when:
		def map = ["1": '"test"']
		
		then:
		JSONUtils.getJSONFromMap(map) == '{"1":"\\"test\\""}'
	}
	
	def "get map from json string"() {
		when:
		def json = '{"1":" \\"test\\""}'
		
		then:
		JSONUtils.getMapFromJSON(json) == ["1": ' "test"']
	}
	
	def "get json from empty map"() {
		when:
		def map = [:]
		
		then:
		JSONUtils.getJSONFromMap(map) == '{}'
	}
	
}

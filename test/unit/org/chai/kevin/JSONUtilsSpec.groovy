package org.chai.kevin;

import org.chai.kevin.util.JSONUtils;
import grails.plugin.spock.UnitSpec;
import static org.junit.Assert.*;

class JSONUtilsSpec extends UnitSpec {

	def "get json from map with integer returns null"() {
		when:
		def map = [1:'test']
		
		then:
		map == null
//		JSONUtils.getJSONFromMap(map) == '{"1":"test"}'
		
	}
	
}

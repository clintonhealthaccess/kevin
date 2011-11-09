package org.chai.kevin

import grails.plugin.spock.UnitSpec;

class JSONMapSpec extends UnitSpec {

	
	def "test json output with string"() {
		when:
		JSONMap<String> map = new JSONMap<String>();
		map['en'] = 'test'
		
		then:
		map.getJsonText() == '{"en":"test"}'
	}
	
	def "test json output with integer"() {
		when:
		JSONMap<Integer> map = new JSONMap<Integer>();
		map['en'] = 10
		
		then:
		map.getJsonText() == '{"en":10}'
	}
	
	def "test json output with embedded map"() {
		when:
		JSONMap<Map<String, String>> map = new JSONMap<Map<String, String>>();
		map['1'] = ['DH': '10']
		
		then:
		map.getJsonText() == '{"1":{"DH":"10"}}'
	}
	
	def "test map from json map"() {
		when:
		JSONMap<Map<String, String>> map = new JSONMap<Map<String, String>>();
		map.setJsonText('{"1":{"DH":"10"}}');
		
		then:
		map['1'].equals(['DH': '10'])
		map['1']['DH'].equals('10')
	}
	
}

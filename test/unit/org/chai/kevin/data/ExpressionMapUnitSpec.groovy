package org.chai.kevin.data

import org.chai.kevin.json.JSONMap;

import grails.plugin.spock.UnitSpec;

class ExpressionMapUnitSpec extends UnitSpec {

	def "test simple map"() {
		
		when:
		def map = new Expressions([('1'): new JSONMap([('HC'): '$1 + $2'])])
		
		then:
		map[('1')].equals(['HC':'$1 + $2'])
		map[('1')]['HC'] == '$1 + $2'
		
	}
	
	def "test map with 2 periods"() {
		when:
		def map = new Expressions()
		map['1'] = [:]
		map['1']['type'] = '123'
		map['2'] = [:]
		map['2']['type'] = '456'
		
		then:
		map.equals(['1':['type':'123'],'2':['type':'456']])
		
	}
	
}

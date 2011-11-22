package org.chai.kevin.data

import org.chai.kevin.json.JSONMap;

import grails.plugin.spock.UnitSpec;

class ExpressionMapUnitSpec extends UnitSpec {

	def "test simple map"() {
		
		when:
		def map = new ExpressionMap([('1'): new JSONMap([('HC'): '$1 + $2'])])
		
		then:
		map[('1')].equals(['HC':'$1 + $2'])
		map[('1')]['HC'] == '$1 + $2'
		
	}
	
}

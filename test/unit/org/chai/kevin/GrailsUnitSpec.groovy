package org.chai.kevin

import grails.plugin.spock.UnitSpec;

class GrailsUnitSpec extends UnitTests {


	def "weird map bug"() {
		
		setup:
		def expression = new Expression(jsonNames:j(["en":"Constant 10"]), code:"CONST10", expression: "10", type: ValueType.VALUE)
		mockDomain(Expression, [expression])
		
		when:
		//expression = Expression.findByCode("CONST10")
		expression.properties = ["names[en]":"New name"]
//		expression.save()
				
		then:
		Expression.count() == 1
		
	}	
}

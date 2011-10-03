package org.chai.kevin;

import static org.junit.Assert.*;
import grails.validation.ValidationException;

import org.chai.kevin.data.Average;
import org.chai.kevin.data.Sum;
import org.chai.kevin.data.Type;

class CalculationSpec extends IntegrationTests {
	
	def "calculation type cannot be invalid"() {
		when:
		new Sum(expressions:[:], code:CODE(1), type: INVALID_TYPE).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new Average(expressions:[:], code:CODE(2), type: INVALID_TYPE).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "sum type cannot be of non-number"() {
		when:
		new Sum(expressions:[:], code:CODE(1), type:type).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		where:
		type << [Type.TYPE_BOOL(), Type.TYPE_DATE(), Type.TYPE_STRING(), Type.TYPE_ENUM(CODE(3))]
	}	
	
	def "average type cannot be of non-number"() {	
		when:
		new Average(expressions:[:], code:CODE(2), type:type).save(failOnError: true)
		
		then:
		thrown ValidationException

		where:
		type << [Type.TYPE_BOOL(), Type.TYPE_DATE(), Type.TYPE_STRING(), Type.TYPE_ENUM(CODE(3))]
	}
	
	def "cannot delete expression with associated calculation"() {
		when:
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "10")
		def average = new Average(expressions: [HEALTH_CENTER_GROUP: expression], type:Type.TYPE_NUMBER()).save(failOnError: true)
		expression.delete(flush: true)
		
		then:
		thrown Exception
	}
}

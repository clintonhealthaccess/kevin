package org.chai.kevin;

import static org.junit.Assert.*;
import grails.validation.ValidationException;

import org.chai.kevin.data.Average;
import org.chai.kevin.data.Sum;
import org.chai.kevin.data.Type;

class CalculationSpec extends IntegrationTests {
	
	def "calculation type cannot be invalid"() {
		when:
		newSum([:], CODE(1), new Type(INVALID_TYPE))
		
		then:
		thrown ValidationException
		
		when:
		newAverage([:], CODE(2), new Type(INVALID_TYPE))
		
		then:
		thrown ValidationException
	}
	
	def "sum type cannot be of non-number"() {
		when:
		newSum([:], CODE(1), type)
		
		then:
		thrown ValidationException
		
		where:
		type << [Type.TYPE_BOOL, Type.TYPE_DATE, Type.TYPE_STRING, Type.TYPE_ENUM(CODE(3))]
	}	
	
	def "average type cannot be of non-number"() {	
		when:
		newAverage([:], CODE(2), type)
		
		then:
		thrown ValidationException

		where:
		type << [Type.TYPE_BOOL, Type.TYPE_DATE, Type.TYPE_STRING, Type.TYPE_ENUM(CODE(3))]
	}
	
	def "cannot delete expression with associated calculation"() {
		when:
		def expression = expression(CODE(1), Type.TYPE_NUMBER, "10")
		def average = newAverage([HEALTH_CENTER_GROUP: expression], Type.TYPE_NUMBER)
		expression.delete()
		
		then:
		thrown Exception
	}
}

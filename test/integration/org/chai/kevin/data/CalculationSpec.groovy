package org.chai.kevin.data;

import static org.junit.Assert.*;
import grails.validation.ValidationException;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Aggregation;
import org.chai.kevin.data.Sum;
import org.chai.kevin.data.Type;

class CalculationSpec extends IntegrationTests {
	
	def "sum expression must be valid"() {
		when:
		new Sum(code:CODE(1), expression: "1").save(failOnError: true)
		
		then:
		Sum.count() == 1
		
		when:
		new Sum(code:CODE(2), expression: "1(").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "sum code must be unique"() {
		when:
		new Sum(code:CODE(1), expression: "1").save(failOnError: true)
		
		then:
		Sum.count() == 1
		
		when:
		new Sum(code:CODE(1), expression: "1").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "sum expression does not accept calculations"() {
		when:
		def sum = newSum("1", CODE(1))
		new Sum(code:CODE(1), expression: "\$"+sum.id).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "ratio expression must be valid"() {
		when:
		new Sum(code:CODE(1), expression: "1").save(failOnError: true)
		
		then:
		Sum.count() == 1
		
		when:
		new Sum(code:CODE(2), expression: "1(").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "aggregation expression must be valid"() {
		when:
		new Aggregation(code:CODE(1), expression: "1").save(failOnError: true)
		
		then:
		Aggregation.count() == 1
		
		when:
		new Aggregation(code:CODE(2), expression: "1(").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "aggregation expression does not accept calculations"() {
		when:
		def sum = newSum("1", CODE(1))
		new Aggregation(code:CODE(1), expression: "\$"+sum.id).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "sum code must not be null"() {
		when:
		new Sum(code:CODE(1), expression: "1").save(failOnError: true)
		
		then:
		Sum.count() == 1
		
		when:
		new Sum(expression: "1").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "ratio code must not be null"() {
		when:
		new Sum(code:CODE(1), expression: "1").save(failOnError: true)
		
		then:
		Sum.count() == 1
		
		when:
		new Sum(expression: "1").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "aggregation code must not be null"() {
		when:
		new Aggregation(code:CODE(1), expression: "1").save(failOnError: true)
		
		then:
		Aggregation.count() == 1
		
		when:
		new Aggregation(expression: "1").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
}

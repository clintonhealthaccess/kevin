package org.chai.kevin.data;

import static org.junit.Assert.*;
import grails.validation.ValidationException;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Aggregation;
import org.chai.kevin.data.Summ;
import org.chai.kevin.data.Type;

class CalculationSpec extends IntegrationTests {
	
	def "sum expression must be valid"() {
		when:
		new Summ(code:CODE(1), expression: "1").save(failOnError: true)
		
		then:
		Summ.count() == 1
		
		when:
		new Summ(code:CODE(2), expression: "1(").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "sum code must be unique"() {
		when:
		new Summ(code:CODE(1), expression: "1").save(failOnError: true)
		
		then:
		Summ.count() == 1
		
		when:
		new Summ(code:CODE(1), expression: "1").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "sum code must not be null"() {
		when:
		new Summ(code:CODE(1), expression: "1").save(failOnError: true)
		
		then:
		Summ.count() == 1
		
		when:
		new Summ(expression: "1").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "sum expression does not accept calculations"() {
		when:
		def sum = newSum("1", CODE(1))
		new Summ(code:CODE(1), expression: "\$"+sum.id).save(failOnError: true)
		
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
	
	def "aggregation code must be unique"() {
		when:
		new Aggregation(code:CODE(1), expression: "1").save(failOnError: true)
		
		then:
		Aggregation.count() == 1
		
		when:
		new Aggregation(code:CODE(1), expression: "1").save(failOnError: true)
		
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
	
	def "aggregation expression does not accept calculations"() {
		when:
		def sum = newSum("1", CODE(1))
		new Aggregation(code:CODE(1), expression: "\$"+sum.id).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "mode expression must be valid"() {
		when:
		new Mode(code:CODE(1), type: Type.TYPE_LIST(Type.TYPE_NUMBER()), expression: "1").save(failOnError: true)
		
		then:
		Mode.count() == 1
		
		when:
		new Mode(code:CODE(2), type: Type.TYPE_LIST(Type.TYPE_NUMBER()), expression: "1(").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "mode code must be unique"() {
		when:
		new Mode(code:CODE(1), type: Type.TYPE_LIST(Type.TYPE_NUMBER()), expression: "1").save(failOnError: true)
		
		then:
		Mode.count() == 1
		
		when:
		new Mode(code:CODE(1), type: Type.TYPE_LIST(Type.TYPE_NUMBER()), expression: "1").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "mode code must not be null"() {
		when:
		new Mode(code:CODE(1), type: Type.TYPE_LIST(Type.TYPE_NUMBER()), expression: "1").save(failOnError: true)
		
		then:
		Mode.count() == 1
		
		when:
		new Mode(type: Type.TYPE_LIST(Type.TYPE_NUMBER()), expression: "1").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "mode expression does not accept calculations"() {
		when:
		def sum = newSum("1", CODE(1))
		new Mode(code:CODE(1), type: Type.TYPE_LIST(Type.TYPE_NUMBER()), expression: "\$"+sum.id).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
}

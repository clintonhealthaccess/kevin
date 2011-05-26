package org.chai.kevin.maps;

import grails.validation.ValidationException;

import org.chai.kevin.Expression;
import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.IntegrationTests;

public class DomainSpec extends IntegrationTests {

	def setup() {
		IntegrationTestInitializer.createExpressions()
	}
	
	def "target constraint: code cannot be null"() {
		when:
		new MapsTarget(code:"CODE" ,expression: Expression.findByCode("CONST10")).save(failOnError:true)
		
		then:
		MapsTarget.count() == 1
		
		when:
		new MapsTarget(expression: Expression.findByCode("CONST10")).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
	def "target constraint: expression cannot be null"() {
		when:
		new MapsTarget(code:"CODE1" ,expression: Expression.findByCode("CONST10")).save(failOnError:true)
		
		then:
		MapsTarget.count() == 1
		
		when:
		new MapsTarget(code:"CODE2").save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
}

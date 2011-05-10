package org.hisp.dhis

import grails.validation.ValidationException;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Initializer;
import org.chai.kevin.Expression.ExpressionType;
import org.chai.kevin.IntegrationTests;
import org.hisp.dhis.dataelement.Constant;
import org.springframework.dao.DataIntegrityViolationException;

class DomainSpec extends IntegrationTests {

	private static final Log log = LogFactory.getLog(DomainSpec.class)

	def setup() {
		Initializer.createDummyStructure();
	}

	def "constant saved properly" () {
		when:
		new Constant(name:"Constant", shortName:"CONST", value:"10").save(failOnError: true)
		
		then:
		Constant.count() == 1
	}
	
	def "constant name cannot be empty"() {
		when:
		new Constant(name:"", shortName:"CONST", value:"10").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "constant name cannot be null"() {
		when:
		new Constant(shortName:"CONST", value:"10").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "constant value cannot be empty"() {
		when:
		new Constant(name:"Constant", shortName:"CONST", value:"").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "constant value cannot be null"() {
		when:
		new Constant(name:"Constant", shortName:"CONST").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
}

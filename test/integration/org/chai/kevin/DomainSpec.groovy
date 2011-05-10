package org.chai.kevin

import grails.validation.ValidationException;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Initializer;
import org.chai.kevin.Expression.ExpressionType;
import org.hisp.dhis.dataelement.Constant;
import org.springframework.dao.DataIntegrityViolationException;

class DomainSpec extends IntegrationTests {

	private static final Log log = LogFactory.getLog(DomainSpec.class)

	def setup() {
		Initializer.createDummyStructure();
	}
	
	def "expression type cannot be null"() {
		when:
		def expression = new Expression(name:"Expression")
		expression.save(failOnError:true)
		
		then:
		thrown ValidationException
		
	}
	
	def "expression name is unique"() {
		when:
		new Expression(name:"Expression", type:ExpressionType.VALUE, expression:"1").save(failOnError:true)
		
		then:
		Expression.count();
		
		when:
		new Expression(name:"Expression", type:ExpressionType.VALUE, expression:"1").save(failOnError:true)
		
		then:
		thrown ValidationException
		
	}
	
	def "expression can be a constant"() {
		setup:
		IntegrationTestInitializer.createConstants()
		
		when:
		new Expression(name:"Expression", type:ExpressionType.VALUE, expression:"[c"+Constant.findByName("Constant 1000").id+"]").save(failOnError:true)
		
		then:
		Expression.count() == 1;
		
	}
	
}

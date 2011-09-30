package org.chai.kevin

import grails.validation.ValidationException;

import org.chai.kevin.data.Average;
import org.chai.kevin.data.Expression;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.ExpressionValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class ExpressionSpec extends IntegrationTests {

	def "expression type cannot be invalid"() {
		when:
		newExpression(CODE(1), new Type(INVALID_TYPE), expression:"1")
		
		then:
		thrown ValidationException
	}

	def "expression type cannot be null"() {
		when:
		def expression = newExpression(CODE(1), null, "1")

		then:
		thrown ValidationException

	}

	def "expression code is unique"() {
		when:
		newExpression(CODE(1), Type.TYPE_NUMBER, "1")

		then:
		Expression.count();

		when:
		newExpression(CODE(1), Type.TYPE_NUMBER, "1")

		then:
		thrown ValidationException

	}
	
	def "expression value hashcode and equals"() {
		setup:
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER, "10")
		def organisationUnit = newOrganisationUnit(name: BUTARO)
		def period = newPeriod()

		when:
		def expr1 = newExpressionValue(expression, period, organisationUnit);
		def expr2 = newExpressionValue(expression, period, organisationUnit);

		then:
		expr1.hashCode() == expr2.hashCode();
		expr1.equals(expr2)
		expr2.equals(expr1)

		when:
		def set = new HashSet()
		set.add(expr1)

		then:
		set.contains(expr2)
	}

	def "invalid expression"() {
		when:
		newExpression(CODE(1), Type.TYPE_NUMBER, formula)

		then:
		thrown ValidationException

		where:
		formula << [
			"if((123) 1 else 0",
			"if(3) 3",
			"if(\$328==1 || \$286==1 || \$277==1 || \$215==1) \"&#10003;\" else \"NEGS\""
		]
	}

	
	//	def "expression can be a constant"() {
	//		setup:
	//		IntegrationTestInitializer.createConstants()
	//
	//		when:
	//		new newExpression(names:j(["en":"Expression"]), code:"EXPR", type:Type.TYPE_NUMBER, expression:"["+Constant.findByCode("CONST1").id+"]").save(failOnError:true)
	//
	//		then:
	//		Expression.count() == 1;
	//
	//	}
	
	
	//	def "expression date is updated on save"() {
	//		setup:
	//		new newExpression(code:"CODE", expression: "1", type: Type.TYPE_NUMBER, timestamp: new Date()).save(failOnError: true)
	//
	//		when:
	//		def expression = Expression.findByCode("CODE");
	//		def oldDate = expression.timestamp
	//
	//		then:
	//		oldDate != null
	//
	//		when:
	//		expression.save(failOnError: true)
	//		expression = Expression.findByCode("CODE");
	//		def newDate = expression.timestamp
	//
	//		then:
	//		!oldDate.equals(newDate)
	//
	//	}
}

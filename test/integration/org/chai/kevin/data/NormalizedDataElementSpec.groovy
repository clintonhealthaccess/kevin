package org.chai.kevin.data

import grails.validation.ValidationException;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Period;
import org.chai.kevin.data.Average;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.value.NormalizedDataElementValue;

class NormalizedDataElementSpec extends IntegrationTests {

	def "normalized data element type is valid"() {
		when:
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap:e([:])).save(failOnError: true)
		
		then:
		NormalizedDataElement.count() == 1
		NormalizedDataElement.list()[0].type.jsonValue == "{\"type\":\"number\"}";
	}
	
	def "normalized data element type cannot be invalid"() {
		when:
		new NormalizedDataElement(code: CODE(1), type: INVALID_TYPE, expressionMap:e([:])).save(failOnError: true)
		
		then:
		thrown ValidationException
	}

	def "normalized data element type cannot be null"() {
		when:
		def normalizedDataElement = new NormalizedDataElement(code: CODE(1), expressionMap:e([:])).save(failOnError: true)

		then:
		thrown ValidationException
	}

	// TODO uncomment when GRAILS-8615 is fixed
//	def "normalized data element code is unique"() {
//		when:
//		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap:e([:])).save(failOnError: true)
//
//		then:
//		NormalizedDataElement.count() == 1;
//
//		when:
//		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap:e([:])).save(failOnError: true)
//
//		then:
//		thrown ValidationException
//	}
	
	def "normalized data element value hashcode and equals"() {
		setup:
		setupLocationTree()
		def type = newDataLocationType(DISTRICT_HOSPITAL_GROUP)
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))

		when:
		def expr1 = new NormalizedDataElementValue(data: normalizedDataElement, period: period, location:  DataLocation.findByCode(BUTARO));
		def expr2 = new NormalizedDataElementValue(data: normalizedDataElement, period: period, location:  DataLocation.findByCode(BUTARO));

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

	def "invalid expressions in map"() {
		when:
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: e(["1":["DH":formula]])).save(failOnError: true)

		then:
		thrown ValidationException

		where:
		formula << [
			"if((123) 1 else 0",
			"if(3) 3",
			"if(\$328==1 || \$286==1 || \$277==1 || \$215==1) \"&#10003;\" else \"NEGS\""
		]
	}
	
	def "expressions in map only contain data elements"() {
		when:
		def dataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([:]))
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: e(["1":["DH":"\$"+dataElement.id]])).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "expressions can be empty"() {
		when:
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: e(["1":["DH":""]])).save(failOnError: true)
		
		then:
		NormalizedDataElement.count() == 1
		
	}	
	
	//	def "expression can be a constant"() {
	//		setup:
	//		IntegrationTestInitializer.createConstants()
	//
	//		when:
	//		new newExpression(names:j(["en":"Expression"]), code:"EXPR", type:Type.TYPE_NUMBER(), expression:"["+Constant.findByCode("CONST1").id+"]").save(failOnError:true)
	//
	//		then:
	//		Expression.count() == 1;
	//
	//	}
	
	
	//	def "expression date is updated on save"() {
	//		setup:
	//		new newExpression(code:"CODE", expression: "1", type: Type.TYPE_NUMBER(), timestamp: new Date()).save(failOnError: true)
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

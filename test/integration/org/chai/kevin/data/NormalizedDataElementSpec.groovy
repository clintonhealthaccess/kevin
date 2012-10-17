package org.chai.kevin.data

import grails.validation.ValidationException;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Period;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Type;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.kevin.value.NormalizedDataElementValue;

class NormalizedDataElementSpec extends IntegrationTests {

	def "normalized data element type is valid"() {
		when:
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: ['1':['test':'23']] ).save(failOnError: true)
		
		then:
		NormalizedDataElement.count() == 1
		NormalizedDataElement.list()[0].type.jsonValue == "{\"type\":\"number\"}";
	}
	
	def "normalized data element type cannot be invalid"() {
		when:
		new NormalizedDataElement(code: CODE(1), type: INVALID_TYPE, expressionMap: [:]).save(failOnError: true)
		
		then:
		thrown ValidationException
	}

	def "normalized data element type cannot be null"() {
		when:
		def normalizedDataElement = new NormalizedDataElement(code: CODE(1), expressionMap: [:]).save(failOnError: true)

		then:
		thrown ValidationException
	}

	def "normalized data element code is unique"() {
		when:
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: [:]).save(failOnError: true)

		then:
		NormalizedDataElement.count() == 1;

		when:
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: [:]).save(failOnError: true)

		then:
		thrown ValidationException
	}
	
	def "normalized data element value hashcode and equals"() {
		setup:
		setupLocationTree()
		def type = DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [:])

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
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: ["1":["DH":formula]]).save(failOnError: true)

		then:
		thrown ValidationException

		where:
		formula << [
			"if((123) 1 else 0",
			"if(3) 3",
			"if(\$328==1 || \$286==1 || \$277==1 || \$215==1) \"&#10003;\" else \"NEGS\""
		]
	}
	
	def "expressions in map only can contain normalized data elements"() {
		when:
		def dataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [:])
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: ["1":["DH":"\$"+dataElement.id]]).save(failOnError: true)
		
		then:
		NormalizedDataElement.count() == 2
	}
	
	def "expressions in map cannot contain calculation"() {
		when:
		def calculation = newSum("1", CODE(2))
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: ["1":["DH":"\$"+calculation.id]]).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "expressions can be empty"() {
		when:
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: ["1":["DH":""]]).save(failOnError: true)
		
		then:
		NormalizedDataElement.count() == 1
	}
	
	def "no dependency on normalized data element"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		
		when:
		def dataElement = new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: [:]).save(failOnError: true)
		
		then:
		NormalizedDataElement.count() == 1
		
		when:
		dataElement.expressionMap = [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+dataElement.id,(HEALTH_CENTER_GROUP):"1"]]
		dataElement.save(failOnError: true)
		
		then:
		thrown ValidationException
	}	
	
	//	def "expression can be a constant"() {
	//		setup:
	//		IntegrationTestInitializer.createConstants()
	//
	//		when:
	//		new newExpression(names:["en":"Expression"], code:"EXPR", type:Type.TYPE_NUMBER(), expression:"["+Constant.findByCode("CONST1").id+"]").save(failOnError:true)
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

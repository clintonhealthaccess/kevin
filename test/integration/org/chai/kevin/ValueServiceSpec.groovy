package org.chai.kevin

import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ExpressionValue;
import org.chai.kevin.value.ExpressionValue.Status;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class ValueServiceSpec extends IntegrationTests {

	def valueService;
	
	def expressionService;
	
	def setup() {
		IntegrationTestInitializer.createDummyStructure()
	}
	
	def "test get non calculated expressions"() {
		setup:
		new Expression(
			names:j(["en":"Constant 10"]), 
			descriptions:j([:]), 
			code:"Constant 10", expression: "10", 
			type: ValueType.VALUE, 
			timestamp:new Date()
		).save(failOnError: true)
		
		expect:
		List<ExpressionValue> values = valueService.getNonCalculatedExpressions();
		values.size() == OrganisationUnit.count() * Period.count();
	}
	
	def "test get non calculated expressions when already one expression value"() {
		setup:
		new Expression(
			names:j(["en":"Constant 10"]),
			descriptions:j([:]),
			code:"Constant 10", expression: "10",
			type: ValueType.VALUE,
			timestamp:new Date()
		).save(failOnError: true)
		
		new ExpressionValue(
			period: Period.list()[0],
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			timestamp: new Date(),
			status: Status.VALID,
			value: "10",
			expression: Expression.findByCode("Constant 10")
		).save(failOnError: true)
		
		expect:
		List<ExpressionValue> values = valueService.getNonCalculatedExpressions();
		values.size() == OrganisationUnit.count() * Period.count() - 1;
	}
	
	
	def "test outdated expressions"() {
		setup:
		new Expression(
			names:j(["en":"Constant 10"]),
			descriptions:j([:]),
			code:"Constant 10", expression: "10",
			type: ValueType.VALUE,
		).save(failOnError: true)
		new ExpressionValue(
			period: Period.list()[0],
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			status: Status.VALID,
			value: "10",
			expression: Expression.findByCode("Constant 10")
		).save(failOnError: true)
		
		when:
		List<ExpressionValue> values = valueService.getOutdatedExpressions();
		
		then:
		values.size() == 0
		
		when:
		def expression = Expression.findByCode("Constant 10")
		expression.setTimestamp(new Date());
		expression.save(failOnError: true);
		values = valueService.getOutdatedExpressions();
		
		then:
		values.size() == 1
		values.get(0).expression.equals(Expression.findByCode("Constant 10"));
	}
	
	def "test get non calculated calculations"() {
		setup:
		new Expression(
			names:j(["en":"Constant 10"]),
			descriptions:j([:]),
			code:"Constant 10", expression: "10",
			type: ValueType.VALUE,
			timestamp:new Date()
		).save(failOnError: true)
		new Calculation(expressions: [
			"District Hospital": Expression.findByCode("Constant 10"),
			"Health Center": Expression.findByCode("Constant 10")
		], timestamp:new Date()).save(failOnError: true)
		
		expect:
		List<CalculationValue> values = valueService.getNonCalculatedCalculations();
		values.size() == OrganisationUnit.count() * Period.count();
	}
	
	def "test outdated calculations"() {
		setup:
		new Expression(
			names:j(["en":"Constant 10"]),
			descriptions:j([:]),
			code:"Constant 10", expression: "10",
			type: ValueType.VALUE,
			timestamp:new Date()
		).save(failOnError: true)
		new Calculation(expressions: [
			"District Hospital": Expression.findByCode("Constant 10"),
			"Health Center": Expression.findByCode("Constant 10")
		], timestamp:new Date()).save(failOnError: true)
		new CalculationValue(
			period: Period.list()[0],
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			timestamp: new Date(),
			average: "10",
			calculation: Calculation.list()[0],
			hasMissingExpression: false,
			hasMissingValues: false
		).save(failOnError: true)
		
		when:
		List<CalculationValue> values = valueService.getOutdatedCalculations();
		
		then:
		values.size() == 0
		
		when:
		def calculation = Calculation.list()[0]
		calculation.setTimestamp(new Date());
		calculation.save(failOnError: true, flush: true);
		values = valueService.getOutdatedCalculations();
		
		then:
		values.size() == 1
		values.get(0).calculation.equals(Calculation.list()[0]);
	}
	
	def "test outdated calculations when expression is saved"() {
		setup:
		new Expression(
			names:j(["en":"Constant 10"]),
			descriptions:j([:]),
			code:"Constant 10", expression: "10",
			type: ValueType.VALUE,
			timestamp:new Date()
		).save(failOnError: true)
		new Calculation(expressions: [
			"District Hospital": Expression.findByCode("Constant 10"),
			"Health Center": Expression.findByCode("Constant 10")
		], timestamp:new Date()).save(failOnError: true)
		new CalculationValue(
			period: Period.list()[0],
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			timestamp: new Date(),
			average: "10",
			calculation: Calculation.list()[0],
			hasMissingExpression: false,
			hasMissingValues: false
		).save(failOnError: true)
		
		when:
		List<CalculationValue> values = valueService.getOutdatedCalculations();
		
		then:
		values.size() == 0
		
		when:
		def expression = Expression.findByCode("Constant 10")
		expression.setTimestamp(new Date());
		expression.save(failOnError: true, flush: true)
		values = valueService.getOutdatedCalculations();
		
		then:
		values.size() == 1
		values.get(0).calculation.equals(Calculation.list()[0]);
	}
	
}

package org.chai.kevin.cost

import grails.validation.ValidationException;

import org.chai.kevin.Expression;
import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.cost.CostTarget.CostType;
import org.springframework.dao.DataIntegrityViolationException;

class DomainSpec extends IntegrationTests {

	def setup() {
		Initializer.createDummyStructure()
		IntegrationTestInitializer.createExpressions()
		
		new CostRampUp(name: "Constant", years: [
			1: new CostRampUpYear(year: 1, value: 0.2),
			2: new CostRampUpYear(year: 2, value: 0.2),
			3: new CostRampUpYear(year: 3, value: 0.2),
			4: new CostRampUpYear(year: 4, value: 0.2),
			5: new CostRampUpYear(year: 5, value: 0.2)
		]).save(failOnError: true);
	
		def costObjective = new CostObjective(name:"Human Resources for Health")
		costObjective.addTarget new CostTarget(name:"Training", expression: Expression.findByName("Constant 10"), costRampUp: CostRampUp.findByName("Constant"), costType: CostType.INVESTMENT)
		costObjective.save(failOnError: true)
	}
	
	def "target constraint: expression cannot be null"() {
		when:
		new CostTarget(name:"Test Target", costRampUp: CostRampUp.findByName("Constant"), costType: CostType.INVESTMENT).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
	
	def "ramp up constraint: name cannot be null"() {
		when:
		new CostRampUp().save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "ramp up year constraint: year cannot be null"() {
		when:
		new CostRampUp(name:"Test", years: [new CostRampUpYear(value: 1.0d)]).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "ramp up year constraint: value cannot be null"() {
		when:
		new CostRampUp(name:"Test", years: [new CostRampUpYear(year: 1)]).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "ramp up year constraint: value must be integer"() {
		when:
		new CostRampUp(name:"Test", years: [new CostRampUpYear(year: "test")]).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "delete objective cascade deletes target"() {
		when:
		def costObjective = CostObjective.findByName("Human Resources for Health");
		costObjective.delete();
		
		then:
		CostObjective.count() == 0
		CostTarget.count() == 0
	}
	
	def "save objective saves target"() {
		when:
		def costObjective = new CostObjective(name:"Test Objective")
		costObjective.addTarget new CostTarget(name:"Test Target", expression: Expression.findByName("Constant 10"), costRampUp: CostRampUp.findByName("Constant"), costType: CostType.INVESTMENT)
		costObjective.save();
		
		then:
		CostObjective.count() == 2
		CostTarget.count() == 2
	}
	
	
	def "save target preserves order"() {
		when:
		def costObjective = new CostObjective(name:"Test Objective")
		costObjective.addTarget new CostTarget(name:"Test 4", expression: Expression.findByName("Constant 10"), costRampUp: CostRampUp.findByName("Constant"), costType: CostType.INVESTMENT, order: 4)
		costObjective.addTarget new CostTarget(name:"Test 3", expression: Expression.findByName("Constant 10"), costRampUp: CostRampUp.findByName("Constant"), costType: CostType.INVESTMENT, order: 3)
		costObjective.save();
		
		then:
		def expectedObjective = CostObjective.findByName("Test Objective");
		expectedObjective.targets[0].order == 3
		expectedObjective.targets[1].order == 4

	}
	
	def "save target preserves groups"() {
		when:
		new CostTarget(name:"Test Target", groupUuidString: 'group1', expression: Expression.findByName("Constant 10"), costRampUp: CostRampUp.findByName("Constant"), costType: CostType.INVESTMENT, order: 4).save(failOnError: true)
		
		then:
		CostService.getGroupUuids(CostTarget.findByName("Test Target").groupUuidString).size() == 1
	}
	
	def "save target erases old groups"() {
		when:
		new CostTarget(name:"Test Target", groupUuidString: 'group1', expression: Expression.findByName("Constant 10"), costRampUp: CostRampUp.findByName("Constant"), costType: CostType.INVESTMENT, order: 4).save(failOnError: true)
		CostTarget.findByName("Test Target").groupUuidString = ["group2"]
		
		then:
		CostService.getGroupUuids(CostTarget.findByName("Test Target").groupUuidString).size() == 1
		CostService.getGroupUuids(CostTarget.findByName("Test Target").groupUuidString) == new HashSet(["group2"])
	}
	
	
	
}

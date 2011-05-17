package org.chai.kevin.cost

import java.util.List;

import org.chai.kevin.Expression;
import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.Organisation;
import org.chai.kevin.cost.CostTarget.CostType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import grails.plugin.spock.IntegrationSpec;

class CostTableServiceSpec extends IntegrationTests {

	def costTableService;
	def organisationService;
	
	def setup() {
		Initializer.createDummyStructure()
		IntegrationTestInitializer.createExpressions()
		IntegrationTestInitializer.addCostData()
	}
	
	def "cost service returns expected values"() {
		when:
		def period = Period.list()[0]
		def objective = CostObjective.findByName("Human Resources for Health")
		def costTable = costTableService.getCostTable(period, objective, organisationService.getRootOrganisation())
		def expectedTarget = CostTarget.findByName(targetName)
		
		then:
		costTable.getCost(expectedTarget, year).value == value
		
		where:
		targetName	| year	| value
		"Training"	| 1		| 4.0d
		"Training"	| 2		| 4.0d
		"Training"	| 3		| 4.0d
		"Training"	| 4		| 4.0d
		"Training"	| 5		| 4.0d
		"Average"	| 1		| 4.0d
		"Average"	| 2		| 5.0d
		"Average"	| 3		| 6.0d
		"Average"	| 4		| 7.0d
		"Average"	| 5		| 8.0d
	}
	
	def "cost service takes into account only selected groups"() {
		setup:
		def costObjective = new CostObjective(name:"Test Objective")
		costObjective.addTarget new CostTarget(name:"Test Target", expression: Expression.findByName("Constant 10"), costRampUp: CostRampUp.findByName("Constant"), costType: CostType.INVESTMENT, groupUuidString: "District Hospital")
		costObjective.save(failOnError: true)
		
		when:
		def period = Period.list()[0]
		def objective = CostObjective.findByName("Test Objective")
		def costTable = costTableService.getCostTable(period, objective, organisationService.getRootOrganisation())
		def expectedTarget = CostTarget.findByName(targetName)
		
		
		then:
		costTable.getCost(expectedTarget, year).value == value
		
		where:
		targetName		| year	| value
		"Test Target"	| 1		| 2.0d
		"Test Target"	| 2		| 2.0d
		"Test Target"	| 3		| 2.0d
		"Test Target"	| 4		| 2.0d
		"Test Target"	| 5		| 2.0d
		
	}
	
	def "cost service returns expected years and targets"() {
		when:
		def period = Period.list()[0]
		def objective = CostObjective.findByName("Human Resources for Health")
		def costTable = costTableService.getCostTable(period, objective, organisationService.getRootOrganisation())
		
		then:
		costTable.targets.containsAll getTargets(expectedTargets)
		costTable.years == expectedYears
		
		where:
		expectedTargets			| expectedYears
		["Training", "Average"]	| [1, 2, 3, 4, 5]
	}
	
	def "cost service returns correct explanation"() {
		when:
		def period = Period.list()[0]
		def target = CostTarget.findByName(targetName)
		def organisation = organisationService.getOrganisation(OrganisationUnit.findByName(organisationName).id)
		def explanation = costTableService.getExplanation(period, target, organisation)
		
		then:
		def cost = explanation.getCost(organisationService.getOrganisation(OrganisationUnit.findByName(expectedOrganisationName).id), year)
		cost.value == expectedValue
		
		where:
		targetName	| organisationName	| expectedOrganisationName	| year	| expectedValue
		"Training"	| "Rwanda"			| "North"					| 1		| 4.0d
		"Training"	| "Rwanda"			| "North"					| 2		| 4.0d
		"Training"	| "Rwanda"			| "North"					| 3		| 4.0d
		"Training"	| "Rwanda"			| "North"					| 4		| 4.0d
		"Training"	| "Rwanda"			| "North"					| 5		| 4.0d
		"Training"	| "North"			| "Burera"					| 1		| 4.0d
		"Training"	| "North"			| "Burera"					| 2		| 4.0d
		"Training"	| "North"			| "Burera"					| 3		| 4.0d
		"Training"	| "North"			| "Burera"					| 4		| 4.0d
		"Training"	| "North"			| "Burera"					| 5		| 4.0d
		"Training"	| "Burera"			| "Kivuye HC"				| 1		| 2.0d
		"Training"	| "Burera"			| "Kivuye HC"				| 2		| 2.0d
		"Training"	| "Burera"			| "Kivuye HC"				| 3		| 2.0d
		"Training"	| "Burera"			| "Kivuye HC"				| 4		| 2.0d
		"Training"	| "Burera"			| "Kivuye HC"				| 5		| 2.0d
		"Training"	| "Burera"			| "Butaro DH"				| 1		| 2.0d
		"Training"	| "Burera"			| "Butaro DH"				| 2		| 2.0d
		"Training"	| "Burera"			| "Butaro DH"				| 3		| 2.0d
		"Training"	| "Burera"			| "Butaro DH"				| 4		| 2.0d
		"Training"	| "Burera"			| "Butaro DH"				| 5		| 2.0d
		
	}
	
	def "explanation applies to correct organisation"() {
		setup:
		def costObjective = new CostObjective(name:"Test Objective")
		costObjective.addTarget new CostTarget(name:"Test Target", expression: Expression.findByName("Constant 10"), costRampUp: CostRampUp.findByName("Constant"), costType: CostType.INVESTMENT, groupUuidString: "District Hospital")
		costObjective.save(failOnError: true)
		
		when:
		def period = Period.list()[0]
		def target = CostTarget.findByName("Test Target")
		def explanation = costTableService.getExplanation(period, target, getOrganisation("Burera"))
		
		then:
		explanation.organisations.containsAll getOrganisations(["Butaro DH"])
		explanation.organisations.size() == 1
		
	}
	

}

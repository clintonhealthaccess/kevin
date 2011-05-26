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
		def objective = CostObjective.findByCode("HRH")
		def costTable = costTableService.getCostTable(period, objective, organisationService.getRootOrganisation())
		def expectedTarget = CostTarget.findByCode(targetCode)
		
		then:
		costTable.getCost(expectedTarget, year).value == value
		
		where:
		targetCode	| year	| value
		"TRAINING"	| 1		| 4.0d
		"TRAINING"	| 2		| 4.0d
		"TRAINING"	| 3		| 4.0d
		"TRAINING"	| 4		| 4.0d
		"TRAINING"	| 5		| 4.0d
		"AVERAGE"	| 1		| 4.0d
		"AVERAGE"	| 2		| 5.0d
		"AVERAGE"	| 3		| 6.0d
		"AVERAGE"	| 4		| 7.0d
		"AVERAGE"	| 5		| 8.0d
	}
	
	def "cost service takes into account only selected groups"() {
		setup:
		def costObjective = new CostObjective(code:"Test Objective")
		costObjective.addTarget new CostTarget(code:"Test Target", expression: Expression.findByCode("CONST10"), costRampUp: CostRampUp.findByCode("CONST"), costType: CostType.INVESTMENT, groupUuidString: "District Hospital")
		costObjective.save(failOnError: true)
		
		when:
		def period = Period.list()[0]
		def objective = CostObjective.findByCode("Test Objective")
		def costTable = costTableService.getCostTable(period, objective, organisationService.getRootOrganisation())
		def expectedTarget = CostTarget.findByCode(targetCode)
		
		
		then:
		costTable.getCost(expectedTarget, year).value == value
		
		where:
		targetCode		| year	| value
		"Test Target"	| 1		| 2.0d
		"Test Target"	| 2		| 2.0d
		"Test Target"	| 3		| 2.0d
		"Test Target"	| 4		| 2.0d
		"Test Target"	| 5		| 2.0d
		
	}
	
	def "cost service returns expected years and targets"() {
		when:
		def period = Period.list()[0]
		def objective = CostObjective.findByCode("HRH")
		def costTable = costTableService.getCostTable(period, objective, organisationService.getRootOrganisation())
		
		then:
		costTable.targets.containsAll getTargets(expectedTargets)
		costTable.years == expectedYears
		
		where:
		expectedTargets			| expectedYears
		["TRAINING", "AVERAGE"]	| [1, 2, 3, 4, 5]
	}
	
	def "cost service returns correct explanation"() {
		when:
		def period = Period.list()[0]
		def target = CostTarget.findByCode(targetCode)
		def organisation = organisationService.getOrganisation(OrganisationUnit.findByName(organisationName).id)
		def explanation = costTableService.getExplanation(period, target, organisation)
		
		then:
		def cost = explanation.getCost(organisationService.getOrganisation(OrganisationUnit.findByName(expectedOrganisationName).id), year)
		cost.value == expectedValue
		
		where:
		targetCode	| organisationName	| expectedOrganisationName	| year	| expectedValue
		"TRAINING"	| "Rwanda"			| "North"					| 1		| 4.0d
		"TRAINING"	| "Rwanda"			| "North"					| 2		| 4.0d
		"TRAINING"	| "Rwanda"			| "North"					| 3		| 4.0d
		"TRAINING"	| "Rwanda"			| "North"					| 4		| 4.0d
		"TRAINING"	| "Rwanda"			| "North"					| 5		| 4.0d
		"TRAINING"	| "North"			| "Burera"					| 1		| 4.0d
		"TRAINING"	| "North"			| "Burera"					| 2		| 4.0d
		"TRAINING"	| "North"			| "Burera"					| 3		| 4.0d
		"TRAINING"	| "North"			| "Burera"					| 4		| 4.0d
		"TRAINING"	| "North"			| "Burera"					| 5		| 4.0d
		"TRAINING"	| "Burera"			| "Kivuye HC"				| 1		| 2.0d
		"TRAINING"	| "Burera"			| "Kivuye HC"				| 2		| 2.0d
		"TRAINING"	| "Burera"			| "Kivuye HC"				| 3		| 2.0d
		"TRAINING"	| "Burera"			| "Kivuye HC"				| 4		| 2.0d
		"TRAINING"	| "Burera"			| "Kivuye HC"				| 5		| 2.0d
		"TRAINING"	| "Burera"			| "Butaro DH"				| 1		| 2.0d
		"TRAINING"	| "Burera"			| "Butaro DH"				| 2		| 2.0d
		"TRAINING"	| "Burera"			| "Butaro DH"				| 3		| 2.0d
		"TRAINING"	| "Burera"			| "Butaro DH"				| 4		| 2.0d
		"TRAINING"	| "Burera"			| "Butaro DH"				| 5		| 2.0d
		
	}
	
	def "explanation applies to correct organisation"() {
		setup:
		def costObjective = new CostObjective(code:"Test Objective")
		costObjective.addTarget new CostTarget(code:"Test Target", expression: Expression.findByCode("CONST10"), costRampUp: CostRampUp.findByCode("CONST"), costType: CostType.INVESTMENT, groupUuidString: "District Hospital")
		costObjective.save(failOnError: true)
		
		when:
		def period = Period.list()[0]
		def target = CostTarget.findByCode("Test Target")
		def explanation = costTableService.getExplanation(period, target, getOrganisation("Burera"))
		
		then:
		explanation.organisations.containsAll getOrganisations(["Butaro DH"])
		explanation.organisations.size() == 1
		
	}
	
	def getTargets(def targets) {
		def result = []
		targets.each {
			result.add CostTarget.findByCode(it)
		}
		return result
	}
	

}

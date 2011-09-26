package org.chai.kevin.dsr

import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Expression;
import org.chai.kevin.util.JSONUtils;
import org.hisp.dhis.period.Period;

class DsrServiceSpec extends IntegrationTests {

	def dsrService
	
	def setup() {
		IntegrationTestInitializer.createDummyStructure();
	}
	
	def "test dsr formatting"() {
		setup:
		def expression = new Expression(code:"EXPR1", type: JSONUtils.TYPE_NUMBER, expression: "10").save(failOnError: true)
		def objective = new DsrObjective(code: "OBJ1")
		def target = new DsrTarget(expression: expression)
		objective.addTarget(target)
		objective.save(failOnError: true)
		refresh()
		
		when:
		def organisation = getOrganisation("Burera")
		def period = Period.list()[1]
		def dsrTable = dsrService.getDsr(organisation, objective, period)
		
		then:
		dsrTable.getDsrValue(getOrganisation("Butaro DH"), target) == "10"
		
	}
	
	
}

package org.chai.kevin.cost

import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import grails.plugin.spock.IntegrationSpec;
import grails.plugin.spock.UnitSpec;

class CostControllerSpec extends IntegrationTests {

	def costController
	
	def setup() {
		Initializer.createDummyStructure();
		IntegrationTestInitializer.createExpressions();
		IntegrationTestInitializer.addCostData();
	}
	
	def "controller returns objective list"() {
		setup:
		costController = new CostController()
		
		when:
		costController.params.period = Period.list()[0].id+''
		costController.params.objective = CostObjective.list()[0].id+''
		def model = costController.view()
		
		then:
		def objectives = model.objectives;
		objectives.size() == 2
	}
	
}

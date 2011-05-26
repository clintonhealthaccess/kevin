package org.chai.kevin.dashboard

import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import grails.plugin.spock.IntegrationSpec;
import grails.plugin.spock.UnitSpec;

class DashboardControllerSpec extends IntegrationTests {

	def dashboardObjectiveController
	def dashboardTargetController
	
	def setup() {
		Initializer.createDummyStructure();
		IntegrationTestInitializer.createExpressions();
		IntegrationTestInitializer.createDashboard();
	}
	
	def "delete objective with children"() {
		setup:
		dashboardObjectiveController = new DashboardObjectiveController()
		dashboardObjectiveController.params.id = DashboardObjective.findByCode("HRH").objectiveEntries[0].id
		
		when:
		dashboardObjectiveController.delete()
		
		then:
		dashboardObjectiveController.response.contentAsString.contains "error";
	}
	
	def "delete objective without children"() {
		setup:
		dashboardObjectiveController = new DashboardObjectiveController()
		dashboardObjectiveController.params.id = DashboardObjective.findByCode("STAFFING").objectiveEntries[0].id
		
		when:
		dashboardObjectiveController.delete()
		
		then:
		dashboardObjectiveController.response.contentAsString.contains "success";
	}
	
}

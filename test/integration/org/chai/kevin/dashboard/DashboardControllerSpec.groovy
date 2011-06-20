package org.chai.kevin.dashboard

import org.chai.kevin.Calculation;
import org.chai.kevin.Expression;
import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
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
	
	def "test tests"() {
		expect:
		DashboardObjectiveEntry.get(DashboardObjective.findByCode("HRH").objectiveEntries[0].id) != null
		DashboardObjectiveEntry.get(DashboardObjective.findByCode("STAFFING").objectiveEntries[0].id) != null
		
	}
	
	def "delete objective with children"() {
		setup:
		def entries = DashboardObjectiveEntry.count()
		def targets = DashboardTarget.count()
		def objectives = DashboardObjective.count()
		dashboardObjectiveController = new DashboardObjectiveController()
		
		when:
		dashboardObjectiveController.params.id = DashboardObjective.findByCode("HRH").objectiveEntries[0].id
		dashboardObjectiveController.delete()
		
		then:
		entries == DashboardObjectiveEntry.count()
		targets == DashboardTarget.count()
		objectives == DashboardObjective.count()
//		dashboardObjectiveController.response.contentAsString.contains "error";
	}

	
	def "delete target deletes entry and target"() {
		setup:
		def entries = DashboardObjectiveEntry.count()
		def targets = DashboardTarget.count()
		def objectives = DashboardObjective.count()
		dashboardTargetController = new DashboardTargetController();
		
		when:
		dashboardTargetController.params.id = DashboardObjective.findByCode("STAFFING").objectiveEntries[0].id
		dashboardTargetController.delete()
		
		then:
		DashboardTarget.count() == targets-1
		DashboardObjectiveEntry.count() == entries-1
		DashboardObjective.count() == objectives
		dashboardTargetController.response.contentAsString.contains "success";
	}
	
	def "save new objective"() {
		setup:
		def entries = DashboardObjectiveEntry.count()
		def targets = DashboardTarget.count()
		def objectives = DashboardObjective.count()
		dashboardObjectiveController = new DashboardObjectiveController()
		
		when:
		dashboardObjectiveController.params['currentObjective'] = DashboardObjective.findByCode("STAFFING").id
		dashboardObjectiveController.params['weight'] = 1
		dashboardObjectiveController.params['entry.code'] = "NEW"
		dashboardObjectiveController.saveWithoutTokenCheck()
		def newObjective = DashboardObjective.findByCode("NEW")
		
		then:
		dashboardObjectiveController.response.contentAsString.contains "success";
		newObjective != null
		entries + 1 == DashboardObjectiveEntry.count()
		targets == DashboardTarget.count()
		objectives + 1 == DashboardObjective.count()
		newObjective.parent.weight == 1
	}
	
	def "save new target"() {
		setup:
		def entries = DashboardObjectiveEntry.count()
		def targets = DashboardTarget.count()
		def objectives = DashboardObjective.count()
		dashboardTargetController = new DashboardTargetController()
		
		when:
		dashboardTargetController.params['currentObjective'] = DashboardObjective.findByCode("STAFFING").id
		dashboardTargetController.params['weight'] = 1
		dashboardTargetController.params['entry.code'] = "NEW"
		dashboardTargetController.saveWithoutTokenCheck()
		def newTarget = DashboardTarget.findByCode("NEW")
		
		then:
		dashboardTargetController.response.contentAsString.contains "success";
		newTarget != null
		entries + 1 == DashboardObjectiveEntry.count()
		targets + 1 == DashboardTarget.count() 
		objectives == DashboardObjective.count() 
		newTarget.parent.weight == 1
	}
	
	def "save target with calculations"() {
		setup:
		def entries = DashboardObjectiveEntry.count()
		def targets = DashboardTarget.count()
		def objectives = DashboardObjective.count()
		def calculations = Calculation.count()
		dashboardTargetController = new DashboardTargetController()
		
		when:
		dashboardTargetController.params['currentObjective'] = DashboardObjective.findByCode("STAFFING").id
		dashboardTargetController.params['weight'] = 1
		dashboardTargetController.params['entry.code'] = "NEW"
		dashboardTargetController.params['entry.calculations['+OrganisationUnitGroup.findByName('Health Center').uuid+'].expression.id'] = Expression.findByCode("CONST10").id+""
		dashboardTargetController.params['entry.calculations['+OrganisationUnitGroup.findByName('District Hospital').uuid+'].expression.id'] = "null"
		dashboardTargetController.saveWithoutTokenCheck()
		def newTarget = DashboardTarget.findByCode("NEW")
		
		then:
		dashboardTargetController.response.contentAsString.contains "success";
		newTarget != null
		entries + 1 == DashboardObjectiveEntry.count()
		targets + 1 == DashboardTarget.count()
		objectives == DashboardObjective.count()
		calculations + 2 == Calculation.count()
		newTarget.calculations['Health Center'].expression == Expression.findByCode("CONST10")
		newTarget.calculations['District Hospital'].expression == null
		newTarget.parent.weight == 1
	}
	
	def "edit target with calculations"() {
		setup:
		def entries = DashboardObjectiveEntry.count()
		def targets = DashboardTarget.count()
		def objectives = DashboardObjective.count()
		def calculations = Calculation.count()
		dashboardTargetController = new DashboardTargetController()
		
		when:
		dashboardTargetController.params['id'] = DashboardTarget.findByCode('A2').parent.id
		dashboardTargetController.params['weight'] = 1
		dashboardTargetController.params['entry.code'] = "NEW"
		dashboardTargetController.params['entry.calculations['+OrganisationUnitGroup.findByName('Health Center').uuid+'].expression.id'] = Expression.findByCode("CONST10").id+""
		dashboardTargetController.params['entry.calculations['+OrganisationUnitGroup.findByName('District Hospital').uuid+'].expression.id'] = "null"
		dashboardTargetController.saveWithoutTokenCheck()
		def newTarget = DashboardTarget.findByCode("NEW")
		
		then:
		dashboardTargetController.response.contentAsString.contains "success";
		newTarget != null
		entries == DashboardObjectiveEntry.count()
		targets == DashboardTarget.count()
		objectives == DashboardObjective.count()
		calculations == Calculation.count()
		newTarget.calculations['Health Center'].expression == Expression.findByCode("CONST10")
		newTarget.calculations['District Hospital'].expression == null
		newTarget.parent.weight == 1
	}
	
}

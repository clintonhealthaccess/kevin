package org.chai.kevin.dashboard

import org.hisp.dhis.organisationunit.OrganisationUnit;

import java.util.Date;
import java.util.Calendar;
import java.util.List;

import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Organisation;
import org.chai.kevin.ProgressListener;
import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import grails.plugin.spock.IntegrationSpec;
import grails.plugin.spock.UnitSpec;

class DashboardServiceSpec extends IntegrationTests {
   
	def dashboardService
	def expressionService
	
	def setup() {
		Initializer.createDummyStructure();
		IntegrationTestInitializer.createExpressions()
		IntegrationTestInitializer.createDashboard()
	}
	
	def "test values"() {
		setup:
		IntegrationTestInitializer.createDataElements()
		IntegrationTestInitializer.addNonConstantData()
		expressionService.refreshExpressions();
		expressionService.refreshCalculations();
		
		when:
		def period = Period.list()[1]
		def currentOrganisation = new Organisation(OrganisationUnit.findByName(currentOrganisationName));
		def currentObjective = DashboardObjective.findByCode(currentObjectiveName);
//		dashboardService.refreshDashboard(currentOrganisation, currentObjective, period, new ProgressListener());
		def dashboard = dashboardService.getDashboard(currentOrganisation, currentObjective, period);
		def organisation = new Organisation(OrganisationUnit.findByName(organisationName));
		def objective = DashboardTarget.findByCode(objectiveName);
		if (objective == null) objective = DashboardObjective.findByCode(objectiveName);
		def percentage = dashboard.getPercentage(organisation, objective)
		
		then:
//		percentage.status == status;
		if (percentage.value == null) value == null
		else percentage.value == value
		
		where:
		currentOrganisationName	| currentObjectiveName			| organisationName	| objectiveName 	| value
		"Burera"				| "STAFFING"					| "Butaro DH"		| "TARGET1"			| 40.0d
		"Burera"				| "STAFFING"					| "Butaro DH"		| "A1"				| 10.0d
		"Burera"				| "STAFFING"					| "Butaro DH"		| "A2"				| 20.0d
		"Burera"				| "STAFFING"					| "Kivuye HC"		| "TARGET1"			| null  // TODO check this !
		"Burera"				| "STAFFING"					| "Kivuye HC"		| "A1"				| 10.0d
		"Burera"				| "STAFFING"					| "Kivuye HC"		| "A2"				| 20.0d
		"Burera"				| "HRH"							| "Butaro DH"		| "STAFFING"		| 23.0d
		"Burera"				| "HRH"							| "Kivuye HC"		| "STAFFING"		| 15.0d
		"Rwanda"				| "OBJ"							| "North"			| "HRH"				| 23.0d
	}
	
	
	def "test missing data value"() {
		
	}
	
	def "test missing data element"() {
		
	}
	
	def "explanation"() {
		setup:
		IntegrationTestInitializer.createDataElements()
		IntegrationTestInitializer.addNonConstantData()
		expressionService.refreshExpressions();
		expressionService.refreshCalculations();
		
		
		when:
		def period = Period.list()[1]
		def organisation = new Organisation(OrganisationUnit.findByName(organisationName));
		def objective = DashboardTarget.findByCode(objectiveCode);
		def dataElement = DataElement.findByCode(elementCode);
//		dashboardService.refreshEntireDashboard(new ProgressListener());
		def explanation = dashboardService.getExplanation(organisation, objective, period);
		
		
		then:
		explanation.organisation == organisation
		explanation.entry == objective
		// TODO
//		explanation.expression.name == expressionName
//		explanation.htmlFormula == htmlFormula
		
		where:
		organisationName	| objectiveCode	| expressionName		| elementCode	| htmlFormula
		"Butaro DH"			| "TARGET1"		| "Expression Element 1"| "CODE"		| "<span class=\"element\" id=\"element-code\">[CODE]</span>"
		
	}

	def "dashboard test objectives"() {
		
		setup:
		expressionService.refreshExpressions();
		expressionService.refreshCalculations();
		
		when:
		def period = Period.list()[1]
		def organisation = new Organisation(OrganisationUnit.findByName(organisationName));
		def objective = DashboardObjective.findByCode(objectiveCode);
//		dashboardService.refreshDashboard(organisation, objective, period, new ProgressListener());
		def dashboard = dashboardService.getDashboard(organisation, objective, period);
		
		then:
		organisation.children.size() == 2
		
		organisation == dashboard.currentOrganisation
		objective == dashboard.currentObjective
		dashboard.objectiveEntries == getWeightedObjectives(expectedObjectives)
		// TODO order organisations
		dashboard.organisations.containsAll getOrganisations(expectedOrganisations)
		dashboard.organisationPath == getOrganisations(expectedOrganisationPath)
		dashboard.objectivePath == getObjectives(expectedObjectivePath)
		
		where:
		organisationName	| objectiveCode	| expectedOrganisations			| expectedObjectives		| expectedOrganisationPath	| expectedObjectivePath
		"Burera"			| "STAFFING"	| ["Butaro DH", "Kivuye HC"]	| ["A1", "A2"]				| ["Rwanda", "North"]		| ["OBJ", "HRH"]
		
	}
	
	def getObjectives(List<String> codes) {
		def objectives = []
		for (String code : codes) {
			def objective = DashboardTarget.findByCode(code);
			if (objective == null) objective = DashboardObjective.findByCode(code);
			objectives.add(objective)
		}
		return objectives;
	}
	
	def getWeightedObjectives(List<String> codes) {
		def objectives = []
		for (String code : codes) {
			def objective = DashboardTarget.findByCode(code);
			if (objective == null) objective = DashboardObjective.findByCode(code);
			objectives.add(objective.getParent());
		}
		return objectives;
	}
	
}
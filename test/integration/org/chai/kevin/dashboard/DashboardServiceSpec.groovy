package org.chai.kevin.dashboard

import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategory;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.datavalue.DataValue;

import java.util.Date;
import java.util.Calendar;
import java.util.List;

import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Organisation;
import org.chai.kevin.ProgressListener;
import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.dashboard.DashboardPercentage.Status;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
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
	
	def setup() {
		Initializer.createDummyStructure();
		IntegrationTestInitializer.createExpressions()
		IntegrationTestInitializer.createDashboard()
	}
	
	def "test values"() {
		setup:
		IntegrationTestInitializer.createDataElements()
		IntegrationTestInitializer.addNonConstantData()
		
		when:
		def period = Period.list()[1]
		def currentOrganisation = new Organisation(OrganisationUnit.findByName(currentOrganisationName));
		def currentObjective = DashboardObjective.findByName(currentObjectiveName);
		dashboardService.refreshDashboard(currentOrganisation, currentObjective, period, new ProgressListener());
		def dashboard = dashboardService.getDashboard(currentOrganisation, currentObjective, period);
		def organisation = new Organisation(OrganisationUnit.findByName(organisationName));
		def objective = DashboardTarget.findByName(objectiveName);
		if (objective == null) objective = DashboardObjective.findByName(objectiveName);
		def percentage = dashboard.getPercentage(organisation, objective)
		
		then:
		percentage.status == status;
		if (percentage.value == null) value == null
		else percentage.value == value
		
		where:
		currentOrganisationName	| currentObjectiveName			| organisationName	| objectiveName 				| status				| value
		"Burera"				| "Staffing"					| "Butaro DH"		| "Target 1"					| Status.VALID			| 40.0d
		"Burera"				| "Staffing"					| "Butaro DH"		| "Nurse A1"					| Status.VALID			| 10.0d
		"Burera"				| "Staffing"					| "Butaro DH"		| "Nurse A2"					| Status.VALID			| 20.0d
		"Burera"				| "Staffing"					| "Kivuye HC"		| "Target 1"					| Status.MISSING_VALUE	| null  // TODO check this !
		"Burera"				| "Staffing"					| "Kivuye HC"		| "Nurse A1"					| Status.VALID			| 10.0d
		"Burera"				| "Staffing"					| "Kivuye HC"		| "Nurse A2"					| Status.VALID			| 20.0d
		"Burera"				| "Human Resources for Health"	| "Butaro DH"		| "Staffing"					| Status.VALID			| 23.0d
		"Burera"				| "Human Resources for Health"	| "Kivuye HC"		| "Staffing"					| Status.VALID			| 15.0d
		"Rwanda"				| "Strategic Objectives"		| "North"			| "Human Resources for Health"	| Status.VALID			| 23.0d
	}
	
	def "test missing organisation group"() {
		setup:
		def burera = OrganisationUnit.findByName("Burera")
		def resasa = new OrganisationUnit(name: "Resasa HC", shortName:"RW,N,BU,KIHC", parent: burera)
		burera.children.add resasa
		
		when:
		def period = Period.list()[1]
		def currentOrganisation = new Organisation(OrganisationUnit.findByName("Burera"));
		def currentObjective = DashboardObjective.findByName("Staffing");
		dashboardService.refreshDashboard(currentOrganisation, currentObjective, period, new ProgressListener());
		def dashboard = dashboardService.getDashboard(currentOrganisation, currentObjective, period);
		def organisation = new Organisation(OrganisationUnit.findByName("Resasa HC"));
		def objective = DashboardTarget.findByName("Nurse A1");
		if (objective == null) objective = DashboardObjective.findByName("Nurse A1");
		def explanation = dashboardService.getExplanation(organisation, objective, period);
		
		then:
		dashboard.getPercentage(organisation, objective).status == Status.MISSING_EXPRESSION;
		explanation.average.validPercentage == false
		
	}
	
	def "test missing data value"() {
		
	}
	
	def "test missing data element"() {
		
	}
	
	def "explanation"() {
		setup:
		IntegrationTestInitializer.createDataElements()
		IntegrationTestInitializer.addNonConstantData()
		
		when:
		def period = Period.list()[1]
		def organisation = new Organisation(OrganisationUnit.findByName(organisationName));
		def objective = DashboardTarget.findByName(objectiveName);
		def dataElement = DataElement.findByName(elementName);
		dashboardService.refreshEntireDashboard(new ProgressListener());
		def explanation = dashboardService.getExplanation(organisation, objective, period);
		
		
		then:
		explanation.organisation == OrganisationUnit.findByName(organisationName)
		explanation.entry == objective
		// TODO
//		explanation.expression.name == expressionName
//		explanation.htmlFormula == htmlFormula
		
		where:
		organisationName	| objectiveName	| expressionName		| elementName	| htmlFormula
		"Butaro DH"			| "Target 1"	| "Expression Element 1"| "Element 1"	| "<span class=\"element\" id=\"element-code\">[CODE]</span>"
		
	}

	def "dashboard test objectives"() {
		
		when:
		def period = Period.list()[1]
		def organisation = new Organisation(OrganisationUnit.findByName(organisationName));
		def objective = DashboardObjective.findByName(objectiveName);
		dashboardService.refreshDashboard(organisation, objective, period, new ProgressListener());
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
		organisationName	| objectiveName	| expectedOrganisations			| expectedObjectives		| expectedOrganisationPath	| expectedObjectivePath
		"Burera"			| "Staffing"	| ["Butaro DH", "Kivuye HC"]	| ["Nurse A1", "Nurse A2"]	| ["Rwanda", "North"]		| ["Strategic Objectives", "Human Resources for Health"]
		
	}
	
	def getOrganisations(List<String> names) {
		def organisations = []
		for (String name : names) {
			organisations.add(new Organisation(OrganisationUnit.findByName(name)));
		}
		return organisations;
	}
	
	def getObjectives(List<String> names) {
		def objectives = []
		for (String name : names) {
			def objective = DashboardTarget.findByName(name);
			if (objective == null) objective = DashboardObjective.findByName(name);
			objectives.add(objective)
		}
		return objectives;
	}
	
	def getWeightedObjectives(List<String> names) {
		def objectives = []
		for (String name : names) {
			def objective = DashboardTarget.findByName(name);
			if (objective == null) objective = DashboardObjective.findByName(name);
			objectives.add(objective.getParent());
		}
		return objectives;
	}
	
}
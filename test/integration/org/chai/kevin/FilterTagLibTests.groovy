package org.chai.kevin

import grails.test.GroovyPagesTestCase;

import org.chai.kevin.IntegrationTests
import org.chai.kevin.dashboard.DashboardProgram
import org.chai.kevin.dashboard.DashboardIntegrationTests
import org.chai.kevin.dsr.DsrIntegrationTests
import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.security.UserController;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.data.Type;

class FilterTagLibTests extends GroovyPagesTestCase {

	
	def testProgramFilter() {
		def period = IntegrationTests.newPeriod()
		IntegrationTests.setupProgramTree()
		DashboardIntegrationTests.setupDashboardTree()
		def program = ReportProgram.findByCode(IntegrationTests.PROGRAM1)
		
		def controller = new UserController()
		controller.request.params = [:]
		controller.request.params.put('program', program.id)
		controller.request.params.put('dashboardEntity', 3)
		controller.request.params.put('dsrCategory', 4)
		controller.request.params.put('fctTarget', 5)
		
		def html = applyTemplate(
//			'<g:programFilter linkParams="${params}" selected="${program}" selectedTargetClass="${target}" exclude="${excludeParams}"/>',
			'<g:programFilter linkParams="${params}" exclude="${excludeParams}" selectedTargetClass="${target}"/>',
			[
				'params': controller.request.params,
//				'program': program,
				'target': DashboardTarget.class,
				'excludeParams': ['dashboardEntity', 'dsrCategory', 'fctTarget']
			]
		)
		
		assertTrue html.contains("href=\"/user/index?program="+program.id+"\"")
		assertTrue !html.contains("dashboardEntity="+3+"\"")
		assertTrue !html.contains("dsrCategory="+4+"\"")
		assertTrue !html.contains("fctTarget="+5+"\"")
	}
	
	def testLocationFilter() {
		def hc = IntegrationTests.newDataLocationType(["en":IntegrationTests.HEALTH_CENTER_GROUP], IntegrationTests.HEALTH_CENTER_GROUP);
		def country = IntegrationTests.newLocationLevel(IntegrationTests.NATIONAL, 1)
		def rwanda = IntegrationTests.newLocation(["en":IntegrationTests.RWANDA], IntegrationTests.RWANDA, country)
		def butaro = IntegrationTests.newDataLocation(["en":IntegrationTests.BUTARO], IntegrationTests.BUTARO, rwanda, hc)
		
		def html = applyTemplate(
			'<g:locationFilter selected="${location}"/>',
			[
				'location': rwanda
			]
		)
		
		assertTrue html.contains("href=\"/test?location="+rwanda.id+"\"")
		assertTrue html.contains("Rwanda")
	}
	
	def testPeriodFilter() {
		def period = IntegrationTests.newPeriod()
		
		def html = applyTemplate(
			'<g:periodFilter selected="${period}"/>',
			[
				'period': period
			]
		)
		
		assertTrue html.contains("href=\"/test?period="+period.id+"\"")
		assertTrue html.contains("2005")
	}
	
	def testLocationTypeFilter() {
		def hc = IntegrationTests.newDataLocationType(["en":IntegrationTests.HEALTH_CENTER_GROUP], IntegrationTests.HEALTH_CENTER_GROUP);
		def dh = IntegrationTests.newDataLocationType(["en":IntegrationTests.DISTRICT_HOSPITAL_GROUP], IntegrationTests.DISTRICT_HOSPITAL_GROUP);
		
		def html = applyTemplate(
			'<g:dataLocationTypeFilter selected="${dataLocationTypes}"/>',
			[
				'dataLocationTypes': [hc, dh]
			]
		)
		
		assertTrue html.contains(IntegrationTests.HEALTH_CENTER_GROUP)
		assertTrue html.contains(IntegrationTests.DISTRICT_HOSPITAL_GROUP)
	}
	
	def testLocationFilterDisplaysNothingWhenNothingSelected() {
		def hc = IntegrationTests.newDataLocationType(["en":IntegrationTests.HEALTH_CENTER_GROUP], IntegrationTests.HEALTH_CENTER_GROUP);
		def country = IntegrationTests.newLocationLevel(IntegrationTests.NATIONAL, 1)
		def rwanda = IntegrationTests.newLocation(["en":IntegrationTests.RWANDA], IntegrationTests.RWANDA, country)
		def butaro = IntegrationTests.newDataLocation(["en":IntegrationTests.BUTARO], IntegrationTests.BUTARO, rwanda, hc)
		
		def html = applyTemplate('<g:locationFilter />')
		assertTrue html.contains('select ')
	}
	
	def testProgramFilterDisplaysNothingWhenNothingSelected() {
		def period = IntegrationTests.newPeriod()
		IntegrationTests.setupProgramTree()
		DashboardIntegrationTests.setupDashboardTree()
		
		def html = applyTemplate('<g:programFilter selectedTargetClass="${target}"/>',
			[
				'target': DashboardTarget.class	
			]	
		)
		assertTrue html.contains('select ')
	}
	
	def testTopLevelReportTabsFilter() {
		def period = IntegrationTests.newPeriod()
		IntegrationTests.setupProgramTree()
		DashboardIntegrationTests.setupDashboardTree()
		def program = ReportProgram.findByCode(IntegrationTests.PROGRAM1)
		
		def controller = new UserController()
		controller.request.params = [:]
		controller.request.params.put('program', program.id)
		controller.request.params.put('dashboardEntity', 3)
		
		def html = applyTemplate(
			'<g:topLevelReportTabs linkParams="${params}" exclude="${excludeParams}" />',
			[
				'params': controller.request.params,
				'excludeParams': ['dashboardEntity']
			]
		)

		assertTrue html.contains("href=\"/dashboard/index?program="+program.id+"\"")
		assertTrue html.contains("href=\"/dsr/index?program="+program.id+"\"")
		assertTrue html.contains("href=\"/fct/index?program="+program.id+"\"")
		assertTrue !html.contains("dashboardEntity="+3+"\"")
	}
}
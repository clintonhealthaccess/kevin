package org.chai.kevin

import grails.test.GroovyPagesTestCase;

import org.apache.commons.lang.StringUtils;
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
import org.hibernate.validator.cfg.defs.AssertTrueDef;

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
			'<g:programFilter linkParams="${params}" exclude="${excludeParams}" selectedTargetClass="${target}"/>',
			[
				'params': controller.request.params,
				'target': DashboardTarget.class,
				'excludeParams': ['dashboardEntity', 'dsrCategory', 'fctTarget']
			]
		)
		
		assertTrue html.contains("href=\"/user/index?program="+program.id+"\"")
		assertTrue !html.contains("dashboardEntity="+3+"\"")
		assertTrue !html.contains("dsrCategory="+4+"\"")
		assertTrue !html.contains("fctTarget="+5+"\"")
	}
	
	def testProgramFilterWithNoTargets() {
		def period = IntegrationTests.newPeriod()
		IntegrationTests.setupProgramTree()
		
		def controller = new UserController()
		controller.request.params = [:]
		
		def html = applyTemplate(
			'<g:programFilter linkParams="${params}" selectedTargetClass="${target}"/>',
			[
				'params': controller.request.params,
				'target': DashboardTarget.class,
			]
		)
		
		assertTrue html.contains('No targets in system, please add some targets using the admin menu, or ask an administrator.')
	}
	
	def testProgramFilterOrder() {
		def period = IntegrationTests.newPeriod()
		def root = IntegrationTests.newReportProgram(IntegrationTests.ROOT)		
		def program1 = IntegrationTests.newReportProgram(['en': IntegrationTests.PROGRAM1], IntegrationTests.PROGRAM1, 2, root)
		def program2 = IntegrationTests.newReportProgram(['en': IntegrationTests.PROGRAM2], IntegrationTests.PROGRAM2, 1, root)
		def dashboardRoot = DashboardIntegrationTests.newDashboardProgram(DashboardIntegrationTests.DASHBOARD_ROOT, root, 0)
		def dashboardProgram1 = DashboardIntegrationTests.newDashboardProgram(DashboardIntegrationTests.DASHBOARD_PROGRAM1, program1, 1, 1)
		def dashboardProgram2 = DashboardIntegrationTests.newDashboardProgram(DashboardIntegrationTests.DASHBOARD_PROGRAM2, program2, 1, 2)
		
		def ratio = IntegrationTests.newSum("1", 'sum')
		def target1 = DashboardIntegrationTests.newDashboardTarget(['en': DashboardIntegrationTests.TARGET1], DashboardIntegrationTests.TARGET1, ratio, program1, 1, 1)
		def target2 = DashboardIntegrationTests.newDashboardTarget(['en': DashboardIntegrationTests.TARGET2], DashboardIntegrationTests.TARGET2, ratio, program2, 1, 2)
		
		def controller = new UserController()
		controller.request.params = [:]
		
		def html = applyTemplate(
			'<g:programFilter linkParams="${params}" selectedTargetClass="${target}"/>',
			[
				'params': controller.request.params,
				'target': DashboardTarget.class
			]
		)
		
		assertTrue html.contains(IntegrationTests.PROGRAM2)
		assertTrue html.contains(IntegrationTests.PROGRAM1)
		assertTrue html.indexOf(IntegrationTests.PROGRAM2) < html.indexOf(IntegrationTests.PROGRAM1)
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
	
	def testLocationFilterWithSkipLevels() {
		def country = IntegrationTests.newLocationLevel(IntegrationTests.NATIONAL, 1)
		def province = IntegrationTests.newLocationLevel(IntegrationTests.PROVINCE, 2)
		def district = IntegrationTests.newLocationLevel(IntegrationTests.DISTRICT, 3)
		def rwanda = IntegrationTests.newLocation(["en":IntegrationTests.RWANDA], IntegrationTests.RWANDA, country)
		def north = IntegrationTests.newLocation(["en":IntegrationTests.NORTH], IntegrationTests.NORTH, rwanda, province)
		def burera = IntegrationTests.newLocation(["en":IntegrationTests.BURERA], IntegrationTests.BURERA, north, district)
		def hc = IntegrationTests.newDataLocationType(["en":IntegrationTests.HEALTH_CENTER_GROUP], IntegrationTests.HEALTH_CENTER_GROUP);
		def butaro = IntegrationTests.newDataLocation(["en":IntegrationTests.BUTARO], IntegrationTests.BUTARO, burera, hc)
		
		def html = applyTemplate(
			'<g:locationFilter selected="${location}" skipLevels="${skipLevels}"/>',
			[
				'location': rwanda,
				'skipLevels': [province]
			]
		)
		
		assertTrue html.contains("href=\"/test?location="+rwanda.id+"\"")
		assertTrue html.contains("href=\"/test?location="+burera.id+"\"")
		assertFalse html.contains("href=\"/test?location="+north.id+"\"")
		
	}
	
	def testLocationFilterWithNoSelectedLocationTypes() {
		def hc = IntegrationTests.newDataLocationType(["en":'not in config'], 'not_in_config', 1);
		def country = IntegrationTests.newLocationLevel(IntegrationTests.NATIONAL, 1)
		def rwanda = IntegrationTests.newLocation(["en":IntegrationTests.RWANDA], IntegrationTests.RWANDA, country)
		def butaro = IntegrationTests.newDataLocation(["en":IntegrationTests.BUTARO], IntegrationTests.BUTARO, rwanda, hc)
		
		def html = applyTemplate(
			'<g:locationFilter selectedTypes="${types}"/>',
			[
				'types': []	
			]
		)
		
		assertTrue html.contains('No location type selected')
	}
	
	def testLocationFilterWithNoLocations() {
		def html = applyTemplate(
			'<g:locationFilter/>'
		)
		
		assertTrue html.contains('No locations in system, please add some locations using the admin menu, or ask an administrator.')
	}
	
	def testLocationFilterWithNoDataLocations() {
		def country = IntegrationTests.newLocationLevel(IntegrationTests.NATIONAL, 1)
		def rwanda = IntegrationTests.newLocation(["en":IntegrationTests.RWANDA], IntegrationTests.RWANDA, country)
		def hc = IntegrationTests.newDataLocationType(["en":IntegrationTests.HEALTH_CENTER_GROUP], IntegrationTests.HEALTH_CENTER_GROUP, 2);
		
		def html = applyTemplate(
			'<g:locationFilter/>'
		)
		
		assertTrue html.contains('No data locations in system, please add some locations using the admin menu, or ask an administrator.')
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
	
	def testPeriodFilterWhenNoPeriod() {
		def html = applyTemplate(
			'<g:periodFilter/>'
		)
		
		assertTrue html.contains('No periods in system, please add some targets using the admin menu, or ask an administrator.')
	}
	
	def testLocationTypeFilter() {
		def hc = IntegrationTests.newDataLocationType(["en":IntegrationTests.HEALTH_CENTER_GROUP], IntegrationTests.HEALTH_CENTER_GROUP, 2);
		def dh = IntegrationTests.newDataLocationType(["en":IntegrationTests.DISTRICT_HOSPITAL_GROUP], IntegrationTests.DISTRICT_HOSPITAL_GROUP, 1);
		
		def html = applyTemplate(
			'<g:dataLocationTypeFilter selected="${dataLocationTypes}"/>',
			[
				'dataLocationTypes': []
			]
		)
		
		assertTrue html.contains(IntegrationTests.DISTRICT_HOSPITAL_GROUP)
		assertTrue html.contains(IntegrationTests.HEALTH_CENTER_GROUP)
		assertTrue html.indexOf(IntegrationTests.DISTRICT_HOSPITAL_GROUP) < html.indexOf(IntegrationTests.HEALTH_CENTER_GROUP)
		
		// order with selected stuff
		html = applyTemplate(
			'<g:dataLocationTypeFilter selected="${dataLocationTypes}"/>',
			[
				'dataLocationTypes': [hc, dh]
			]
		)
		
		assertTrue html.contains(IntegrationTests.DISTRICT_HOSPITAL_GROUP)
		assertTrue html.contains(IntegrationTests.HEALTH_CENTER_GROUP)
		assertTrue html.indexOf(IntegrationTests.DISTRICT_HOSPITAL_GROUP) < html.indexOf(IntegrationTests.HEALTH_CENTER_GROUP)
	}
	
	def testLocationTypeFilterWhenNoDefault() {
		def hc = IntegrationTests.newDataLocationType(["en":'not in config'], 'not_in_config', 1);
		
		def html = applyTemplate(
			'<g:dataLocationTypeFilter selected="${dataLocationTypes}"/>',
			[
				'dataLocationTypes': []
			]
		)
		
		assertTrue html.contains('not in config')
		assertTrue html.contains('Please select location types')
	}
	
	def testLocationTypeFilterWhenNoTypesInSystem() {
		def html = applyTemplate(
			'<g:dataLocationTypeFilter selected="${dataLocationTypes}"/>',
			[
				'dataLocationTypes': []
			]
		)
		
		assertTrue html.contains('No location types in system, please add some location types using the admin menu, or ask an administrator.')
	}
	
	def testLocationFilterDisplaysNothingWhenNothingSelected() {
		def hc = IntegrationTests.newDataLocationType(["en":IntegrationTests.HEALTH_CENTER_GROUP], IntegrationTests.HEALTH_CENTER_GROUP);
		def country = IntegrationTests.newLocationLevel(IntegrationTests.NATIONAL, 1)
		def rwanda = IntegrationTests.newLocation(["en":IntegrationTests.RWANDA], IntegrationTests.RWANDA, country)
		def butaro = IntegrationTests.newDataLocation(["en":IntegrationTests.BUTARO], IntegrationTests.BUTARO, rwanda, hc)
		
		def html = applyTemplate('<g:locationFilter />')
		assertTrue html.contains('Please select a location')
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
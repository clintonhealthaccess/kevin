package org.chai.kevin

import grails.test.GroovyPagesTestCase;

import org.chai.kevin.IntegrationTests
import org.chai.kevin.dashboard.DashboardIntegrationTests
import org.chai.kevin.dsr.DsrIntegrationTests
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
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
		
		def html = applyTemplate(
			'<g:programFilter selected="${program}" selectedTargetClass="${target}"/>',
			[
				'program': program,
				'target': DashboardTarget.class
			]
		)
		
		assertTrue html.contains("href=\"/test?program="+program.id+"\"")
	}
	
	def testLocationFilter() {
		def hc = IntegrationTests.newDataEntityType(IntegrationTests.j(["en":IntegrationTests.HEALTH_CENTER_GROUP]), IntegrationTests.HEALTH_CENTER_GROUP);
		def country = IntegrationTests.newLocationLevel(IntegrationTests.COUNTRY, 1)
		def rwanda = IntegrationTests.newLocationEntity(IntegrationTests.j(["en":IntegrationTests.RWANDA]), IntegrationTests.RWANDA, country)
		def butaro = IntegrationTests.newDataLocationEntity(IntegrationTests.j(["en":IntegrationTests.BUTARO]), IntegrationTests.BUTARO, rwanda, hc)
		
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
		def hc = IntegrationTests.newDataEntityType(IntegrationTests.j(["en":IntegrationTests.HEALTH_CENTER_GROUP]), IntegrationTests.HEALTH_CENTER_GROUP);
		def dh = IntegrationTests.newDataEntityType(IntegrationTests.j(["en":IntegrationTests.DISTRICT_HOSPITAL_GROUP]), IntegrationTests.DISTRICT_HOSPITAL_GROUP);
		
		def html = applyTemplate(
			'<g:locationTypeFilter selected="${locationTypes}"/>',
			[
				'locationTypes': [hc, dh]
			]
		)
		
		assertTrue html.contains("Health Center")
		assertTrue html.contains("District Hospital")
	}
	
	//TODO fix these tests
	
	def testLocationTypesLinkParamFilter() {
		def period = IntegrationTests.newPeriod()
		def country = IntegrationTests.newLocationLevel(IntegrationTests.COUNTRY, 1)
		def rwanda = IntegrationTests.newLocationEntity(IntegrationTests.j(["en":IntegrationTests.RWANDA]), IntegrationTests.RWANDA, country)
		def program = IntegrationTests.newReportProgram(IntegrationTests.ROOT)
		
		def html = applyTemplate(
			'<input type="hidden" name="location" value="${location}"/>' +
			'<input type="hidden" name="program" value="${program}"/>' +
			'<input type="hidden" name="period" value="${period}"/>',
			[
				'location': rwanda.id,
				'program': program.id,
				'period': period.id
			]
		)
		
		assertTrue html.contains(rwanda.id.toString())
		assertTrue html.contains(program.id.toString())
		assertTrue html.contains(period.id.toString())
	}

	def testLinkParamFilter() {
		def controller = new UserController()
		controller.request.params = ['param1': '123', 'param2': ['123','456']]
		
		def html = applyTemplate(
			'<g:linkParamFilter linkParams="${params}"/>'
		)

		assertTrue html.contains('<input type="hidden" name="param1" value="123"/>')
		assertTrue html.contains('<input type="hidden" name="param2" value="123"/>')
		assertTrue html.contains('<input type="hidden" name="param2" value="456"/>')
	}
}
package org.chai.kevin

import grails.test.GroovyPagesTestCase;

import org.chai.kevin.IntegrationTests
import org.chai.kevin.dashboard.DashboardIntegrationTests
import org.chai.kevin.dsr.DsrIntegrationTests
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.data.Type;

class FilterTagLibTests extends GroovyPagesTestCase {

	
	def testProgramFilter() {
		def period = IntegrationTests.newPeriod()
		IntegrationTests.setupProgramTree()
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
				'location': butaro
			]
		)
		
		assertTrue html.contains("href=\"/test?location="+butaro.id+"\"") 
		assertTrue html.contains("Rwanda")
	}
	
	def testIterationFilter() {
		def period = IntegrationTests.newPeriod()
		
		def html = applyTemplate(
			'<g:iterationFilter selected="${period}"/>',
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

	def testReportCategoryLinkParamFilter() {
		def period = IntegrationTests.newPeriod()
		def country = IntegrationTests.newLocationLevel(IntegrationTests.COUNTRY, 1)
		def rwanda = IntegrationTests.newLocationEntity(IntegrationTests.j(["en":IntegrationTests.RWANDA]), IntegrationTests.RWANDA, country)
		def root = IntegrationTests.newReportProgram(IntegrationTests.ROOT)
		def hc = IntegrationTests.newDataEntityType(IntegrationTests.j(["en":IntegrationTests.HEALTH_CENTER_GROUP]), IntegrationTests.HEALTH_CENTER_GROUP);
		def dh = IntegrationTests.newDataEntityType(IntegrationTests.j(["en":IntegrationTests.DISTRICT_HOSPITAL_GROUP]), IntegrationTests.DISTRICT_HOSPITAL_GROUP);
		
		//test for 1 location type
		def html = applyTemplate(
			'<input type="hidden" name="location" value="${location}"/>' +
			'<input type="hidden" name="program" value="${program}"/>' +
			'<input type="hidden" name="period" value="${period}"/>' +
			'<input type="hidden" name="locationTypes" value="${locationTypes}"/>',
			
			[
				'location': rwanda.id,
				'program': root.id,
				'period': period.id,
				'locationTypes1': hc.id,
			]
		)
		
		assertTrue html.contains(rwanda.id.toString())
		assertTrue html.contains(root.id.toString())
		assertTrue html.contains(period.id.toString())
		assertTrue html.contains(hc.id.toString())
		
		//test with 2 location types
		html = applyTemplate(
			'<input type="hidden" name="location" value="${location}"/>' +
			'<input type="hidden" name="program" value="${program}"/>' +
			'<input type="hidden" name="period" value="${period}"/>' +
			'<input type="hidden" name="locationTypes" value="${locationTypes1}"/>' +
			'<input type="hidden" name="locationTypes" value="${locationTypes2}"/>',
			
			[
				'location': rwanda.id,
				'program': root.id,
				'period': period.id,
				'locationTypes1': hc.id,
				'locationTypes2': dh.id
			]
		)
		
		assertTrue html.contains(rwanda.id.toString())
		assertTrue html.contains(root.id.toString())
		assertTrue html.contains(period.id.toString())
		assertTrue html.contains(hc.id.toString())
		assertTrue html.contains(dh.id.toString())
	}
	
	def testTargetFilterLinkParamFilter() {
		def period = IntegrationTests.newPeriod()
		def country = IntegrationTests.newLocationLevel(IntegrationTests.COUNTRY, 1)
		def rwanda = IntegrationTests.newLocationEntity(IntegrationTests.j(["en":IntegrationTests.RWANDA]), IntegrationTests.RWANDA, country)
		def root = IntegrationTests.newReportProgram(IntegrationTests.ROOT)
		def hc = IntegrationTests.newDataEntityType(IntegrationTests.j(["en":IntegrationTests.HEALTH_CENTER_GROUP]), IntegrationTests.HEALTH_CENTER_GROUP);
		def dh = IntegrationTests.newDataEntityType(IntegrationTests.j(["en":IntegrationTests.DISTRICT_HOSPITAL_GROUP]), IntegrationTests.DISTRICT_HOSPITAL_GROUP);
		
		//test for 1 location type
		def html = applyTemplate(
			'<input type="hidden" name="location" value="${location}"/>' +
			'<input type="hidden" name="program" value="${program}"/>' +
			'<input type="hidden" name="period" value="${period}"/>' +
			'<input type="hidden" name="locationTypes" value="${locationTypes}"/>',
			
			[
				'location': rwanda.id,
				'program': root.id,
				'period': period.id,
				'locationTypes1': hc.id,
			]
		)
		
		assertTrue html.contains(rwanda.id.toString())
		assertTrue html.contains(root.id.toString())
		assertTrue html.contains(period.id.toString())
		assertTrue html.contains(hc.id.toString())
		
		//test with 2 location types
		html = applyTemplate(
			'<input type="hidden" name="location" value="${location}"/>' +
			'<input type="hidden" name="program" value="${program}"/>' +
			'<input type="hidden" name="period" value="${period}"/>' +
			'<input type="hidden" name="locationTypes" value="${locationTypes1}"/>' +
			'<input type="hidden" name="locationTypes" value="${locationTypes2}"/>',
			
			[
				'location': rwanda.id,
				'program': root.id,
				'period': period.id,
				'locationTypes1': hc.id,
				'locationTypes2': dh.id
			]
		)
		
		assertTrue html.contains(rwanda.id.toString())
		assertTrue html.contains(root.id.toString())
		assertTrue html.contains(period.id.toString())
		assertTrue html.contains(hc.id.toString())
		assertTrue html.contains(dh.id.toString())
	}
}
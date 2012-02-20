package org.chai.kevin

import grails.test.GroovyPagesTestCase;

import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;

class FilterTagLibTests extends GroovyPagesTestCase {

	def testLocationFilter() {
		def hc = IntegrationTests.newDataEntityType(IntegrationTests.j(["en":IntegrationTests.HEALTH_CENTER_GROUP]), IntegrationTests.HEALTH_CENTER_GROUP);
		def country = IntegrationTests.newLocationLevel(IntegrationTests.COUNTRY, 1)
		def rwanda = IntegrationTests.newLocationEntity(IntegrationTests.j(["en":IntegrationTests.RWANDA]), IntegrationTests.RWANDA, country)
		IntegrationTests.newDataLocationEntity(IntegrationTests.j(["en":IntegrationTests.BUTARO]), IntegrationTests.BUTARO, rwanda, hc)
		
		def html = applyTemplate(
			'<g:locationFilter selected="${location}"/>',
			[
				'location': DataLocationEntity.findByCode(IntegrationTests.BUTARO)
			]
		)
		
		assertTrue html.contains("href=\"/test?location=1\"") 
		assertTrue html.contains("Rwanda")
	}
	
	def testIterationFilter() {
		def period1 = IntegrationTests.newPeriod() 
				
		def html = applyTemplate(
			'<g:iterationFilter selected="${period}"/>',
			[
				'period': period1
			]
		)
		
		assertTrue html.contains("href=\"/test?period=1\"")
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
		
//		assertTrue html.contains("href=\"/test?locationTypes=1\"")
		assertTrue html.contains("Health Center")
		assertTrue html.contains("District Hospital")
	}
	
}
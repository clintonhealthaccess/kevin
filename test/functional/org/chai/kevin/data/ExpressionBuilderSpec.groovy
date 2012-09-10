package org.chai.kevin.data

import org.chai.kevin.DataLocationEditPage;
import org.chai.kevin.DataLocationListPage;
import org.chai.kevin.DataLocationTypeEditPage;
import org.chai.kevin.DataLocationTypeListPage;
import org.chai.kevin.FunctionalSpec;
import org.chai.kevin.LocationEditPage;
import org.chai.kevin.LocationLevelEditPage;
import org.chai.kevin.LocationLevelListPage;
import org.chai.kevin.LocationListPage;
import org.chai.kevin.PeriodEditPage;
import org.chai.kevin.PeriodListPage;
import org.chai.kevin.user.UserListPage;
import org.openqa.selenium.Keys;

import spock.lang.Stepwise;
import geb.spock.GebSpec;

@Stepwise
class ExpressionBuilderSpec extends FunctionalSpec {

	def "expression builder page"() {
		given:
		login()
		
		when:
		to ExpressionBuilderPage
		
		then:
		at ExpressionBuilderPage
	}
	
	def "create location level"() {
		given:
		login()
		to LocationLevelEditPage
		
		when:
		code = 'location level'
		order = '1'
		save.click()
		
		then:
		at LocationLevelListPage
	}
	
	def "create root location"() {
		given:
		login()
		to LocationEditPage
		
		when:
		code = 'location'
		level = '1'
		save.click()
		
		then:
		at LocationListPage
	}
	
	def "create facility type"() {
		given:
		login()
		to DataLocationTypeEditPage
		
		when:
		code = 'data location type'
		save.click()
		
		then:
		at DataLocationTypeListPage
	}
	
	// TODO somehow does not work with HTMLUnit
//	def "create facility"() {
//		given:
//		login()
//		to DataLocationEditPage
//		
//		when:
//		code = 'data location'
//		type = '1'
//		locationContainer.click()
//		locationInput = 'location'
//		locationInput << Keys.ENTER
//		Thread.sleep(2000)
//		locationContainer.find('.chzn-results').find('li', 1).click()
//		save.click()
//		
//		then:
//		at DataLocationListPage
//	}
	
	def "create period"() {
		given:
		login()
		to PeriodEditPage
		
		when:
		code = 'period'
		endDateDay = '31'
		save.click()
		
		then:
		at PeriodListPage
	}
	
	def "build expression"() {
		given:
		login()
		to ExpressionBuilderPage
		
		when:
		expression = '1'
		type = '{"type":"number"}'
		locationType = 'data location type'
		period = '1'
		save.click()
		report 'builder'
		
		then:
		at ExpressionBuilderResultPage
		// TODO won't work with HTMLUnit because it depends on 
		// a data location being created
//		tableBody.find('tr').size() > 0
	}
	
}

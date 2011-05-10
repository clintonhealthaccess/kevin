package org.chai.kevin.cost

import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import grails.plugin.spock.IntegrationSpec;
import grails.plugin.spock.UnitSpec;

class CostRampUpControllerSpec extends IntegrationTests {

	def costRampUpController
	
	def setup() {
	}
	
	def "create return years"() {
		setup:
		costRampUpController = new CostRampUpController()
		
		when:
		costRampUpController.create()
		
		then:
		costRampUpController.response.contentAsString.contains "success";
		costRampUpController.response.contentAsString.contains "years[1].year";
		
	}
	
}

package org.chai.kevin

import org.chai.kevin.location.DataLocation;

class HomeControllerSpec extends IntegrationTests {

	def homeController
	
	def "data user with survey landing page get redirected to survey"() {
		setup:
		setupLocationTree()
		def dataLocation = DataLocation.findByCode(KIVUYE);
		def user = newSurveyUser("myuser1", UUID.randomUUID().toString(), dataLocation.id);
		setupSecurityManager(user)
		homeController = new HomeController()
		
		when:
		homeController.index()
		
		then:
		homeController.response.redirectedUrl == "/editSurvey/view"
	}
	
	def "data user with planning landing page get redirected to planning"() {
		setup:
		setupLocationTree()
		def dataLocation = DataLocation.findByCode(KIVUYE);
		def user = newPlanningUser("myuser1", UUID.randomUUID().toString(), dataLocation.id);
		setupSecurityManager(user)
		homeController = new HomeController()
		
		when:
		homeController.index()
		
		then:
		homeController.response.redirectedUrl == "/editPlanning/view"
	}
	
	def "other users get redirected to landing page"() {
		setup:
		setupLocationTree()
		def dataLocation = DataLocation.findByCode(KIVUYE);
		def user = newUser("myuser1", UUID.randomUUID().toString());
		setupSecurityManager(user)
		homeController = new HomeController()
		
		when:
		homeController.index()
		
		then:
		homeController.response.redirectedUrl == "/dashboard/view"
	}
	
}

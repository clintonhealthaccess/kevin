package org.chai.kevin.planning

import org.chai.kevin.location.DataLocationEntity;

class PlanningControllerSpec extends PlanningIntegrationTests {
	
	def planningController
	
	def "accessing index page redirects to proper page - normal user to summary page"() {
		setup:
		setupSecurityManager(newUser('test', 'uuid'))
		planningController = new PlanningController()
		
		when:
		planningController.view()
		
		then:
		planningController.response.redirectedUrl == '/planning/summaryPage'
	}
	
	
	def "accessing index page redirects to proper page - data entry user to own planning page"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocationEntity.findByCode(BUTARO).id))
		def period = newPeriod()
		def planning = newPlanning(period, true)
		planningController = new PlanningController()
		
		when:
		planningController.view()
		
		then:
		planningController.response.redirectedUrl == '/planning/overview/'+DataLocationEntity.findByCode(BUTARO).id+'?planning='+planning.id
	}
	
}

package org.chai.kevin.data

import org.chai.kevin.IntegrationTests;

class EnumOptionControllerSpec extends IntegrationTests {

	def enumOptionController
	
	def "list 404 when no enum"() {
		setup:
		enumOptionController = new EnumOptionController()
		
		when:
		enumOptionController.list()
		
		then:
		enumOptionController.modelAndView == null
	}
	
}

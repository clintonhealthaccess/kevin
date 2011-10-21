package org.chai.kevin.fct

import org.chai.kevin.OrganisationService
import org.chai.kevin.IntegrationTests

class FctControllerSpec extends IntegrationTests {

	def fctController = new FctController()
	
	def "do not return default root organisation"() {						
		when:
		def organisation = fctController.getOrganisation(false)
		
		then:
		organisation == null
	}
	
}


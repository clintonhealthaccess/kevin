package org.chai.kevin

import org.chai.kevin.user.UserListPage;
import geb.spock.GebSpec;

class PagesSpec extends FunctionalSpec {

	def "user list page"() {
		given:
		login()
		
		when:
		to UserListPage
		
		then:
		at UserListPage
	}
	
}

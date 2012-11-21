package org.chai.kevin.security

import org.chai.kevin.IntegrationTests;

class AccountControllerSpec extends IntegrationTests {

	def accountController
	
	def "test not logged-in user has no access to page"() {
		setup:
		accountController = new AccountController()
		
		when:
		accountController.editAccount()
		
		then:
		accountController.response.redirectedUrl == null
		accountController.modelAndView == null
	}
	
	def "test not logged-in user cannot save"() {
		setup:
		accountController = new AccountController()
		
		when:
		accountController.saveAccount()
		
		then:
		accountController.response.redirectedUrl == null
		accountController.modelAndView == null
	}
	
	def "logged-in user sees own information on page"() {
		setup:
		def user = newUser('test@test.com', true, true)
		setupSecurityManager(user)
		accountController = new AccountController()
		
		when:
		accountController.editAccount()
		
		then:
		accountController.modelAndView.model.user == user
	}
	
	def "error messages are displayed properly"() {
		setup:
		def user = newUser('test@test.com', true, true)
		setupSecurityManager(user)
		accountController = new AccountController()
		
		when:
		accountController.params.phoneNumber = ''
		accountController.params.firstname = ''
		accountController.params.lastname = ''
		accountController.params.organisation = ''
		accountController.saveAccount()
		
		then:
		accountController.modelAndView.model.user == user
		accountController.modelAndView.model.user.errors.hasFieldErrors('phoneNumber') == true
		accountController.modelAndView.model.user.errors.hasFieldErrors('firstname') == true
		accountController.modelAndView.model.user.errors.hasFieldErrors('lastname') == true
		accountController.modelAndView.model.user.errors.hasFieldErrors('organisation') == true
	}
	
	def "fields are properly saved"() {
		setup:
		def user = newUser('test@test.com', true, true)
		setupSecurityManager(user)
		accountController = new AccountController()
		
		when:
		accountController.params.phoneNumber = '+250 22 222 22 22'
		accountController.params.firstname = 'first2'
		accountController.params.lastname = 'last2'
		accountController.params.organisation = 'org2'
		accountController.params.targetURI = '/user/list'
		accountController.saveAccount()
		
		then:
		accountController.response.redirectUrl == '/user/list'
		User.list()[0].phoneNumber == '+250 22 222 22 22'
		User.list()[0].firstname == 'first2'
		User.list()[0].lastname == 'last2'
		User.list()[0].organisation == 'org2'
	}
	
}

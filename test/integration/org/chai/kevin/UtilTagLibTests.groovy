package org.chai.kevin

import org.chai.kevin.security.UserController;

import grails.test.GroovyPagesTestCase;

class UtilTagLibTests extends GroovyPagesTestCase {

	def grailsApplication
	
	def testLinkWithTargetURI() {
		def controller = new UserController()
		
		// we assume that the query string is what's after '?'
		controller.request.queryString = 'test=123'
		// we assume that forwardURI is always the full URI after the hostname
		controller.request.forwardURI = '/kevin/test/test'
		// contextPath is always the path to the app
		controller.request.contextPath = '/kevin'
		controller.request.params = ['param': '123']
		
		def url = applyTemplate(
			'<g:createLinkWithTargetURI controller="test" action="test" params="[test: 123]"/>'
		)

		assertEquals "/test/test?targetURI=%2Ftest%2Ftest%3Ftest%3D123&test=123", url
	}
	
	def testLinkWithTargetURIWhenNullQuery() {
		def controller = new UserController()
		
		controller.request.queryString = null
		// we assume that forwardURI is always the full URI after the hostname
		controller.request.forwardURI = '/kevin/test/test'
		// contextPath is always the path to the app
		controller.request.contextPath = '/kevin'
		controller.request.params = ['param': '123']
		
		def url = applyTemplate(
			'<g:createLinkWithTargetURI controller="test" action="test" params="[test: 123]"/>'
		)

		assertEquals "/test/test?targetURI=%2Ftest%2Ftest&test=123", url
	}
	
}

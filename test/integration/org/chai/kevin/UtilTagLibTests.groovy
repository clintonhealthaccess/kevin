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
		controller.request.params = ['test': '123']
		
		def url = applyTemplate(
			'<g:createLinkWithTargetURI controller="test" action="test" params="[test: 123]"/>'
		)

		assertEquals "/kevin/test/test?targetURI=%2Ftest%2Ftest%3Ftest%3D123&test=123", url
	}
	
	def testLinkWithTargetURIWhenNullQuery() {
		def controller = new UserController()
		
		controller.request.queryString = null
		// we assume that forwardURI is always the full URI after the hostname
		controller.request.forwardURI = '/kevin/test/test'
		// contextPath is always the path to the app
		controller.request.contextPath = '/kevin'
		controller.request.params = ['test': '123']
		
		def url = applyTemplate(
			'<g:createLinkWithTargetURI controller="test" action="test" params="[test: 123]"/>'
		)

		assertEquals "/kevin/test/test?targetURI=%2Ftest%2Ftest&test=123", url
	}
	
	def testLinkExcludeParams(){
		def controller = new UserController()
		
		controller.request.params = ['test': '123', 'more': '456']
		def url = applyTemplate(
			'<g:createLinkExcludeParams controller="controller" action="action" params="[test: 123, more: 456]" exclude="[\'more\']"/>'
		)
		assertEquals "/kevin/controller/action?test=123", url
	}
	
	def testLinkExcludeParamsWhenNull(){
		def controller = new UserController()
		
		controller.request.params = ['test': '123', 'more': '456']
		def url = applyTemplate(
			'<g:createLinkExcludeParams controller="controller" action="action" params="[test: 123, more: 456]"/>'
		)
		assertEquals "/kevin/controller/action?more=456&test=123", url
	}
	
	def testLinkExcludeParamsWhenEmptyList(){
		def controller = new UserController()
		
		controller.request.params = ['test': '123', 'more': '456']
		def url = applyTemplate(
			'<g:createLinkExcludeParams controller="controller" action="action" params="[test: 123, more: 456]" exclude="[]"/>'
		)
		assertEquals "/kevin/controller/action?more=456&test=123", url
	}
	
	def testStripHtml() {
		def html = applyTemplate(
			'<g:stripHtml field="${field}" chars="10"/>',
			[
				field: '<br/>this is a test with blabla'
			]
		)
		assertEquals 'this is a <a href="#" onclick="return false;" title="this is a test with blabla" class="tooltip">...</a>', html
	}
	
	def testIfTextWhenNoText() {
		def html = applyTemplate(
			'<g:ifText field="${field}">TEXT</g:ifText>',
			[
				field: '<br/>'
			]
		)

		assertEquals "", html
	}
	
	def testIfTextWhenNullText() {
		def html = applyTemplate(
			'<g:ifText field="${field}">TEXT</g:ifText>',
			[
				field: null
			]
		)

		assertEquals "", html
	}
	
	def testIfTextWhenText() {
		def html = applyTemplate(
			'<g:ifText field="${field}">TEXT</g:ifText>',
			[
				field: '<br/>123'
			]
		)

		assertEquals "TEXT", html
	}
	
	def testStripHtmlWhenNullText() {
		def html = applyTemplate(
			'<g:stripHtml field="${field}" chars="10"/>',
			[
				field: null
			]
		)

		assertEquals "", html
	}
}

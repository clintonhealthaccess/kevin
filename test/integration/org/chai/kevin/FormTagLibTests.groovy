package org.chai.kevin

import grails.test.GroovyPagesTestCase;

class FormTagLibTests extends GroovyPagesTestCase {

	def testSimpleSelectFromListSingle() {
		
		def html = applyTemplate (
			'<g:selectFromList name="name" label="Name" '+
			'bean="${bean}" field="name" multiple="false" from="${names}" '+
			'value="${namesSet}"/>',
			[
				bean: ['name': 'TEST'],
				names: ['name1', 'name2'],
				namesSet: 'name1'
			]
		)
		
		assertTrue html.contains ('<option value="name1" selected>')
		assertTrue html.contains ('<option value="name2" >')
	}
	
	def testSimpleSelectFromListMultiple() {
		
		def html = applyTemplate (
			'<g:selectFromList name="name" label="Name" '+
			'bean="${bean}" field="name" multiple="true" from="${names}" '+
			'value="${namesSet}"/>',
			[
				bean: ['name': 'TEST'],
				names: ['name1', 'name2'],
				namesSet: ['name1', 'name2']
			]
		)
		
		assertTrue html.contains ('<option value="name1" selected>')
		assertTrue html.contains ('<option value="name2" selected>')
		// if it is multiple, contains a hidden field
		assertTrue html.contains ('<input type="hidden" name="name" value=""/>')
	}
	
}

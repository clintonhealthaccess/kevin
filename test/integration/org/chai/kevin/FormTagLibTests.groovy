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
	
	def testi18nTextareaWithNullValue(){
				
		def html = applyTemplate (
			'<g:i18nTextarea name="name" label="Name" '+
			'bean="${bean}" field="name" '+
			'value="${null}"/>',
			[
				bean: ['name': 'TEST']
			]
		)
		assertTrue html.contains ('<textarea type="text" class="idle-field" name="name_en" rows="4"></textarea>');
	}

	def testFileUploadForm() {
		def html = applyTemplate(
			'<g:file bean="${bean}"/>',
			[
				bean: null,	
			]
		)
		
		assertTrue html.contains ('UTF-8')
		assertTrue html.contains ('ISO-8859-1')
		assertTrue html.contains ('<input type="text" name="delimiter" value="," style="width: 30px"></input>')
	}
		
}

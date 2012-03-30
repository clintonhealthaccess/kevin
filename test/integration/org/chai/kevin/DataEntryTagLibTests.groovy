package org.chai.kevin

import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.Value;

import grails.plugin.spock.GroovyPagesSpec;
import grails.test.GroovyPagesTestCase;

class DataEntryTagLibTests extends GroovyPagesTestCase {
	
	def languageService
	
	def testEachOption() {
		def option1 = new EnumOption(value: "1", order: IntegrationTests.o(['en':2, 'fr':1]))
		def option2 = new EnumOption(value: "2", order: IntegrationTests.o(['en':1, 'fr':2]))
		def enume = new Enum(enumOptions: [option1, option2])

		assertEquals applyTemplate(
			'<g:eachOption enum="${enume}" var="option">${option.value} </g:eachOption>',
			['enume':enume]
		), '2 1 '
		
	}
	
	
	// this does not work because it modifies the metaClass of languageService for all the tests
//		languageService.metaClass.currentLanguage = { return "fr";}
//		languageService.metaClass.getCurrentLanguage = { return "fr";}
//		
//		assertEquals applyTemplate(
//			'<g:eachOption enum="${enume}" var="option">${option.value} </g:eachOption>',
//			['enume':enume]
//		), '1 2 '
//	
//		languageService.metaClass.remove('currentLanguage')
//		languageService.metaClass.remove('getCurrentLanguage')

		
	def testEachOptionWithNullEnums() {
		assertEquals applyTemplate('<g:eachOption var="option">${option.value}</g:eachOption>'), ''
	}
	

}

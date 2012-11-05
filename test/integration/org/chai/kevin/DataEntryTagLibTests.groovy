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
		def enume = new Enum()
		enume.addToEnumOptions(new EnumOption(value: "1", order: ['en':2, 'fr':1]))
		enume.addToEnumOptions(new EnumOption(value: "2", order: ['en':1, 'fr':2]))

		assertEquals  '2 1 ', applyTemplate(
			'<g:eachOption enum="${enume}" var="option">${option.value} </g:eachOption>',
			['enume':enume]
		)
		
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

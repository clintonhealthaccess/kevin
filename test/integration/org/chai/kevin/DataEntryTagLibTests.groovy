package org.chai.kevin

import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.Value;

import grails.plugin.spock.GroovyPagesSpec;
import grails.test.GroovyPagesTestCase;

class DataEntryTagLibTests extends GroovyPagesTestCase {
	
	def languageService
	
//	def testEachOption() {
//		def option1 = new EnumOption(value: "1", order: IntegrationTests.o(['en':2, 'fr':1]))
//		def option2 = new EnumOption(value: "2", order: IntegrationTests.o(['en':1, 'fr':2]))
//		def enume = new Enum(enumOptions: [option1, option2])
//
//		assertEquals applyTemplate(
//			'<g:eachOption enum="${enume}" var="option">${option.value} </g:eachOption>',
//			['enume':enume]
//		), '2 1 '
//		
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
//	}
	
	def testValueWithNullValue() {
		
		assertEquals applyTemplate(
			'<g:value value="${value}" type="${type}" enums="${enums}"/>',
			[
				'value': null,
				'type': Type.TYPE_ENUM("code"),
				'enums': [:]
			]
		), ''
		
	}
	
	def testValueWithEmptyEnum() {
		
		assertEquals applyTemplate(
			'<g:value value="${value}" type="${type}" enums="${enums}"/>',
			[
				'value': new Value("{\"value\":\"value\"}"),
				'type': Type.TYPE_ENUM("code"),
				'enums': [:]
			]
		), 'value'
		
	}
	
	def testValueWithExistingEnumButNoOption() {
		
		assertEquals applyTemplate(
			'<g:value value="${value}" type="${type}" enums="${enums}"/>',
			[
				'value': new Value("{\"value\":\"value\"}"),
				'type': Type.TYPE_ENUM("code"),
				'enums': ['code': new Enum()]
			]
		), 'value'
		
	}
	
	def testValueWithExistingEnumAndExistingOption() {
		
		assertEquals applyTemplate(
			'<g:value value="${value}" type="${type}" enums="${enums}"/>',
			[
				'value': new Value("{\"value\":\"value\"}"),
				'type': Type.TYPE_ENUM("code"),
				'enums': ['code': new Enum(enumOptions: [new EnumOption(value: 'value', names: IntegrationTests.j(['en':'VALUE_EN']))])]
			]
		), 'VALUE_EN'
		
	}
	
}

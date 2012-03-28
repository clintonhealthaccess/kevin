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
	
	def testAdminValue() {
		assertEquals applyTemplate(
			'<g:adminValue value="${value}" type="${type}"/>',
			[
				'value': Value.VALUE_NUMBER(10),
				'type': Type.TYPE_NUMBER()
			]
		), '10.0'
	
		assertEquals applyTemplate(
			'<g:adminValue value="${value}" type="${type}"/>',
			[
				'value': Value.VALUE_LIST([Value.VALUE_NUMBER(10)]),
				'type': Type.TYPE_LIST(Type.TYPE_NUMBER())
			]
		), '[<a href="#" onclick="$(this).next().toggle();return false;">0</a><div class="hidden">10.0</div>,]'
	
		assertEquals applyTemplate(
			'<g:adminValue value="${value}" type="${type}"/>',
			[
				'value': Value.VALUE_LIST([Value.VALUE_NUMBER(10), Value.VALUE_NUMBER(20)]),
				'type': Type.TYPE_LIST(Type.TYPE_NUMBER())
			]
		), '[<a href="#" onclick="$(this).next().toggle();return false;">0</a><div class="hidden">10.0</div>,<a href="#" onclick="$(this).next().toggle();return false;">1</a><div class="hidden">20.0</div>,]'
	
		assertEquals applyTemplate(
			'<g:adminValue value="${value}" type="${type}"/>',
			[
				'value': Value.VALUE_MAP(['key1': Value.VALUE_NUMBER(10), 'key2': Value.VALUE_NUMBER(20)]),
				'type': Type.TYPE_MAP(['key1': Type.TYPE_NUMBER(), 'key2': Type.TYPE_NUMBER()])
			]
		), '<ul class="value-map"><li class="value-map-entry"><span class="value-map-key">key1</span>: <span class="value-map-value">10.0</span></li><li class="value-map-entry"><span class="value-map-key">key2</span>: <span class="value-map-value">20.0</span></li></ul>'
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
	
	def testValueWithNullEnums() {
		
		assertEquals applyTemplate(
			'<g:value value="${value}" type="${type}" enums="${enums}"/>',
			[
				'value': new Value("{\"value\":\"value\"}"),
				'type': Type.TYPE_ENUM("code"),
				'enums': null
			]
		), 'value'

	}
	
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
	
	def testValueWithNullValueAndEmptyString() {
		assertEquals applyTemplate(
			'<g:value value="${value}" type="${type}" enums="${enums}" nullText="NULL_TEXT"/>',
			[
				'value': null,
				'type': Type.TYPE_ENUM("code"),
				'enums': [:]
			]
		), 'NULL_TEXT'
	}
	
	def testValueWithValueIsNullAndEmptyString() {
		assertEquals applyTemplate(
			'<g:value value="${value}" type="${type}" enums="${enums}" nullText="NULL_TEXT"/>',
			[
				'value': Value.NULL_INSTANCE(),
				'type': Type.TYPE_ENUM("code"),
				'enums': [:]
			]
		), 'NULL_TEXT'
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

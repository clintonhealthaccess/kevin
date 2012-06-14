package org.chai.kevin

import grails.test.GroovyPagesTestCase;

import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;

class ValueTagLibTests extends GroovyPagesTestCase {

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
				'value': Value.VALUE_DATE(Utils.parseDate("01-01-2000")),
				'type': Type.TYPE_DATE()
			]
		), '\"01-01-2000\"'
	
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
	
	
	def testReportValueBool() {
		assertEquals applyTemplate(
			'<g:reportValue value="${value}" type="${type}"/>',
			[
				'value': Value.VALUE_BOOL(true),
				'type': Type.TYPE_BOOL()	
			]
		), '<div class="report-value-true">&#10003;</div>'
		
		assertEquals applyTemplate(
			'<g:reportValue value="${value}" type="${type}"/>',
			[
				'value': Value.VALUE_BOOL(false),
				'type': Type.TYPE_BOOL()
			]
		), '<div class="report-value-false">&#10007;</div>'
	}
	
	def testReportValueNull() {
	
		assertEquals applyTemplate(
			'<g:reportValue value="${value}" type="${type}"/>',
			[
				'value': Value.NULL_INSTANCE(),
				'type': Type.TYPE_BOOL()
			]
		), '<div class="report-value-null">N/A</div>'
	}
	
	def testReportValueEnum() {
		def enume = IntegrationTests.newEnume('enum')
		IntegrationTests.newEnumOption(IntegrationTests.j(['en': 'VALUE']), enume, 'enum1')
		
		assertEquals applyTemplate(
			'<g:reportValue value="${value}" type="${type}"/>',
			[
				'value': Value.VALUE_STRING('enum1'),
				'type': Type.TYPE_ENUM('enum')
			]
		), 'VALUE'
		
		assertEquals applyTemplate(
			'<g:reportValue value="${value}" type="${type}"/>',
			[
				'value': Value.VALUE_STRING('enum1'),
				'type': Type.TYPE_ENUM('unknown')
			]
		), 'enum1'
	}
	
	def testReportValueNumber() {
		
		assertEquals applyTemplate(
			'<g:reportValue value="${value}" type="${type}"/>',
			[
				'value': Value.VALUE_NUMBER(10),
				'type': Type.TYPE_NUMBER()
			]
		), '10'
		
		assertEquals applyTemplate(
			'<g:reportValue value="${value}" type="${type}" format="${format}"/>',
			[
				'value': Value.VALUE_NUMBER(0.1d),
				'type': Type.TYPE_NUMBER(),
				'format': '##%'
			]
		), '10%'
	}
	
	def testValueNumber() {
		
		assertEquals applyTemplate(
			'<g:value value="${value}" type="${type}"/>',
			[
				'value': Value.VALUE_NUMBER(10),
				'type': Type.TYPE_NUMBER()
			]
		), '10'
		
		assertEquals applyTemplate(
			'<g:value value="${value}" type="${type}" format="${format}"/>',
			[
				'value': Value.VALUE_NUMBER(0.1d),
				'type': Type.TYPE_NUMBER(),
				'format': '##%'
			]
		), '10%'
	
		assertEquals applyTemplate(
			'<g:value value="${value}" type="${type}" format="${format}"/>',
			[
				'value': Value.VALUE_NUMBER(0d),
				'type': Type.TYPE_NUMBER()
			]
		), '0'
	
		assertEquals applyTemplate(
			'<g:value value="${value}" type="${type}" format="${format}" zero="${zero}"/>',
			[
				'value': Value.VALUE_NUMBER(0d),
				'type': Type.TYPE_NUMBER(),
				'zero': '-'
			]
		), '-'
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
	
	def testValueWithExistingEnumAndExistingOptionAndNoEnums() {
		def enume = IntegrationTests.newEnume('code')
		IntegrationTests.newEnumOption(IntegrationTests.j(['en':'VALUE_EN']), enume, 'value')
			
		assertEquals applyTemplate(
			'<g:value value="${value}" type="${type}" enums="${enums}"/>',
			[
				'value': new Value("{\"value\":\"value\"}"),
				'type': Type.TYPE_ENUM("code")
			]
		), 'VALUE_EN'
		
	}
	
}

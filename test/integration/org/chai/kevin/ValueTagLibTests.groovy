package org.chai.kevin

import grails.test.GroovyPagesTestCase;

import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;

class ValueTagLibTests extends GroovyPagesTestCase {

	def testReportMapValueNull() {
		assertEquals applyTemplate(
			'<g:reportMapValue value="${value}" type="${type}" format="${format}"/>',
			[
				'value': Value.NULL_INSTANCE(),
				'type': Type.TYPE_BOOL(),
				'format': null
			]
		), '<div class="report-value report-value-null"'+
			' data-report-value="N/A"'+
			' data-report-value-type="null">N/A</div>'
	}
	
	def testReportMapValueBool() {
		assertEquals applyTemplate(
			'<g:reportMapValue value="${value}" type="${type}" format="${format}"/>',
			[
				'value': Value.VALUE_BOOL(true),
				'type': Type.TYPE_BOOL(),
				'format': null
			]
		), '<div class="report-value report-value-true"'+
			' data-report-value="&#10003;"'+
			' data-report-value-raw="true"'+
			' data-report-value-type="BOOL">'+
			'&#10003;</div>'
		
		assertEquals applyTemplate(
			'<g:reportMapValue value="${value}" type="${type}" format="${format}"/>',
			[
				'value': Value.VALUE_BOOL(false),
				'type': Type.TYPE_BOOL(),
				'format': null
			]
		), '<div class="report-value report-value-false"'+
			' data-report-value="&#10007;"'+
			' data-report-value-raw="false"'+
			' data-report-value-type="BOOL">'+
			'&#10007;</div>'
	}
	
	def testReportMapValueEnum(){
		def enume = IntegrationTests.newEnume('enum')
		IntegrationTests.newEnumOption(['en': 'VALUE'], enume, 'enum1')
		
		assertEquals applyTemplate(
			'<g:reportMapValue value="${value}" type="${type}"/>',
			[
				'value': Value.VALUE_STRING('enum1'),
				'type': Type.TYPE_ENUM('enum')
			]
		), '<div class="report-value"'+
			' data-report-value="VALUE"'+
			' data-report-value-raw="VALUE"'+
			' data-report-value-type="ENUM">'+
			'VALUE</div>'
	
		assertEquals applyTemplate(
			'<g:reportMapValue value="${value}" type="${type}"/>',
			[
				'value': Value.VALUE_STRING('enum1'),
				'type': Type.TYPE_ENUM('unknown')
			]
		), '<div class="report-value"'+
			' data-report-value="enum1"'+
			' data-report-value-raw="enum1"'+
			' data-report-value-type="ENUM">'+
			'enum1</div>'
	}
	
	def testReportMapValueNumber(){
		assertEquals applyTemplate(
			'<g:reportMapValue value="${value}" type="${type}" format="${format}"/>',
			[
				'value': Value.VALUE_NUMBER(58),
				'type': Type.TYPE_NUMBER(),
				'format': null
			]
		), '<div class="report-value"'+
			' data-report-value="58"'+
			' data-report-value-raw="58.0"'+
			' data-report-value-type="NUMBER">'+
			'58</div>'
	}
	
	def testReportMapPercentageNumber(){
		assertEquals applyTemplate(
			'<g:reportMapValue value="${value}" type="${type}" format="${format}"/>',
			[
				'value': Value.VALUE_NUMBER(0.58),
				'type': Type.TYPE_NUMBER(),
				'format': '#%',
				'rounded': '2'
			]
		), '<div class="report-value"'+
			' data-report-value="58%"'+
			' data-report-value-raw="0.58"'+
			' data-report-value-type="NUMBER">'+
			'58%</div>'
	}

	
	def testReportBarValue() {
		assertEquals applyTemplate(
			'<g:reportBarValue value="${value}" type="${type}" format="${format}"/>',
			[
				'value': Value.VALUE_NUMBER(58),
				'type': Type.TYPE_NUMBER(),
				'format': "#"
			]
		), '58'
	}
	
	def testReportBarValueNull() {
		assertEquals applyTemplate(
			'<g:reportBarValue value="${value}" type="${type}" format="${format}"/>',
			[
				'value': Value.NULL_INSTANCE(),
				'type': Type.TYPE_NUMBER(),
				'format': "#"
			]
		), '<div class="report-value-null">N/A</div>'
	}
	
	def testReportBarPercentage() {
		assertEquals applyTemplate(
			'<g:reportBarPercentage value="${value}" type="${type}" format="${format}" rounded="${rounded}"/>',
			[
				'value': Value.VALUE_NUMBER(0.579),
				'type': Type.TYPE_NUMBER(),
				'format': "#%",
				'rounded': '2'
			]
		), '58%'
	}
	
	def testReportBarPercentageNull() {
		assertEquals applyTemplate(
			'<g:reportBarValue value="${value}" type="${type}" format="${format}"/>',
			[
				'value': Value.NULL_INSTANCE(),
				'type': Type.TYPE_NUMBER(),
				'format': "#%"
			]
		), '<div class="report-value-null">N/A</div>'
	}
	
	def testReportBarTooltip() {
		assertEquals applyTemplate(
			'<g:reportBarTooltip percentage="${percentage}" value="${value}" totalLocations="${totalLocations}"/>',
			[
				'percentage': '50%',
				'value': '50',
				'totalLocations': 100
			]
		), 'Percentage of Locations: 50%<br />'+'Number of Locations: 50<br />'+'Total Locations: 100'
	}	
	
	
	def testReportValueWithTooltip() {
		assertEquals applyTemplate(
			'<g:reportValue tooltip="${tooltip}" value="${value}" type="${type}"/>',
			[
				'tooltip': 'true',
				'value': Value.VALUE_BOOL(true),
				'type': Type.TYPE_BOOL()
			]
		), '<div class="report-value-true"><span class="tooltip" original-title="true">&#10003;</span></div>'
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
	
	def testReportValueWithNullFormat() {
		
		assertEquals applyTemplate(
			'<g:reportValue value="${value}" type="${type}"/>',
			[
				'value': Value.VALUE_NUMBER(1),
				'type': Type.TYPE_NUMBER()
			]
		), '1'
	}
	
	def testReportValueEnum() {
		def enume = IntegrationTests.newEnume('enum')
		IntegrationTests.newEnumOption(['en': 'VALUE'], enume, 'enum1')
		
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
	
	def testReportValuePercentage() {
		
		assertEquals applyTemplate(
			'<g:reportValue value="${value}" format="${format}" type="${type}"/>',
			[
				'value': Value.VALUE_NUMBER(0.67),
				'type': Type.TYPE_NUMBER(),
				'format': '#%',
				'rounded': '2'
			]
		), '67%'
		
		assertEquals applyTemplate(
			'<g:reportValue value="${value}" format="${format}" type="${type}"/>',
			[
				'value': Value.VALUE_NUMBER(0.673),
				'type': Type.TYPE_NUMBER(),
				'format': '#%',
				'rounded': '2'
			]
		), '67%'

		assertEquals applyTemplate(
			'<g:reportValue value="${value}" format="${format}" type="${type}"/>',
			[
				'value': Value.VALUE_NUMBER(0.677),
				'type': Type.TYPE_NUMBER(),
				'format': '#%',
				'rounded': '2'
			]
		), '68%'
		
		assertEquals applyTemplate(
			'<g:reportValue value="${value}" format="${format}" type="${type}"/>',
			[
				'value': null,
				'type': Type.TYPE_NUMBER(),
				'format': '#%',
				'rounded': '2'
			]
		), '<div class="report-value-null">N/A</div>'
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
				'enums': ['code': new Enum(enumOptions: [new EnumOption(value: 'value', names_en:'VALUE_EN')])]
			]
		), 'VALUE_EN'
		
	}
	
	def testValueWithExistingEnumAndExistingOptionAndNoEnums() {
		def enume = IntegrationTests.newEnume('code')
		IntegrationTests.newEnumOption(['en':'VALUE_EN'], enume, 'value')
			
		assertEquals applyTemplate(
			'<g:value value="${value}" type="${type}" enums="${enums}"/>',
			[
				'value': new Value("{\"value\":\"value\"}"),
				'type': Type.TYPE_ENUM("code")
			]
		), 'VALUE_EN'
		
	}
	
}

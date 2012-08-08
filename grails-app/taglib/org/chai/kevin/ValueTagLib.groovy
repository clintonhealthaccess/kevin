package org.chai.kevin

import org.apache.commons.lang.NotImplementedException;

import java.text.DecimalFormat;

import org.chai.kevin.dsr.DsrTarget;

import org.chai.kevin.UtilTagLib;

import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type.ValueType;

import org.chai.kevin.data.Type;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;

class ValueTagLib {

	def languageService
	
	def reportValue = { attrs, body ->
		def value = attrs['value']
		def type = attrs['type']
		def format = attrs['format']
		
		if (value == null || value.isNull()) {
			out << '<div class="report-value report-value-null"'+
				' data-report-value="'+message(code: 'report.value.null')+'"'+
				' data-report-value-type="null">'+
				message(code: 'report.value.null')+'</div>'
		}
		else {
			switch (type.type) {
				case ValueType.BOOL:
					if (value.booleanValue){
						out << '<div class="report-value report-value-true"'+
							' data-report-value="'+value.booleanValue+'"'+
							' data-report-value-type="'+type.type+'">'+
							'&#10003;</div>'
					}					
					else{
						out << '<div class="report-value report-value-false"'+
							' data-report-value="'+value.booleanValue+'"'+
							' data-report-value-type="'+type.type+'">'+
							'&#10007;</div>'
					}
					break;
				default:
					def reportValue = languageService.getStringValue(value, type, null, format)
					out << '<div class="report-value"'+
						' data-report-value="'+reportValue+'"'+
						' data-report-value-type="'+type.type+'">'+
						reportValue+'</div>'
			}
		}
	}
	
	def reportPercentage = { attrs, body ->
		def value = attrs['value']
		
		if(value == null || value.isNull()){			
			out << '<div class="report-value report-value-null"'+
			' data-report-value="'+message(code: 'report.value.null')+'"'+
			' data-report-value-type="null">'+
			message(code: 'report.value.null')+'</div>'
		}
		else{
			def average = value.numberValue.round(2)
			DecimalFormat df = new DecimalFormat('#%')
			def reportValue = df.format(average)
			out << '<div class="report-value"'+
				' data-report-value="'+reportValue+'"'+
				' data-report-value-type="'+ValueType.NUMBER+'">'+
				reportValue+'</div>'
		}
	}
	
	def reportTooltip = { attrs, body ->
		def average = attrs['average']
		def value = attrs['value']
		def totalLocations = attrs['totalLocations']
		
		out << message(code: 'fct.report.chart.tooltip.percentage')+': '+average+'<br />'+
				message(code: 'fct.report.chart.tooltip.value')+': '+value+'<br />'+
				message(code: 'fct.report.chart.tooltip.datalocations')+': '+totalLocations
	}
	
	def adminValue = {attrs, body ->
		def type = attrs['type']
		def value = attrs['value']
		
		def printableValue = new StringBuffer()
		prettyPrint(type, value, printableValue, 0)
		
		out << printableValue.toString()
	}
	
	def prettyPrint(Type type, Value value, StringBuffer printableValue, Integer level) {
		if (value == null || value.isNull()) printableValue.append 'null'
		else {
			switch (type.type) {
				case (ValueType.ENUM):
				case (ValueType.STRING):
				case (ValueType.TEXT):
					printableValue.append '"'
					printableValue.append  value.stringValue
					printableValue.append '"'
					break;
				case (ValueType.DATE):
					printableValue.append '"'
					printableValue.append  Utils.formatDate(value.dateValue)
					printableValue.append '"'
					break;
				case (ValueType.NUMBER):
					printableValue.append  value.numberValue
					break;
				case (ValueType.BOOL):
					printableValue.append  value.booleanValue
					break;
				case (ValueType.LIST):
					printableValue.append  '['
					int i = 0
					for (Value listValue : value.listValue) {
						printableValue.append '<a href="#" onclick="$(this).next().toggle();return false;">'
						printableValue.append i++
						printableValue.append '</a>'
						printableValue.append '<div class="hidden">'
						prettyPrint(type.listType, listValue, printableValue, level+1)
						printableValue.append '</div>'
						printableValue.append ','
					}
					printableValue.append ']'
					break;
				case (ValueType.MAP):
					printableValue.append '<ul class="value-map">'
					for (def entry : type.elementMap) {
						printableValue.append '<li class="value-map-entry">'
						printableValue.append '<span class="value-map-key">'
						printableValue.append entry.key
						printableValue.append '</span>: '
						printableValue.append '<span class="value-map-value">'
						prettyPrint(entry.value, value.mapValue[entry.key], printableValue, level+1)
						printableValue.append '</span>'
						printableValue.append '</li>'
					}
					printableValue.append '</ul>'
					break;
				default:
					throw new NotImplementedException()
			}
		}
	}
	
	def value = {attrs, body ->
		if (log.isDebugEnabled()) log.debug('value(attrs='+attrs+',body='+body+')')
		
		def type = attrs['type']
		def value = attrs['value']
		def format = attrs['format']
		def zero = attrs['zero']
		def enums = attrs['enums']
		def nullText = attrs['nullText']
		
		def result = null
		if (value != null && !value.isNull()) {
			result = languageService.getStringValue(value, type, enums, format, zero)
		}
		if (result == null && nullText != null) out << nullText
		else out << result
	}
	
}

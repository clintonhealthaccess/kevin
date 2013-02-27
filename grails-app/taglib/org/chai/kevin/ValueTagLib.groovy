package org.chai.kevin

import org.apache.commons.lang.NotImplementedException;

import java.text.DecimalFormat;

import org.chai.kevin.dsr.DsrTarget;

import org.chai.kevin.UtilTagLib;

import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type.ValueType;

import org.chai.kevin.data.Type;
import org.chai.kevin.util.DataUtils;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;

class ValueTagLib {

	def languageService
	
	def reportMapValue = { attrs, body ->
		def value = attrs['value']
		def type = attrs['type']
		def format = attrs['format']
		def rounded = attrs['rounded']
		def tooltip = attrs['tooltip'] as String
		
		if (value == null) {
			def reportValue = message(code: 'report.value.null')
			out << '<div class="report-value report-value-null"'+
					' data-report-value="'+reportValue+'"'+
					' data-report-value-raw="null"'+
					' data-report-value-type="null">'+
					(tooltip ? reportTooltip(tooltip, reportValue) : reportValue)+
					'</div>'
		}
		else if (value.isNull()) {
			def reportValue = message(code: 'report.value.null')
			out << '<div class="report-value report-value-is-null"'+
					' data-report-value="'+reportValue+'"'+
					' data-report-value-raw="null"'+
					' data-report-value-type="null">'+
					(tooltip ? reportTooltip(tooltip, reportValue) : reportValue)+
					'</div>'
		}
		else {
			switch (type.type) {
				case ValueType.BOOL:
					if (value.booleanValue){
						def reportValue = '&#10003;'
						out << '<div class="report-value report-value-true"'+
								' data-report-value="&#10003;"'+
								' data-report-value-raw="'+value.booleanValue+'"'+
								' data-report-value-type="'+type.type+'">'+
								(tooltip ? reportTooltip(tooltip, reportValue) : reportValue)+
								'</div>'
					}
					else{
						def reportValue = '&#10007;'
						out << '<div class="report-value report-value-false"'+
								' data-report-value="&#10007;"'+
								' data-report-value-raw="'+value.booleanValue+'"'+
								' data-report-value-type="'+type.type+'">'+
								(tooltip ? reportTooltip(tooltip, reportValue) : reportValue)+
								'</div>'
					}
					break;
				case ValueType.NUMBER:
					def reportValue = Utils.getStringValue(value, type, null, format, null, rounded as Integer)
					if (log.isDebugEnabled()) log.debug("valueTagLib.getMapValue(), value:"+value+", type:"+type+", format:"+format+", rounded:"+rounded+", reportValue:"+reportValue)
					out << '<div class="report-value"'+
							' data-report-value="'+reportValue+'"'+
							' data-report-value-raw="'+value.numberValue+'"'+
							' data-report-value-type="'+type.type+'">'+
							(tooltip ? reportTooltip(tooltip, reportValue) : reportValue)+
							'</div>'
					break;
				default:
					def reportValue = Utils.getStringValue(value, type, null, format, null, rounded as Integer)
					out << '<div class="report-value"'+
							' data-report-value="'+reportValue+'"'+
							' data-report-value-raw="'+reportValue+'"'+
							' data-report-value-type="'+type.type+'">'+
							(tooltip ? reportTooltip(tooltip, reportValue) : reportValue)+
							'</div>'
			}
		}
	}
	
	def reportBarData = { attrs, body ->
		def value = attrs['value']
		def type = attrs['type']
		def format = attrs['format']
		def rounded = attrs['rounded'] as Integer
		
		if (value == null || value.isNull()) {
			out << '<div class="report-value-null">'+message(code: 'report.value.null')+'</div>'
		}
		else {
			out << Utils.getStringValue(value, type, null, format, null, rounded)
		}
	}
	
	def reportBarTooltip = { attrs, body ->
		def percentage = attrs['percentage']
		def value = attrs['value']
		def totalLocations = attrs['totalLocations']
		
		out << message(code: 'fct.report.chart.tooltip.percentage')+': '+percentage+'<br />'+
				message(code: 'fct.report.chart.tooltip.value')+': '+value+'<br />'+
				message(code: 'fct.report.chart.tooltip.datalocations')+': '+totalLocations
	}
	
	/**
	 *
	 */
	def reportValue = { attrs, body ->
		def valueList = attrs['valueList']
		def value = attrs['value']
		def type = attrs['type']
		def format = attrs['format']
		def rounded = attrs['rounded'] as Integer
		def tooltip = attrs['tooltip'] as String
		
		if (value == null || value.isNull()) {
			def reportValue = message(code: 'report.value.null')+''
			out << '<div class="report-value-null">'+
					reportTooltip(tooltip, reportValue)+
				'</div>'
		}
		else {
			if (valueList != null) {
				def result = ''
				valueList = valueList.sort()
				for (Value listValue : valueList){
					result += getReportValue(listValue, type, format, rounded, tooltip)
					switch(type.listType){
						case ValueType.BOOL:
							result += result.join('&nbsp;&nbsp;&nbsp;')
							break;
						default:
							result += result.join(', ')
							break;
					}
				}

				out << result
			}
			else {
				out << getReportValue(value, type, format, rounded, tooltip)
			}
		}
	}
	
	def String getReportValue(def value, def type, def format, def rounded, def tooltip) {
		if (log.isDebugEnabled()) log.debug("valueTagLib.getReportValue(value="+value+", type="+type+")");
		def result = ''
		
		switch (type.type) {
		case ValueType.BOOL:
			if (value.booleanValue){
				result += '<div class="report-value-true">'+
						reportTooltip(tooltip, '&#10003;')+
						'</div>'
			}
			else {
				result += '<div class="report-value-false">'+
						reportTooltip(tooltip, '&#10007;')+
						'</div>'
			}
			break;
		default:
			def reportValue = Utils.getStringValue(value, type, null, format, null, rounded)
			result += reportTooltip(tooltip, reportValue)
		}
			
		return result
	}
	
	def String reportTooltip(String tooltip, String value){
		if(tooltip != null && tooltip != "null" && tooltip != ""){
			tooltip = tooltip+': '+value
			return '<span class="tooltip" original-title="'+tooltip+'">'+value+'</span>'
		}
		else
			return value;
	}
	
	def adminValue = {attrs, body ->
		def type = attrs['type']
		def value = attrs['value']
		
		def printableValue = new StringBuffer()
		prettyPrint(type, value, printableValue, 0)
		
		out << printableValue.toString()
	}
	
	//TODO explain what zero, enums, and nullText are
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
			result = Utils.getStringValue(value, type, enums, format, zero)
		}
		if (result == null && nullText != null) out << nullText
		else out << result
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
					printableValue.append  DataUtils.formatDate(value.dateValue)
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
	
}
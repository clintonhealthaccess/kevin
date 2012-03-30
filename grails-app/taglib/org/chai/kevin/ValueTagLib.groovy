package org.chai.kevin

import org.apache.commons.lang.NotImplementedException;

import java.text.DecimalFormat;

import org.chai.kevin.dsr.DsrTarget;

import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type.ValueType;

import org.chai.kevin.data.Type;
import org.chai.kevin.value.Value;

class ValueTagLib {

	def dataService
	def languageService
	
	def reportValue = { attrs, body ->
		def type = attrs['type']
		def value = attrs['value']
		def format = attrs['format']
		
		if (value.isNull()) {
			out << '<div class="report-value-null">'+message(code: 'report.value.null')+'</div>'
		}
		else {
			switch (type.type) {
				case ValueType.BOOL:
					if (value.booleanValue) out << '<div class="report-value-true">&#10003;</div>'
					else out << '<div class="report-value-false">&#10007;</div>'
					break;
				case ValueType.STRING:
				case ValueType.TEXT:
					out << value.stringValue
					break;
				case ValueType.NUMBER:
					out << formatNumber(format, value.numberValue)
					break;
				case ValueType.ENUM:
					String output = null
					Enum enume = dataService.findEnumByCode(type.enumCode);
					if (enume != null) {
						EnumOption option = enume.getOptionForValue(value.enumValue);
						if (option != null) output = languageService.getText(option.getNames());
					}
					if (output == null) output = value.enumValue
					out << output
					break;
				case ValueType.DATE:
					// TODO
					break;
				default:
					throw new NotImplementedException()
			}
		}
	}
	
	def formatNumber(String format, Number value) {
		if (format == null) format = "#";
		
		DecimalFormat frmt = new DecimalFormat(format);
		return frmt.format(value.doubleValue()).toString();
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
		def enums = attrs['enums']
		def nullText = attrs['nullText']
		
		def result = null
		if (value != null && !value.isNull()) {
			switch (type.type) {
				case (ValueType.ENUM):
					def enume = enums?.get(type.enumCode)
					if (enume == null) result = value.enumValue
					else {
						def option = enume?.getOptionForValue(value.enumValue)
						if (option == null) result = value.enumValue
						else result = languageService.getText(option.names)
					}
					break;
				case (ValueType.MAP):
					// TODO
				case (ValueType.LIST):
					// TODO
				default:
					result = value.stringValue
			}
		}
		if (result == null && nullText != null) out << nullText
		else out << result
	}
	
}

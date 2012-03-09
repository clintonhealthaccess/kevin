package org.chai.kevin.data

import org.chai.kevin.util.Utils;

import grails.plugin.spock.UnitSpec;

class DataElementUnitSpec extends UnitSpec {

	def "get header prefixes"() {
		def dataElement = null
		def element = null
		def prefixes = null
		
		when:
		dataElement = new RawDataElement(type: Type.TYPE_MAP(["key1": Type.TYPE_MAP(["key11": Type.TYPE_NUMBER()])]))
		prefixes = dataElement.getHeaderPrefixes()
		
		then:
		prefixes.equals([".key1", ".key1.key11"])
		
		when:
		dataElement = new RawDataElement(type: Type.TYPE_LIST(Type.TYPE_MAP(["key1": Type.TYPE_MAP(["key11": Type.TYPE_NUMBER()])])))
		prefixes = dataElement.getHeaderPrefixes()
		
		then:
		prefixes.equals(["[_].key1", "[_].key1.key11"])
	}
	
	def "needs refresh works for normalized data element"() {
		setup:
		def normalizedDataElement = null
		
		when:
		normalizedDataElement = new NormalizedDataElement(timestamp: Utils.parseDate("01-01-2000"))
		
		then:
		normalizedDataElement.needsRefresh() == true
		
		when:
		normalizedDataElement = new NormalizedDataElement(timestamp: Utils.parseDate("01-01-2000"), calculated: Utils.parseDate("02-01-2000"))
		
		then:
		normalizedDataElement.needsRefresh() == false
		
		when:
		normalizedDataElement = new NormalizedDataElement(timestamp: Utils.parseDate("02-01-2000"), calculated: Utils.parseDate("01-01-2000"))
		
		then:
		normalizedDataElement.needsRefresh() == true
		
	}
	
	def "needs refresh works for calculation"() {
		setup:
		def average = null
		
		when:
		average = new Average(timestamp: Utils.parseDate("01-01-2000"))
		
		then:
		average.needsRefresh() == true
		
		when:
		average = new Average(timestamp: Utils.parseDate("01-01-2000"), calculated: Utils.parseDate("02-01-2000"))
		
		then:
		average.needsRefresh() == false
		
		when:
		average = new Average(timestamp: Utils.parseDate("02-01-2000"), calculated: Utils.parseDate("01-01-2000"))
		
		then:
		average.needsRefresh() == true
		
	}
	
}

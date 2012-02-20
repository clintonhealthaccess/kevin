package org.chai.kevin.data

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
	
}

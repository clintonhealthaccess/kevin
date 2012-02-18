package org.chai.kevin.location

import grails.plugin.spock.UnitSpec;

class LocationEntitySpec extends UnitSpec {

	def "get data entities for location"() {
		when:
		def rwanda = new LocationEntity(code: "rwanda")
		def north = new LocationEntity(code: "north", parent: rwanda)
		def burera = new LocationEntity(code: "burera", parent: north)		
		rwanda.children = [north]
		north.children = [burera]

		then:
		rwanda.collectTreeWithDataEntities(null, null).empty
		rwanda.collectDataLocationEntities(null, null).empty
		
		when:
		def type1 = new DataEntityType(code: 'type1');
		def data1 = new DataLocationEntity(code: 'data1', location: north, type: type1);
		north.dataEntities = [data1]
		
		then:
		rwanda.collectTreeWithDataEntities(null, null).equals([north, rwanda])
		rwanda.collectDataLocationEntities(null, null).equals([data1])
		rwanda.collectTreeWithDataEntities(new HashSet([type1]), null).equals([north, rwanda])
		rwanda.collectDataLocationEntities(new HashSet([type1]), null).equals([data1])
		
		when:
		def type2 = new DataEntityType(code: 'type2')
		
		then:
		rwanda.collectTreeWithDataEntities(new HashSet([type2]), null).empty
		rwanda.collectDataLocationEntities(new HashSet([type2]), null).empty
	}
}

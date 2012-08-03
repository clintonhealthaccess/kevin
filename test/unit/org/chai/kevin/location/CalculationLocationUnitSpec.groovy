package org.chai.kevin.location

import grails.plugin.spock.UnitSpec;

class CalculationLocationUnitSpec extends UnitSpec {

	def "get data entities on location location"() {
		when:
		def rwanda = new Location(code: "rwanda")
		def north = new Location(code: "north", parent: rwanda)
		def burera = new Location(code: "burera", parent: north)
		rwanda.children = [north]
		north.children = [burera]
		
		def data1 = new DataLocation(code: 'data1', location: north);
		north.dataLocations = [data1]
		def data2 = new DataLocation(code: 'data2', location: burera);
		burera.dataLocations = [data2]
		
		then:
//		rwanda.getDataLocationChildren().empty
		rwanda.getDataLocationChildren(null, null).empty
//		north.getDataLocationChildren().equals([data1])
		north.getDataLocationChildren(null, null).equals([data1])
//		burera.getDataLocationChildren().equals([data2])
		burera.getDataLocationChildren(null, null).equals([data2])
	}

	def "get data entities on location entity with skip"() {
		setup:
		def country = new LocationLevel(code: "country")
		def province = new LocationLevel(code: "province")
		def district = new LocationLevel(code: "district")
		
		when:
		def rwanda = new Location(code: "rwanda", level: country)
		def north = new Location(code: "north", parent: rwanda, level: province)
		def burera = new Location(code: "burera", parent: north, level: district)
		rwanda.children = [north]
		north.children = [burera]
		
		def data1 = new DataLocation(code: 'data1', location: north);
		north.dataLocations = [data1]
		def data2 = new DataLocation(code: 'data2', location: burera);
		burera.dataLocations = [data2]
		
		then:
		rwanda.getDataLocationChildren(new HashSet([province]), null).equals([data1])
		rwanda.getDataLocationChildren(new HashSet([province,district]), null).equals([data1, data2])
		north.getDataLocationChildren(new HashSet([district]), null).equals([data1, data2])
	}
		
	def "collect data entities on location entity with specific type"() {
		when:
		def rwanda = new Location(code: "rwanda")
		def north = new Location(code: "north", parent: rwanda)
		def burera = new Location(code: "burera", parent: north)		
		rwanda.children = [north]
		north.children = [burera]

		then:
		rwanda.collectLocationTreeWithData(null, null, true).empty
		rwanda.collectDataLocations(null, null).empty
		
		when:
		def type1 = new DataLocationType(code: 'type1');
		def data1 = new DataLocation(code: 'data1', location: north, type: type1);
		north.dataLocations = [data1]
		
		then:
		rwanda.collectLocationTreeWithData(null, null, true).equals([north, rwanda, data1])
		rwanda.collectDataLocations(null, null).equals([data1])
		rwanda.collectLocationTreeWithData(null, new HashSet([type1]), true).equals([north, rwanda, data1])
		rwanda.collectDataLocations(null, new HashSet([type1])).equals([data1])
		
		when:
		def type2 = new DataLocationType(code: 'type2')

		then:
		rwanda.collectLocationTreeWithData(null, new HashSet([type2]), true).empty
		rwanda.collectDataLocations(null, new HashSet([type2])).empty
		
	}
	
	def "collect data entities on location at different levels"() {
		when:
		def rwanda = new Location(code: "rwanda")
		def north = new Location(code: "north", parent: rwanda)
		def burera = new Location(code: "burera", parent: north)
		rwanda.children = [north]
		north.children = [burera]
		
		def data1 = new DataLocation(code: 'data1', location: north);
		north.dataLocations = [data1]
		def data2 = new DataLocation(code: 'data2', location: burera);
		burera.dataLocations = [data2]
		
		then:
		rwanda.collectDataLocations(null, null).equals([data2, data1])
	}
	
	def "collect data entities for on data location entity"() {
		when:
		def type1 = new DataLocationType(code: 'type1');
		def data1 = new DataLocation(code: 'data1', type: type1);
		
		then:
		data1.collectDataLocations(null, new HashSet([type1])).equals([data1])
		
		when:
		def type2 = new DataLocationType(code: 'type2');
		
		then:
		data1.collectDataLocations(null, new HashSet([type2])).empty
	}
	
	def "get data entities on data entitiy"() {
		when:
		def data1 = new DataLocation(code: 'data1');
		
		then:
		data1.getDataLocationChildren(null, null).equals([data1])
	}
	
	def "get data entities of type on data entity"() {
		when:
		def type1 = new DataLocationType(code: 'type1');
		def data1 = new DataLocation(code: 'data1', type: type1);
		
		then:
		data1.getDataLocationChildren(null, new HashSet([type1])).equals([data1])
		
		when:
		def type2 = new DataLocationType(code: 'type2');
		
		then:
		data1.getDataLocationChildren(null, new HashSet([type2])).empty
	}
	
	def "test get children with skip on location entity"() {
		setup:
		def country = new LocationLevel(code: "country")
		def province = new LocationLevel(code: "province")
		def district = new LocationLevel(code: "district")
		
		when:
		def rwanda = new Location(code: "rwanda", level: country)
		def north = new Location(code: "north", parent: rwanda, level: province)
		def burera = new Location(code: "burera", parent: north, level: district)
		rwanda.children = [north]
		north.children = [burera]
		
		then:
		rwanda.getAllChildren(new HashSet([province]), null).equals([burera])
		rwanda.getAllChildren(new HashSet([province, district]), null).empty
	}
	
	def "test get data entities with skip on location entity"() {
		setup:
		def country = new LocationLevel(code: "country")
		def province = new LocationLevel(code: "province")
		def district = new LocationLevel(code: "district")
		
		when:
		def rwanda = new Location(code: "rwanda", level: country)
		def north = new Location(code: "north", parent: rwanda, level: province)
		def burera = new Location(code: "burera", parent: north, level: district)
		rwanda.children = [north]
		north.children = [burera]
		
		def dataDistrict = new DataLocation(code: 'data1', location: burera);
		burera.dataLocations = [dataDistrict]
		
		then:
		rwanda.getDataLocationChildren(new HashSet([province]), null).empty
		rwanda.getDataLocationChildren(new HashSet([province, district]), null).equals([dataDistrict])
		north.getDataLocationChildren(new HashSet([province]), null).empty
		
		when:
		def dataProvince = new DataLocation(code: 'data2', location: north);
		north.dataLocations = [dataProvince]
		
		then:
		rwanda.getDataLocationChildren(new HashSet([province]), null).equals([dataProvince])
		rwanda.getDataLocationChildren(new HashSet([province, district]), null).equals([dataProvince, dataDistrict])
		north.getDataLocationChildren(new HashSet([province]), null).equals([dataProvince])
		
		when:
		def dataCountry = new DataLocation(code: 'data3', location: rwanda)
		rwanda.dataLocations = [dataCountry]
		
		then:
		rwanda.getDataLocationChildren(new HashSet([province]), null).equals([dataCountry, dataProvince])
		rwanda.getDataLocationChildren(new HashSet([province, district]), null).equals([dataCountry, dataProvince, dataDistrict])
		north.getDataLocationChildren(new HashSet([province]), null).equals([dataProvince])
	}
	
	def "test get children entity with skip"() {
		setup:
		def country = new LocationLevel(code: "country")
		def province = new LocationLevel(code: "province")
		def district = new LocationLevel(code: "district")
		def type1 = new DataLocationType(code: 'type1')
		def type2 = new DataLocationType(code: 'type2')
		
		when:
		def rwanda = new Location(code: "rwanda", level: country)
		def north = new Location(code: "north", parent: rwanda, level: province)
		def burera = new Location(code: "burera", parent: north, level: district)
		rwanda.children = [north]
		north.children = [burera]
		
		def data1 = new DataLocation(code: 'data1', location: north, type: type1)
		north.dataLocations = [data1]
		
		def data2 = new DataLocation(code: 'data2', location: burera, type: type2)
		burera.dataLocations = [data2]
		
		then:
		rwanda.getAllChildren(new HashSet([province]), new HashSet([type1, type2])).equals([burera, data1])
		rwanda.getAllChildren(new HashSet([province, district]), new HashSet([type1, type2])).equals([data1, data2])
		
	}
	
	def "test get children entities with data locations"() {
		setup:
		def country = new LocationLevel(code: "country")
		def province = new LocationLevel(code: "province")
		def district = new LocationLevel(code: "district")
		def type1 = new DataLocationType(code: 'type1')
		def type2 = new DataLocationType(code: 'type2')
		def types = new HashSet([type1, type2])
		
		when:
		def rwanda = new Location(code: "rwanda", level: country)
		def south = new Location(code: "south", parent: rwanda, level: province)
		def north = new Location(code: "north", parent: rwanda, level: province)
		def burera = new Location(code: "burera", parent: north, level: district)
		
		def data1 = new DataLocation(code: 'data1', location: rwanda, type: type1)
		def data2 = new DataLocation(code: 'data2', location: burera, type: type2)
		
		rwanda.children = [north, south]
		rwanda.dataLocations = [data1]
		north.children = [burera]
		burera.dataLocations = [data2]
		
		then:
		rwanda.getChildrenWithData(null, types, true).equals([north, data1])
		rwanda.getChildrenWithData(new HashSet([province]), types, true).equals([burera, data1])
		rwanda.getChildrenWithData(new HashSet([province, district]), types, true).equals([data1, data2])
	}
	
	def "test collect locations on DataLocation"() {
		setup:
		def rwanda = new Location(code: "rwanda")
		def type1 = new DataLocationType(code: 'type1')
		def data1 = new DataLocation(code: 'data1', location: rwanda, type: type1);
		rwanda.dataLocations = [data1]
		def locations = [] 
		def dataLocations = []
		
		when:
		data1.collectLocations(locations, dataLocations, null, new HashSet([type1]))
		
		then:
		dataLocations == [data1]
	}
	
}

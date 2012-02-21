package org.chai.kevin.location

import grails.plugin.spock.UnitSpec;

class CalculationEntityUnitSpec extends UnitSpec {

	def "get data entities on location entity"() {
		when:
		def rwanda = new LocationEntity(code: "rwanda")
		def north = new LocationEntity(code: "north", parent: rwanda)
		def burera = new LocationEntity(code: "burera", parent: north)
		rwanda.children = [north]
		north.children = [burera]
		
		def data1 = new DataLocationEntity(code: 'data1', location: north);
		north.dataEntities = [data1]
		def data2 = new DataLocationEntity(code: 'data2', location: burera);
		burera.dataEntities = [data2]
		
		then:
		rwanda.getDataEntities().empty
		rwanda.getDataEntities(null, null).empty
		north.getDataEntities().equals([data1])
		north.getDataEntities(null, null).equals([data1])
		burera.getDataEntities().equals([data2])
		burera.getDataEntities(null, null).equals([data2])
	}

	def "get data entities on location entity with skip"() {
		setup:
		def country = new LocationLevel(code: "country")
		def province = new LocationLevel(code: "province")
		def district = new LocationLevel(code: "district")
		
		when:
		def rwanda = new LocationEntity(code: "rwanda", level: country)
		def north = new LocationEntity(code: "north", parent: rwanda, level: province)
		def burera = new LocationEntity(code: "burera", parent: north, level: district)
		rwanda.children = [north]
		north.children = [burera]
		
		def data1 = new DataLocationEntity(code: 'data1', location: north);
		north.dataEntities = [data1]
		def data2 = new DataLocationEntity(code: 'data2', location: burera);
		burera.dataEntities = [data2]
		
		then:
		rwanda.getDataEntities(new HashSet([province]), null).equals([data1])
		rwanda.getDataEntities(new HashSet([province,district]), null).equals([data1, data2])
		north.getDataEntities(new HashSet([district]), null).equals([data1, data2])
	}
		
	def "collect data entities on location entity with specific type"() {
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
	
	def "collect data entities on location at different levels"() {
		when:
		def rwanda = new LocationEntity(code: "rwanda")
		def north = new LocationEntity(code: "north", parent: rwanda)
		def burera = new LocationEntity(code: "burera", parent: north)
		rwanda.children = [north]
		north.children = [burera]
		
		def data1 = new DataLocationEntity(code: 'data1', location: north);
		north.dataEntities = [data1]
		def data2 = new DataLocationEntity(code: 'data2', location: burera);
		burera.dataEntities = [data2]
		
		then:
		rwanda.collectDataLocationEntities(null, null).equals([data2, data1])
	}
	
	def "collect data entities for on data location entity"() {
		when:
		def type1 = new DataEntityType(code: 'type1');
		def data1 = new DataLocationEntity(code: 'data1', type: type1);
		
		then:
		data1.collectDataLocationEntities(new HashSet([type1]), null).equals([data1])
		
		when:
		def type2 = new DataEntityType(code: 'type2');
		
		then:
		data1.collectDataLocationEntities(new HashSet([type2]), null).empty
	}
	
	def "get data entities on data entitiy"() {
		when:
		def data1 = new DataLocationEntity(code: 'data1');
		
		then:
		data1.getDataEntities(null, null).equals([data1])
	}
	
	def "get data entities of type on data entity"() {
		when:
		def type1 = new DataEntityType(code: 'type1');
		def data1 = new DataLocationEntity(code: 'data1', type: type1);
		
		then:
		data1.getDataEntities(null, new HashSet([type1])).equals([data1])
		
		when:
		def type2 = new DataEntityType(code: 'type2');
		
		then:
		data1.getDataEntities(null, new HashSet([type2])).empty
	}
	
	def "test get children with skip on location entity"() {
		setup:
		def country = new LocationLevel(code: "country")
		def province = new LocationLevel(code: "province")
		def district = new LocationLevel(code: "district")
		
		when:
		def rwanda = new LocationEntity(code: "rwanda", level: country)
		def north = new LocationEntity(code: "north", parent: rwanda, level: province)
		def burera = new LocationEntity(code: "burera", parent: north, level: district)
		rwanda.children = [north]
		north.children = [burera]
		
		then:
		rwanda.getChildren(new HashSet([province])).equals([burera])
		rwanda.getChildren(new HashSet([province, district])).empty
	}
	
	def "test get data entities with skip on location entity"() {
		setup:
		def country = new LocationLevel(code: "country")
		def province = new LocationLevel(code: "province")
		def district = new LocationLevel(code: "district")
		
		when:
		def rwanda = new LocationEntity(code: "rwanda", level: country)
		def north = new LocationEntity(code: "north", parent: rwanda, level: province)
		def burera = new LocationEntity(code: "burera", parent: north, level: district)
		rwanda.children = [north]
		north.children = [burera]
		
		def dataDistrict = new DataLocationEntity(code: 'data1', location: burera);
		burera.dataEntities = [dataDistrict]
		
		then:
		rwanda.getDataEntities(new HashSet([province]), null).empty
		rwanda.getDataEntities(new HashSet([province, district]), null).equals([dataDistrict])
		north.getDataEntities(new HashSet([province]), null).empty
		
		when:
		def dataProvince = new DataLocationEntity(code: 'data2', location: north);
		north.dataEntities = [dataProvince]
		
		then:
		rwanda.getDataEntities(new HashSet([province]), null).equals([dataProvince])
		rwanda.getDataEntities(new HashSet([province, district]), null).equals([dataProvince, dataDistrict])
		north.getDataEntities(new HashSet([province]), null).equals([dataProvince])
		
		when:
		def dataCountry = new DataLocationEntity(code: 'data3', location: rwanda)
		rwanda.dataEntities = [dataCountry]
		
		then:
		rwanda.getDataEntities(new HashSet([province]), null).equals([dataCountry, dataProvince])
		rwanda.getDataEntities(new HashSet([province, district]), null).equals([dataCountry, dataProvince, dataDistrict])
		north.getDataEntities(new HashSet([province]), null).equals([dataProvince])
	}
}

package org.chai.kevin.location

import grails.validation.ValidationException;

import org.chai.kevin.IntegrationTests;

class LocationSpec extends IntegrationTests {
	
	def "type cannot be null"() {
		setup:
		setupLocationTree()
		
		when:
		new DataLocation(names:j([:]), code: CODE(1), type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		DataLocation.count() == 3
		
		when:
		new DataLocation(names:j([:]), code: CODE(2), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}

//	def "location cannot be null"() {
//		setup:
//		setupLocationTree()
//		
//		when:
//		new DataLocation(names:j([:]), code: CODE(1), type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
//		
//		then:
//		DataLocation.count() == 3
//		
//		when:
//		new DataLocation(names:j([:]), code: CODE(2), type: DataLocationType.findByCode(HEALTH_CENTER_GROUP)).save(failOnError: true)
//		
//		then:
//		thrown ValidationException
//	}
	
	def "code cannot be null"() {
		setup:
		setupLocationTree()
		
		when:
		new DataLocation(names:j([:]), code: CODE(1), type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		DataLocation.count() == 3
		
		when:
		new DataLocation(names:j([:]), type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "code cannot be empty"() {
		setup:
		setupLocationTree()
		
		when:
		new DataLocation(names:j([:]), code: CODE(1), type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		DataLocation.count() == 3
		
		when:
		new DataLocation(names:j([:]), code: "", type: DataLocationType.findByCode(HEALTH_CENTER_GROUP), location: Location.findByCode(BURERA)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "data location type code cannot contain delimiter"() {
		when:
		new DataLocationType(code: CODE(1)).save(failOnError: true)
		
		then:
		DataLocationType.count() == 1
		
		when:
		new DataLocationType(code: CODE(1)+DataLocationType.DEFAULT_CODE_DELIMITER).save(failOnError: true)
		
		then:
		thrown ValidationException
		
	}
	
	def "get children with data"(){
		setup:
		setupLocationTree()
		def skipLevels = new HashSet([LocationLevel.findByCode(SECTOR)])
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def newDataLocation = newDataLocation(j(["en":"BLAH"]), "BLAH", Location.findByCode(NORTH), DataLocationType.findByCode(HEALTH_CENTER_GROUP))
		
		when: //with data locations
		def children = Location.findByCode(NORTH).getChildrenWithData(skipLevels, types, true)
		
		then:
		children.equals([Location.findByCode(BURERA), DataLocation.findByCode("BLAH")])
		
		when: //without data locations
		children = Location.findByCode(NORTH).getChildrenWithData(skipLevels, types, false)
		
		then:
		children.equals([Location.findByCode(BURERA)])
	}
	
	def "get location tree with data"(){
		setup:
		setupLocationTree()
		def skipLevels = new HashSet([LocationLevel.findByCode(SECTOR)])
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		
		when: //with data locations
		def children = Location.findByCode(NORTH).collectLocationTreeWithData(skipLevels, types, true)
		
		then:
		children.equals([Location.findByCode(NORTH), Location.findByCode(BURERA), DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)])
		
		when: //without data locations
		children = Location.findByCode(NORTH).collectLocationTreeWithData(skipLevels, types, false)
		
		then:
		children.equals([Location.findByCode(NORTH), Location.findByCode(BURERA)])
	}
}

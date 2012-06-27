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
	
}

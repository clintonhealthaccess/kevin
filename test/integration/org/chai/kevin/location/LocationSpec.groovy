package org.chai.kevin.location

import grails.validation.ValidationException;

import org.chai.kevin.IntegrationTests;

class LocationSpec extends IntegrationTests {
	
	def "type cannot be null"() {
		setup:
		setupLocationTree()
		
		when:
		new DataLocationEntity(names:j([:]), code: CODE(1), type: DataEntityType.findByCode(HEALTH_CENTER_GROUP), location: LocationEntity.findByCode(BURERA)).save(failOnError: true)
		
		then:
		DataLocationEntity.count() == 3
		
		when:
		new DataLocationEntity(names:j([:]), code: CODE(2), location: LocationEntity.findByCode(BURERA)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}

//	def "location cannot be null"() {
//		setup:
//		setupLocationTree()
//		
//		when:
//		new DataLocationEntity(names:j([:]), code: CODE(1), type: DataEntityType.findByCode(HEALTH_CENTER_GROUP), location: LocationEntity.findByCode(BURERA)).save(failOnError: true)
//		
//		then:
//		DataLocationEntity.count() == 3
//		
//		when:
//		new DataLocationEntity(names:j([:]), code: CODE(2), type: DataEntityType.findByCode(HEALTH_CENTER_GROUP)).save(failOnError: true)
//		
//		then:
//		thrown ValidationException
//	}
	
	def "code cannot be null"() {
		setup:
		setupLocationTree()
		
		when:
		new DataLocationEntity(names:j([:]), code: CODE(1), type: DataEntityType.findByCode(HEALTH_CENTER_GROUP), location: LocationEntity.findByCode(BURERA)).save(failOnError: true)
		
		then:
		DataLocationEntity.count() == 3
		
		when:
		new DataLocationEntity(names:j([:]), type: DataEntityType.findByCode(HEALTH_CENTER_GROUP), location: LocationEntity.findByCode(BURERA)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "code cannot be empty"() {
		setup:
		setupLocationTree()
		
		when:
		new DataLocationEntity(names:j([:]), code: CODE(1), type: DataEntityType.findByCode(HEALTH_CENTER_GROUP), location: LocationEntity.findByCode(BURERA)).save(failOnError: true)
		
		then:
		DataLocationEntity.count() == 3
		
		when:
		new DataLocationEntity(names:j([:]), code: "", type: DataEntityType.findByCode(HEALTH_CENTER_GROUP), location: LocationEntity.findByCode(BURERA)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
}

package org.chai.kevin.planning

import org.chai.kevin.data.Type;

import grails.validation.ValidationException;


class PlanningDomainSpec extends PlanningIntegrationTests {

	def "period cannot be null in planning"() {
		setup:
		def period = newPeriod()
		
		when:
		new Planning(period: period).save(failOnError: true)
		
		then:
		Planning.count() == 1
		
		when:
		new Planning().save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "null constraints in planning type"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period)
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_NUMBER()])))
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key', dataElement: dataElement).save(failOnError: true)
		
		then:
		PlanningType.count() == 1
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key').save(failOnError: true)

		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, dataElement: dataElement).save(failOnError: true)

		then:
		thrown ValidationException

		when:
		new PlanningType(discriminator: '[_].key', dataElement: dataElement).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "discriminator must be a prefix of type"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period)
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_NUMBER()])))
		
		when:
		new PlanningType(planning: planning, discriminator: '[_]', dataElement: dataElement).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key2', dataElement: dataElement).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key', dataElement: dataElement).save(failOnError: true)
		
		then:
		PlanningType.count() == 1
	}
	
}
